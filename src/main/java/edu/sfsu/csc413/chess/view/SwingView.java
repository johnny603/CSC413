package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * A window you play by clicking a piece, then the square it should move to.
 *
 * <p>This view never asks to be polled. {@link #start} builds the window and
 * returns; clicks call {@link GameController#handle} on Swing's thread.
 * Legality is read from {@link Game#legalMoves()}, and check, checkmate, and
 * stalemate are read from {@link Game#status()}. The half-finished click
 * (which square is selected) lives only here.
 */
public class SwingView implements BoardView {

    /** Left and top margin reserved for file and rank labels. */
    static final int ORIGIN = 28;

    /** Side length of one square, in pixels. */
    static final int SQUARE = 72;

    private static final java.awt.Color LIGHT = new java.awt.Color(240, 217, 181);
    private static final java.awt.Color DARK = new java.awt.Color(181, 136, 99);
    private static final java.awt.Color SELECTED = new java.awt.Color(246, 220, 70, 170);
    private static final java.awt.Color LEGAL = new java.awt.Color(30, 90, 40, 150);
    private static final java.awt.Color LABEL = new java.awt.Color(40, 32, 24);
    private static final java.awt.Color IVORY = new java.awt.Color(255, 250, 240);
    private static final java.awt.Color INK = new java.awt.Color(28, 24, 20);

    private GameController controller;
    private Game game;
    /** Last message from the controller. */
    private String message = "";
    /** A hint this view invented, such as "select a piece". Cleared on render. */
    private String hint = "";
    /** True after {@link #showMessage} until the next {@link #render}. */
    private boolean messageFresh;
    /** The square the player clicked first, or null if they have not. */
    private Position selected;
    private JFrame frame;
    private BoardPanel board;
    private JLabel statusLabel;
    private JLabel messageLabel;
    private boolean closed;

    @Override
    public void render(Game game) {
        this.game = game;
        // A new position means the previous click is no longer a pending move.
        selected = null;
        hint = "";
        if (!messageFresh) {
            message = "";
        }
        messageFresh = false;
        refresh();
    }

    @Override
    public void showMessage(String message) {
        this.message = message == null ? "" : message;
        this.hint = "";
        messageFresh = true;
        refresh();
    }

    /**
     * Builds the window on the Swing thread and returns at once. It does not
     * call {@link GameController#runLoop()}.
     */
    @Override
    public void start(GameController controller) {
        this.controller = controller;
        SwingUtilities.invokeLater(this::openFrame);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        Runnable dispose = () -> {
            if (frame != null) {
                frame.dispose();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            dispose.run();
        } else {
            SwingUtilities.invokeLater(dispose);
        }
    }

    private void openFrame() {
        frame = new JFrame("CSC 413 Chess");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (!closed && controller != null) {
                    controller.handle(new PlayerAction.Quit());
                }
            }
        });

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));
        messageLabel = new JLabel(" ");
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel header = new JPanel(new BorderLayout());
        header.add(statusLabel, BorderLayout.NORTH);
        header.add(messageLabel, BorderLayout.SOUTH);

        board = new BoardPanel();
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                onClick(event.getX(), event.getY());
            }
        });

        JButton undo = new JButton("Undo");
        undo.addActionListener(event -> {
            if (controller != null) {
                controller.handle(new PlayerAction.Undo());
            }
        });
        JButton moves = new JButton("Moves");
        moves.addActionListener(event -> {
            if (controller != null) {
                controller.handle(new PlayerAction.ShowMoves());
            }
        });
        JButton quit = new JButton("Quit");
        quit.addActionListener(event -> {
            if (controller != null) {
                controller.handle(new PlayerAction.Quit());
            }
        });
        JPanel buttons = new JPanel();
        buttons.add(undo);
        buttons.add(moves);
        buttons.add(quit);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.add(header, BorderLayout.NORTH);
        root.add(board, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        refresh();
    }

    private void onClick(int x, int y) {
        if (controller == null || game == null || game.isGameOver()) {
            return;
        }
        Position clicked = squareFromPoint(x, y, ORIGIN, SQUARE);
        if (clicked == null) {
            return;
        }
        Piece piece = game.board().pieceAt(clicked);
        if (selected == null) {
            if (piece != null && piece.color() == game.sideToMove()) {
                selected = clicked;
                refresh();
            } else {
                hint("Select a " + game.sideToMove() + " piece.");
            }
            return;
        }
        if (selected.equals(clicked)) {
            selected = null;
            refresh();
            return;
        }
        if (piece != null && piece.color() == game.sideToMove()) {
            selected = clicked;
            refresh();
            return;
        }
        String notation = selected.toString() + clicked.toString();
        selected = null;
        controller.handle(new PlayerAction.Move(notation));
    }

    /**
     * Maps a pixel to a square, white at the bottom. Screen rows count down
     * and ranks count up, so the top row of the window is rank 8.
     *
     * @return the square, or null when the point is outside the board
     */
    static Position squareFromPoint(int x, int y, int origin, int square) {
        int file = Math.floorDiv(x - origin, square);
        int row = Math.floorDiv(y - origin, square);
        if (!Position.isOnBoard(file, row)) {
            return null;
        }
        int rank = Position.BOARD_SIZE - 1 - row;
        return new Position(file, rank);
    }

    private void hint(String text) {
        hint = text;
        refresh();
    }

    private void refresh() {
        if (frame == null) {
            return;
        }
        Runnable job = () -> {
            statusLabel.setText(statusText());
            String shown = !hint.isBlank() ? hint : message;
            messageLabel.setText(shown.isBlank() ? " " : shown);
            board.repaint();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            job.run();
        } else {
            SwingUtilities.invokeLater(job);
        }
    }

    private String statusText() {
        if (game == null) {
            return " ";
        }
        return game.sideToMove() + " to move  ·  " + words(game.status());
    }

    private static String words(GameStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "In progress";
            case CHECK -> "Check";
            case CHECKMATE -> "Checkmate";
            case STALEMATE -> "Stalemate";
        };
    }

    private final class BoardPanel extends JPanel {

        private BoardPanel() {
            int span = ORIGIN + SQUARE * Position.BOARD_SIZE + 8;
            setPreferredSize(new Dimension(span, span));
            setBackground(new java.awt.Color(245, 241, 232));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            drawBoard(g);
            g.dispose();
        }

        private void drawBoard(Graphics2D g) {
            Board position = game == null ? null : game.board();
            Set<Position> targets = legalTargets();
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            for (int row = 0; row < Position.BOARD_SIZE; row++) {
                int rank = Position.BOARD_SIZE - 1 - row;
                g.setColor(LABEL);
                g.drawString(Integer.toString(rank + 1), 8, ORIGIN + row * SQUARE + SQUARE / 2 + 5);
                for (int file = 0; file < Position.BOARD_SIZE; file++) {
                    int x = ORIGIN + file * SQUARE;
                    int y = ORIGIN + row * SQUARE;
                    Position square = new Position(file, rank);
                    boolean light = (file + rank) % 2 != 0;
                    g.setColor(light ? LIGHT : DARK);
                    g.fillRect(x, y, SQUARE, SQUARE);
                    if (square.equals(selected)) {
                        g.setColor(SELECTED);
                        g.fillRect(x, y, SQUARE, SQUARE);
                    }
                    if (targets.contains(square)) {
                        g.setColor(LEGAL);
                        int dot = SQUARE / 5;
                        g.fillOval(x + (SQUARE - dot) / 2, y + (SQUARE - dot) / 2, dot, dot);
                    }
                    if (position != null) {
                        drawPiece(g, position.pieceAt(square), x, y);
                    }
                }
            }
            g.setColor(LABEL);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            for (int file = 0; file < Position.BOARD_SIZE; file++) {
                String name = String.valueOf((char) ('a' + file));
                g.drawString(name, ORIGIN + file * SQUARE + SQUARE / 2 - 4,
                        ORIGIN + SQUARE * Position.BOARD_SIZE + 16);
            }
        }

        private void drawPiece(Graphics2D g, Piece piece, int x, int y) {
            if (piece == null) {
                return;
            }
            boolean white = piece.color() == Color.WHITE;
            int margin = 10;
            int diameter = SQUARE - margin * 2;
            g.setColor(white ? IVORY : INK);
            g.fillOval(x + margin, y + margin, diameter, diameter);
            g.setColor(white ? INK : IVORY);
            g.drawOval(x + margin, y + margin, diameter, diameter);
            String letter = String.valueOf(piece.type().symbol());
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            int textX = x + SQUARE / 2 - g.getFontMetrics().stringWidth(letter) / 2;
            int textY = y + SQUARE / 2 + g.getFontMetrics().getAscent() / 2 - 4;
            g.drawString(letter, textX, textY);
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
    }
}
