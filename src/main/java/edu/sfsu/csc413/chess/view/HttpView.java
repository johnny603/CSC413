package edu.sfsu.csc413.chess.view;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * A board in the browser, served with the JDK's HTTP server and nothing else.
 *
 * <p>{@link #start} binds {@code 127.0.0.1} and returns. It does not call
 * {@link GameController#runLoop()}. A click is a GET. The selected square
 * lives in this object; the page asks {@link Game#legalMoves()} which squares
 * to mark and {@link Game#status()} whether the side to move is in check.
 */
public class HttpView implements BoardView {

    private static final int FIRST_PORT = 8765;

    private final Object lock = new Object();
    private GameController controller;
    private Game game;
    /** Last message from the controller. */
    private String message = "";
    /** A hint this view invented, such as "select a piece". Cleared on render. */
    private String hint = "";
    private boolean messageFresh;
    private Position selected;
    private HttpServer server;
    private int port;

    @Override
    public void render(Game game) {
        this.game = game;
        selected = null;
        hint = "";
        if (!messageFresh) {
            message = "";
        }
        messageFresh = false;
    }

    @Override
    public void showMessage(String message) {
        this.message = message == null ? "" : message;
        this.hint = "";
        messageFresh = true;
    }

    @Override
    public void start(GameController controller) {
        this.controller = controller;
        IOException last = null;
        for (int candidate = FIRST_PORT; candidate < FIRST_PORT + 10; candidate++) {
            try {
                server = HttpServer.create(new InetSocketAddress("127.0.0.1", candidate), 0);
                port = candidate;
                break;
            } catch (IOException e) {
                last = e;
            }
        }
        if (server == null) {
            throw new UncheckedIOException("Could not bind a local port for the board", last);
        }
        server.createContext("/", this::serve);
        server.start();
        System.out.println("Open http://127.0.0.1:" + port + "/");
    }

    @Override
    public void close() {
        HttpServer running = server;
        server = null;
        if (running == null) {
            return;
        }
        // handle() calls close() before the response is written. Stop after
        // that write has a chance to leave.
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            running.stop(0);
        }, "http-view-stop");
        stopper.setDaemon(false);
        stopper.start();
    }

    private void serve(HttpExchange exchange) throws IOException {
        synchronized (lock) {
            String path = exchange.getRequestURI().getPath();
            if (controller != null && game != null) {
                if (path.startsWith("/square/")) {
                    onSquare(path.substring("/square/".length()));
                } else if ("/undo".equals(path)) {
                    controller.handle(new PlayerAction.Undo());
                } else if ("/moves".equals(path)) {
                    controller.handle(new PlayerAction.ShowMoves());
                } else if ("/quit".equals(path)) {
                    controller.handle(new PlayerAction.Quit());
                }
            }
            byte[] body = page().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        }
    }

    private void onSquare(String algebraic) {
        if (algebraic.length() != 2 || game.isGameOver()) {
            return;
        }
        Position clicked;
        try {
            clicked = Position.parse(algebraic);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        Piece piece = game.board().pieceAt(clicked);
        if (selected == null) {
            if (piece != null && piece.color() == game.sideToMove()) {
                selected = clicked;
            } else {
                hint = "Select a " + game.sideToMove() + " piece.";
            }
            return;
        }
        if (selected.equals(clicked)) {
            selected = null;
            return;
        }
        if (piece != null && piece.color() == game.sideToMove()) {
            selected = clicked;
            return;
        }
        String notation = selected.toString() + clicked.toString();
        selected = null;
        controller.handle(new PlayerAction.Move(notation));
    }

    private String page() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\">");
        html.append("<title>CSC 413 Chess</title><style>");
        html.append("body{font-family:sans-serif;background:#f4f1ea;color:#241c14;}");
        html.append("table{border-collapse:collapse;}");
        html.append("td a{display:flex;align-items:center;justify-content:center;");
        html.append("width:68px;height:68px;font-size:40px;text-decoration:none;}");
        html.append("td.light{background:#f0d9b5;}td.dark{background:#b58863;}");
        html.append("td.selected{outline:4px solid #e2c200;outline-offset:-4px;}");
        html.append("td.legal{box-shadow:inset 0 0 0 5px rgba(20,90,30,.85);}");
        html.append(".w{color:#fffdf8;text-shadow:0 0 2px #000,1px 1px 0 #000,-1px -1px 0 #000;}");
        html.append(".b{color:#1a120c;text-shadow:0 0 2px #fff,1px 1px 0 #fff,-1px -1px 0 #fff;}");
        html.append("nav a{margin-right:1rem;} .msg{min-height:1.4em;font-weight:600;}");
        html.append("</style></head><body>");
        if (game == null) {
            html.append("<p>Waiting for a game.</p></body></html>");
            return html.toString();
        }
        html.append("<h1>").append(esc(game.sideToMove().toString())).append(" to move</h1>");
        html.append("<p>").append(esc(words(game.status()))).append("</p>");
        String shown = !hint.isBlank() ? hint : message;
        html.append("<p class=\"msg\">").append(esc(shown)).append("</p>");
        html.append("<table>");
        Set<Position> targets = legalTargets();
        for (int row = 0; row < Position.BOARD_SIZE; row++) {
            int rank = Position.BOARD_SIZE - 1 - row;
            html.append("<tr>");
            for (int file = 0; file < Position.BOARD_SIZE; file++) {
                Position square = new Position(file, rank);
                boolean light = (file + rank) % 2 != 0;
                String cls = light ? "light" : "dark";
                if (square.equals(selected)) {
                    cls += " selected";
                }
                if (targets.contains(square)) {
                    cls += " legal";
                }
                Piece piece = game.board().pieceAt(square);
                String glyph = PieceGlyphs.FIGURES.glyphFor(piece);
                String tone = "";
                if (piece != null) {
                    tone = piece.color() == Color.WHITE ? " class=\"w\"" : " class=\"b\"";
                }
                html.append("<td class=\"").append(cls).append("\">");
                html.append("<a href=\"/square/").append(square).append("\" aria-label=\"")
                        .append(square).append("\"").append(tone).append(">");
                html.append(glyph).append("</a></td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<nav><a href=\"/undo\">Undo</a><a href=\"/moves\">Moves</a>");
        html.append("<a href=\"/quit\">Quit</a></nav>");
        html.append("</body></html>");
        return html.toString();
    }

    private Set<Position> legalTargets() {
        Set<Position> targets = new HashSet<>();
        if (game == null || selected == null) {
            return targets;
        }
        for (Move move : game.legalMoves()) {
            if (move.from().equals(selected)) {
                targets.add(move.to());
            }
        }
        return targets;
    }

    private static String words(GameStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "In progress";
            case CHECK -> "Check";
            case CHECKMATE -> "Checkmate";
            case STALEMATE -> "Stalemate";
        };
    }

    private static String esc(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
