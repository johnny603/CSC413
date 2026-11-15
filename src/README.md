# Views

The model and the controller are unchanged. Every view implements `BoardView`.
It reads the position through `Game` (`board`, `sideToMove`, `legalMoves`,
`status`, `history`) and sends what the player asked for as a `PlayerAction`.
No view calls `play` or `undoLastMove`.

## `swing`

`SwingView` is event-driven. `start` builds a window and returns. It does not
call `runLoop`.

You click one of your pieces, then the square it should land on. The selected
square is a field on the view. Dots on the destination squares come from
`legalMoves()` for that piece. Whose turn it is, and whether that side is in
check, checkmate, or stalemate, come from `sideToMove()` and `status()`. An
illegal click is sent as `PlayerAction.Move`, and the controller's message is
shown above the board. Undo sends `new PlayerAction.Undo()`. A promotion is
left to the interface default, which is a queen, so the window never waits
inside the click.

Pieces are a letter on a disc: ivory disc and dark letter for white, dark disc
and ivory letter for black, so both colors stay readable on light and dark
squares. Rank 8 is the top row. Screen y grows downward and ranks grow upward.

```bash
./mvnw -q compile
java -cp target/classes edu.sfsu.csc413.chess.Main swing
```

## `bordered`

`BorderedView` is a console loop, the same questions as `ConsoleView`, with
two display choices of its own. The board from `TextBoardRenderer` is wrapped
in a box, and it is drawn from `sideToMove()`, so the board turns around
after every move.

```bash
java -cp target/classes edu.sfsu.csc413.chess.Main bordered
```

## `glyph`

`NewGlyph` is also a console loop. The only new fact is the glyph table:
`Kw` is a white king, `Pb` a black pawn, `··` an empty square. Both columns
are the same width, so the files stay aligned. It is still a full `BoardView`,
not a glyph table passed into `ConsoleView`.

```bash
java -cp target/classes edu.sfsu.csc413.chess.Main glyph
```

## `3d`

The class is `ThreeDView`. A Java name cannot start with a digit, so the
scaffold file `3DView.java` could not be a class. The command word is `3d`.

Each rank is a row of squares with a slanted left edge. Farther ranks are
indented, and the near edge is the side to move. Input is a typed line,
handled by the controller.

```bash
java -cp target/classes edu.sfsu.csc413.chess.Main 3d
```

## `http`

`HttpView` uses `com.sun.net.httpserver` and listens on `127.0.0.1`, starting
at port 8765. `start` starts the server and returns. A click is a link such
as `/square/e2`. The selected square is stored on the server, in the view.
The page marks legal destinations from `legalMoves()` and prints check from
`status()`. Undo, the move list, and quit are links that send `Undo`,
`ShowMoves`, and `Quit`. There is no new dependency in `pom.xml`.

```bash
java -cp target/classes edu.sfsu.csc413.chess.Main http
```
