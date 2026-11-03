# M6 — The Controller and the End of the Game

**Course:** CSC 413 Software Development
**Milestone:** M6 · Week 11
**Objectives advanced:** 2 (interfaces, dependency inversion), 3 (responsibility assignment), 5 (Command), 6 (separate presentation from business logic with MVC), 7 (test a medium-sized system)
**Assigned:** Monday, November 2
**Due:** Monday, November 9, 11:59 PM

---

## The idea

Your engine knows every rule you have taught it, and nobody can play it.
`Main` plays moves you wrote into the source and prints the board. M5 ended
with a position where white has no legal move, and the program had nothing
to say about it.

M6 closes both gaps:

- **The model learns how a game ends.** When the side to move has no legal
  move, the game is over. Whether that side is in check is the only
  difference between a loss (checkmate) and a draw (stalemate). That is a
  rule, so it goes in the model, next to the filter you wrote in M5.
- **A person can play.** Session 16 sorted code with three questions: is it
  a rule, is it what the user sees, or does it take a request, run the rule,
  and tell the view? You have written the first kind for five milestones.
  M6 gives you the second kind, a console view, and you write the third:
  `GameController`.

You write the controller by hand because in M7 you generate views with AI,
and the controller is what you check them against. A generated view that
needs a change in your controller has broken the split.

Session 16 is the background for all of it:
[session 16 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-16-mvc/notes).

---

## Before you merge: the rename

Sessions 10, 12, and 14 used the name `BoardView` for a read-only board
interface. M6 ships a different `BoardView`, the view interface. If you
added the read-only one to your repository after M4, rename it to
`ReadOnlyBoard` first (IntelliJ: right-click the name → Refactor → Rename),
commit, then merge. Most of you never added it; skip this step.

---

## Getting the milestone

The [weekly loop](https://goleador.github.io/CSC413/guide.html?g=git-workflow):

```bash
git fetch upstream --tags
git merge m6
./mvnw test
```

**What arrives, in `src/main`:**

| File | What it is |
|---|---|
| `engine/GameStatus` | `IN_PROGRESS`, `CHECK`, `CHECKMATE`, `STALEMATE`, and `isGameOver()` |
| `view/BoardView` | the view interface: `render`, `showMessage`, `start`, and three defaults |
| `view/PlayerAction` | the four requests a player can make, and `parse` for typed ones |
| `view/ConsoleView` | a `BoardView` that prints the board with letters and reads moves from the keyboard |
| `view/EmojiView` | the same, with chess figures |
| `view/GameController` | a scaffold: a constructor, `game()`, and three methods that throw |

**What arrives, in `src/test`:**

- **`engine/MoveGeneratorTest`**, replacing M5's: M5's sixteen, plus four
  on `status`.
- **`engine/GameTest`**, replacing M3's: one new test (Fool's Mate ends the
  game), and one new line in `startFromFen`.
- **`view/GameControllerTest`**: eleven tests. They drive your controller
  through a fake view that plays a script of moves and records what it is
  told. No keyboard, no screen.
- **`view/PlayerActionTest`**: six tests of the given `parse`. They pass
  from the start.

Right after the merge the **main** code does not compile:

```
[ERROR] .../view/ConsoleView.java:[64,63] cannot find symbol
  symbol:   method status()
  location: variable game of type edu.sfsu.csc413.chess.engine.Game
```

The given views print the game's status under the board, and `Game` does
not have one yet. That is the assignment.

---

## What to build, in this order

The counts are what `./mvnw test` prints after each step.

**0. The three signatures, as stubs.** Each throws
`UnsupportedOperationException`, as in M5:

```java
// in MoveGenerator
public static GameStatus status(Board board, Color sideToMove)

// in Game
public GameStatus status()
public boolean isGameOver()
```

Javadoc first, as in M5.

*The build compiles: `Tests run: 83, Failures: 0, Errors: 17`.* Sixty-six
are green: sixty of M5's sixty-one, and `PlayerActionTest`'s six. The
sixty-first, `startFromFen`, now also asks for the status.

**1. `MoveGenerator.status(board, sideToMove)`.** How the game stands for
the side to move. Two questions decide it, and you answered both in M5: is
that side in check, and does it have a legal move?

| In check? | Has a legal move? | Status |
|---|---|---|
| no | yes | `IN_PROGRESS` |
| yes | yes | `CHECK` |
| yes | no | `CHECKMATE` |
| no | no | `STALEMATE` |

*`Errors: 13`.* `MoveGeneratorTest` is green.

**2. `Game.status()` and `Game.isGameOver()`.** `Game` is the facade:
views and controllers ask it, never `MoveGenerator`. It already does this
for `legalMoves()`; do the same here. `GameStatus` knows which statuses
end the game.

*`Errors: 11`.* Only `GameControllerTest` is left.

**3. `GameController`.** Read the given `BoardView` and `ConsoleView`
first. They are the other half of the conversation you are writing.

- **`start()`**: greet the player (`showMessage`), draw the opening
  position (`render`), then call `view.start(this)`. What happens next is
  the view's decision. The console view calls `runLoop()` back; a window
  in M7 will not.
- **`runLoop()`**: ask the view for the next action (`nextAction`) and
  `handle` it, until the player quits or the game is over.
- **`handle(action)`**: carry out one action, then redraw:

| Action | What the controller does | What the player is told |
|---|---|---|
| `Move` | find the legal move with that notation and play it | if there is none: `Not a legal move: 'e2e5'.` |
| `Undo` | take back the last move | if there was none: `Nothing to undo.` |
| `ShowMoves` | nothing changes | `20 legal moves: a2a3 a2a4 ...` (sorted) |
| `Quit` | stop; close the view; **no redraw** | `Goodbye.` |

After every action except `Quit`, call `render`. Then, if the game is over,
announce the result and close the view:

```
Checkmate. BLACK wins.
Stalemate. The game is a draw.
```

The side to move is the side that has been mated, so the winner is the
other one. Add whatever private fields and methods you need.

The `switch` over `PlayerAction` needs no `default` branch; the next
section explains why.

Do moves and quitting first. With undo, the list of moves, and the result
still missing, you will see `Failures: 4, Errors: 2`. Adding them leaves
the two promotion tests (`Errors: 2`).

**Promotion.** `e7e8q` names its piece, so it is an ordinary move. `e7e8`
does not, and matches nothing. When a four-character move matches nothing,
ask the view which piece it wants (`promotionChoice`) and try once more
with that piece's letter on the end. `PieceType` already knows its letter
(M1); notation writes it in lower case. The console view does not override `promotionChoice`, so it always
gets a queen; a Swing view in M7 can open a dialog instead, and the
controller does not change.

*All green: `Tests run: 83, Failures: 0, Errors: 0`.*

**4. `Main`.** Replace the hard-coded moves. `Main` creates a `Game`, a
view, and a `GameController` that joins them, and calls `start()`. Choose
the view from the command line: `emoji` for `EmojiView`, anything else for
`ConsoleView`. Declare the view variable as `BoardView`, the interface.

```bash
./mvnw -q compile
java -cp target/classes edu.sfsu.csc413.chess.Main
java -cp target/classes edu.sfsu.csc413.chess.Main emoji
```

Play Fool's Mate (`f2f3`, `e7e5`, `g2g4`, `d8h4`). The last screen:

```
8 r n b . k b n r
7 p p p p . p p p
6 . . . . . . . .
5 . . . . p . . .
4 . . . . . . P q
3 . . . . . P . .
2 P P P P P . . P
1 R N B Q K B N R
  a b c d e f g h

WHITE to move  |  CHECKMATE
Checkmate. BLACK wins.
```

Also try `e2e5`, `moves`, `undo` on the first move, and `quit`.

---

## `sealed`: why the switch needs no `default`

`PlayerAction` starts with a word you have not used yet:

```java
public sealed interface PlayerAction {
    record Move(String notation) implements PlayerAction {}
    record Undo() implements PlayerAction {}
    record ShowMoves() implements PlayerAction {}
    record Quit() implements PlayerAction {}
    ...
}
```

A `sealed` interface lists every class that may implement it. Here the
four records are declared inside it, so they are the list. A smaller
example, with the list written out in a `permits` clause:

```java
public sealed interface Reply permits Accept, Decline {}

public record Accept() implements Reply {}
public record Decline(String reason) implements Reply {}
```

**No one else can join.** A third class that tries does not compile:

```java
public record Maybe() implements Reply {}
```

```
error: class is not allowed to extend sealed class: Reply (as it is not listed in its 'permits' clause)
```

**So a switch can cover every case.** The compiler knows there are exactly
two replies, so a `switch` that handles both needs no `default`:

```java
static String explain(Reply reply) {
    return switch (reply) {
        case Accept accept -> "draw agreed";
        case Decline decline -> "declined: " + decline.reason();
    };
}
```

**And a missing case is a build error.** Leave out one of `PlayerAction`'s
four in a `switch` expression and the build stops:

```
error: the switch expression does not cover all possible input values
```

That is what you want from your controller. If a fifth action is added
later, say `Resign`, every `switch` that forgot it fails to compile,
instead of a player finding the bug. A `default` branch would hide it, so
leave it out.

Each record is also a request object: what the player asked for, without
how they asked. Typing `undo` and clicking an Undo button both become
`new PlayerAction.Undo()`. That is the Command pattern from session 15,
and it is why the controller never sees the words "undo" or "quit":
`parse` is the only place that knows them.

---

## What "done" looks like

```bash
./mvnw test
```

```
Tests run: 83, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Eighty-three: M5's sixty-one, four more in `MoveGeneratorTest`, one more
in `GameTest`, eleven in `GameControllerTest`, and six in
`PlayerActionTest`.

You can play a game to checkmate in the console, and with `emoji`.

The diff since `submit-m5`, apart from the files the merge brought:
`MoveGenerator` gains `status`, `Game` gains `status` and `isGameOver`,
`GameController`'s three methods get bodies, and `Main` changes. Nothing
in `model` or `factory`, apart from the rename if you needed it. If one of the given views changed, ask why.

Two checks from session 16, on your own code:

- `GameController` has no `Scanner`, no `System.out`, and none of the
  strings `"undo"`, `"moves"`, or `"quit"`.
- `ConsoleView` and `EmojiView` are unchanged, and neither calls
  `game.play`.

---

## `m6-submission.md`

Add `m6-submission.md` at the top of your repository with short answers,
two to four sentences each. Name your own classes and methods.

1. **Who decides, who shows.** Checkmate is detected in one class and
   announced in another. Name both, and say which of session 16's three
   questions puts each one there.
2. **The dependency rule.** Which of your classes import
   `edu.sfsu.csc413.chess.view`? Which must not, and why?
3. **Test 2 on your engine.** In M7 you add a Swing view. List the files
   you expect to change. If your list includes `GameController` or
   anything in `engine`, say what in your design makes that necessary.

---

## What you submit

```bash
git add -A
git commit -m "M6: <what you did>"
git tag submit-m6
git push origin main --tags
```

**Commit before you tag.** `git status` should be clean first.

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m6`.

---

## How it is graded

| Criterion | Weight |
|---|---|
| All eighty-three tests green (`./mvnw test`, checked by clone-and-run) | 40% |
| `status` in `MoveGenerator`, built on `isInCheck` and `legalMoves`; `Game` asks it; `model` and `factory` unchanged | 15% |
| `GameController`: no console I/O, no command words, no `default` in the `switch` over `PlayerAction`, no game state of its own beyond knowing whether the player quit | 20% |
| `Main` builds a `Game`, a `BoardView`, and a controller, and the game is playable to checkmate in the console | 10% |
| `m6-submission.md` answers name your own classes and match your code | 10% |
| `submit-m6` tag pushed | 5% |

---

## Common problems

- **`namesTheWinner` fails: the message says `WHITE wins`** — the result
  names the side to move. That side has been mated.
- **`redrawsAfterEachAction` fails** — either the opening position is not
  drawn in `start`, or `Quit` redraws before stopping. The test expects
  three renders for `e2e4`, `e7e5`, `quit`: the opening and one per move.
- **`playsFoolsMate` fails, but `namesTheWinner` passes** — the result is
  announced and the view is not closed.
- **`undoTakesBackAMove` fails** — `Game.undoLastMove` already exists
  (M3). Call it; it returns empty when there is nothing to undo.
- **`promotionDefaultsToQueen` errors with a `NullPointerException`** —
  `e7e8` was reported as illegal and nothing reached e8. See
  *Promotion* in step 3.
- **The console prints nothing and waits** — `start` never called
  `view.start(this)`, or `runLoop` never asks `nextAction`.
- **`UnsupportedOperationException: ... is event-driven, so it does not
  answer nextAction()`** — a view that does not answer `nextAction` reached
  `runLoop`. You will not see this in M6 with the given views; in M7 it
  means a generated Swing view called `runLoop`. It should not.
- **A merge conflict in `view/BoardView.java`, or `cannot find symbol:
  method pieceAt`** — your read-only `BoardView` is still called that. Abort
  the merge (`git merge --abort`), do the rename at the top of this
  handout, commit, and merge again.

---

## A note on scope

Still no castling and no en passant. No draw by repetition, the fifty-move
rule, or insufficient material: stalemate is the only draw. No resigning
and no offering a draw; both would be a fifth `PlayerAction`, and the
compiler would show you every place to handle it.

Do not write a Swing view yet. That is M7, and Wednesday's class covers
how it works.
