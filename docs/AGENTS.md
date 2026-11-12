# Instructions for coding agents

This is a chess engine built with MVC. The model and the controller are written by hand and are finished. Your job is views only.

## Do

- Add to the current scaffold BorderedView, SwingView, 3DView, NewGlyph, and HttpView
- Implement the `BoardView` interface. Read it, `ConsoleView`, and
  `PlayerAction` before writing anything.
- Read the game only through `Game`: `board()`, `sideToMove()`,
  `legalMoves()`, `status()`, `history()`.
- Send every player action to the controller as a `PlayerAction`:
  `controller.handle(new PlayerAction.Move("e2e4"))`.
- Add four `cases` to `Main.chooseView` so the new view can be chosen from
  the command line.
- Update the README.md each iteration explaining the design honestly

## Don't

- Don't change anything in `model/`, `engine/`, or `factory/`.
- Don't change `BoardView`, `PlayerAction`, `GameController`,
  `ConsoleView`, or `EmojiView`.
- Don't call `game.play`, `game.undoLastMove`, or anything that changes
  the game. Only the controller changes the game.
- Don't work out a chess rule in the view. Legal moves come from
  `game.legalMoves()`; check and checkmate come from `game.status()`.
- Don't wait for input inside a click handler. A window's `start` builds
  the window and returns; it never calls `controller.runLoop()`.
- Don't add dependencies to `pom.xml`. Swing is part of the JDK.

## Check your work

- `./mvnw test` passes, with no test changed or deleted.
- `git diff --stat` lists only new files under `view/` and `Main.java`.

## Stop and ask

If you think a file under "Don't" has to change, stop and explain why
instead of changing it.