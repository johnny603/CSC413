package edu.sfsu.csc413.chess.view;

/**
 * Something the player asked for, independent of how they asked for it.
 *
 * <p>This is the type that makes a graphical UI possible without touching the
 * controller. Typing {@code "undo"} and clicking an Undo button are the same
 * request; they differ only in how the request was <em>entered</em>. So the
 * controller is written against the request, and the job of turning keystrokes
 * into requests belongs to {@link #parse} and to nowhere else. A button does
 * not spell out the word {@code "undo"} — it builds an {@link Undo} directly.
 *
 * <p>{@code sealed} means this list is closed: these four are all there is.
 * That is what lets the controller's {@code switch} have no {@code default}
 * branch — the compiler checks we handled every case. If someone adds a fifth
 * action later, the controller stops compiling until it is handled too. A
 * missing case becomes a build error instead of a bug found by a player.
 */
public sealed interface PlayerAction {

    /** Play the move written in long algebraic notation, e.g. {@code "e2e4"}. */
    record Move(String notation) implements PlayerAction {}

    /** Take back the last move. */
    record Undo() implements PlayerAction {}

    /** List every move that is legal right now. */
    record ShowMoves() implements PlayerAction {}

    /** Stop playing. */
    record Quit() implements PlayerAction {}

    /**
     * Turns a typed line into an action.
     *
     * <p>This is the <em>only</em> place in the program that knows the words
     * "undo", "moves", and "quit". Anything unrecognised is treated as an
     * attempted move, and the controller reports it if no such move exists —
     * which is why a typo like "qiut" comes back as an illegal move rather
     * than as a separate kind of error.
     */
    static PlayerAction parse(String line) {
        String text = line == null ? "" : line.trim().toLowerCase();
        return switch (text) {
            case "quit", "exit" -> new Quit();
            case "moves" -> new ShowMoves();
            case "undo" -> new Undo();
            default -> new Move(text);
        };
    }
}
