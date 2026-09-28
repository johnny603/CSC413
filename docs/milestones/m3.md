# M3 — Turns, Moves, and `Game`

**Course:** CSC 413 Software Development
**Milestone:** M3 · Week 5
**Objectives advanced:** 1 (OO software in modern Java), 3 (analyze designs for responsibility assignment), 4 (defensive design and failure handling)
**Assigned:** Wednesday, September 23
**Due:** Monday, October 5, 11:59 PM

---

## The idea

M2's pieces know where they can go. M3 plays the game: list every move for
the side to move, play one, refuse a bad one, take the last one back.

The whole milestone is one paragraph:

> A game of chess is played between two sides, who take turns. On each turn
> the side to move plays one of its legal moves. The game remembers the moves
> played, so that the last one can be taken back.

Session 8 turned that paragraph into a class. `Game`, in a new `engine`
package, **has** a `Board`, **has** a `List<Move>` of the moves played, and
**has** a `Color` that says whose turn it is. It owns all three, and nothing
else touches them. `Board` gains two methods, `apply` and `undo`, that move a
piece without asking whether they should. The design was settled in class —
[session 8 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-08-game-collections/notes)
— and §5 there decides, for every method, how it says "no".

"Legal" this week means the right colour and the right geometry: every
pseudo-legal move of every piece belonging to the side to move. King safety is
M5, and it will tighten `legalMoves()` without renaming it or touching its
callers.

---

## Getting the milestone

The [weekly loop](https://goleador.github.io/CSC413/guide.html?g=git-workflow):

```bash
git fetch upstream --tags
git merge m3
./mvnw test
```

If you are working in a group, one member merges and pushes; the others pull.

**What arrives:**

- **One scaffold** in a new `engine` package: `Game`. The fields, both
  constructors, and every signature are there; the two constructors work,
  and every other body throws `UnsupportedOperationException("M3: implement
  ...")`.
- **Eight new tests** in `engine/GameTest`.

**What does not arrive:** `Board`. It is yours from M1, and this milestone
you add two methods to it. Everything from M1 and M2 stays as it is.

Right after the merge, and unlike M1 and M2, the build compiles. The suite
runs and reports:

```
Tests run: 42, Failures: 0, Errors: 8
```

with every error reading `M3: implement Game.legalMoves` or
`Game.findLegalMove` or `Game.undoLastMove`. That is the assignment.

---

## What to build, in this order

Each step turns the next test's failure into something readable. The
counts are what `./mvnw test` prints after that step.

**1. `Board.apply` and `Board.undo`.** Session 8 §6.

```java
public void apply(Move move)    // lift the piece off `from`, set it down on `to`
public void undo(Move move)     // put `moved` back on `from`; put `captured` (or null) back on `to`
```

`apply` checks nothing. It trusts the `Move` it is given, because deciding
whether a move is allowed is `Game`'s job, not storage's. `undo` needs no
bookkeeping because the `Move` carries the piece it captured; that is why
`Move` has a `captured` field.

One wrinkle: if the move is a promotion, the piece set down on `to` is a new
piece of `promotesTo()`'s type and the mover's colour, not the pawn. You
need to build a piece from a `PieceType`. The one `switch` on `PieceType`
in the project is `PieceFactory.create`, and calling it from `Board` makes
`model` depend on `factory`, an arrow pointing the wrong way. A second,
private switch in `Board` duplicates four lines. Either is accepted this
milestone; say in a comment which you chose and what it costs. No M3 test
promotes, so this is graded by reading.

*Still `Errors: 8`* — nothing calls `Board` yet.

**2. The three accessors** — `board()`, `sideToMove()`, `history()`.
`history()` returns a **copy**: `List.copyOf(history)`. Session 8 §3 says
why; the criterion below says it counts. *Still `Errors: 8`.*

**3. `legalMoves()`** — session 8 §2's loop, for `sideToMove`:

```java
List<Move> moves = new ArrayList<>();
for (Position from : board.positionsOf(sideToMove)) {
    moves.addAll(board.pieceAt(from).pseudoLegalMoves(board, from));
}
return moves;
```

*`twentyMovesAtStart` and `startFromFen` turn green: `Errors: 6`.*

**4. `findLegalMove(String)`** — walk `legalMoves()`, return the first whose
`toString()` equals the notation ignoring case, wrapped in an `Optional`;
`Optional.empty()` if none. Every other test plays its moves through this
method, so it comes before `play`.

*`Failures: 1, Errors: 5`* — `rejectsIllegalMove` now reaches `play` and
gets the wrong exception. Next step.

**5. `play(Move)`** — the guard, then three lines:

```java
if (!legalMoves().contains(move)) {
    throw new IllegalArgumentException("Illegal move: " + move);
}
board.apply(move);
history.add(move);
sideToMove = sideToMove.opposite();
```

`contains` works because `Move` is a record and `equals` compares every
field. The message carries the move; `"illegal move"` on its own tells the
person reading the stack trace nothing.

*`turnsAlternate`, `playMovesThePiece`, `rejectsIllegalMove` green:
`Errors: 3`.*

**6. `undoLastMove()`** — empty history means `Optional.empty()`. Otherwise
remove the last move from the list, `board.undo(it)`, flip `sideToMove`
back, and return `Optional.of(it)`. A `List` is a perfectly good stack when
only one end moves.

*The three undo tests green: `Errors: 0`.*

---

## What "done" looks like

```bash
./mvnw test
```

```
Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Forty-two: M0b's eleven, M1's thirteen, and M2's ten, **all still passing**,
plus eight in `GameTest`.

Then **`Main`**. Replace M2's one-liner with a game: build a `Game`, print
its board, play `e2e4` and then `e7e5` by way of `findLegalMove`, print
again, undo one move, print again. Something like:

```java
Game game = new Game();
TextBoardRenderer renderer = new TextBoardRenderer(PieceGlyphs.LETTERS);
System.out.println(renderer.render(game.board()));

for (String notation : List.of("e2e4", "e7e5")) {
    game.play(game.findLegalMove(notation).orElseThrow());
}
System.out.println(renderer.render(game.board()));

game.undoLastMove();
System.out.println(renderer.render(game.board()));
```

Three boards: the start, both e-pawns advanced, then only White's. `Main`
is not tested. It is the point: `Main` talks to `Game`, and to nothing
behind it.

---

## What you submit

```bash
git add -A
git commit -m "M3: <what you did>"
git tag submit-m3
git push origin main --tags
```

**Commit before you tag.** A tag points at a commit, so anything still uncommitted
when you tag is not in your submission — `git status` should be clean first.
You should have been committing as you went, in which case this last one is small
or unnecessary.

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m3`.

---

## How it is graded

| Criterion | Weight |
|---|---|
| All forty-two tests green (`./mvnw test`, checked by clone-and-run) | 55% |
| M0b's, M1's and M2's thirty-four still passing — nothing from earlier milestones edited except `Board`, which only gains `apply` and `undo` | 10% |
| `history()` returns a copy; `Board.apply` checks nothing | 10% |
| Each method says "no" the way the scaffold's javadoc says: `play` throws `IllegalArgumentException` for a move not in `legalMoves()`, `findLegalMove` and `undoLastMove` return `Optional` and never null, never throw | 10% |
| The scaffolded signatures unchanged; `Game` in `engine`; no `switch` or `instanceof` on piece type in anything new, except the promotion arm in `Board` if you chose it | 10% |
| `submit-m3` tag pushed | 5% |

The three design criteria are graded by reading. If `history()` hands out
the list `Game` keeps, a caller can `clear()` it and the game forgets it was
ever played. If `Board.apply` checks whether the move is legal, two classes
now decide the rules. If `play` quietly ignores a bad move, or
`findLegalMove` throws for one, the caller cannot tell an ordinary "no" from
a bug, which was the whole of session 8 §5.

---

## Common problems

- **"cannot find symbol: method apply(Move)"** — `play` was written before
  `Board.apply`. Step 1 first.
- **`twentyMovesAtStart` gets 0** — the loop asks for
  `positionsOf(sideToMove)` but `sideToMove` is not set, or the loop uses a
  colour of its own. **Gets 40** — you looped over both colours.
  **Gets something else** — an M2 piece is wrong; the thirty-four earlier
  tests will tell you which.
- **`rejectsIllegalMove`: "Unexpected exception type thrown ...
  UnsupportedOperationException"** — `play` is not written yet; this is the
  expected state after step 4. **"Expected IllegalArgumentException to be
  thrown, but nothing was thrown"** — `play` applies the move without the
  guard.
- **`play` throws for a move that is plainly legal** — you compared with
  `==` or looked the move up by `from` and `to` alone. Use
  `legalMoves().contains(move)`; the record's `equals` does the rest.
- **`undoRestoresEverything`: "the turn goes back to black"** — undo flipped
  the board but not `sideToMove`.
- **`undoRestoresCapture`: the captured pawn is gone** — `undo` put `null`
  on `to`. Put `move.captured()` there; it is null for a quiet move anyway.
- **`undoWithNoHistory` throws `IndexOutOfBoundsException`** — no empty
  check before `remove(size - 1)`.
- **`history().size()` is wrong after undo** — `history()` built its copy
  once and cached it. Copy on every call.
- **`ConcurrentModificationException`** — you removed from a list while
  iterating it with for-each. Build a new list, or `removeIf`. Not needed
  this week; it will be in M5.
- **M2's tests now fail** — `Board.place` or `pieceAt` changed while adding
  `apply`. Those are fixed; revert.

---

## A note on scope

No king safety, no check, no checkmate, no stalemate, no castling, no en
passant. `legalMoves()` is pseudo-legal moves for the right colour, and the
tests are written to hold under exactly that rule. M4 moves the generation
loop out of `Game` into its own class, M5 filters it for king safety, M8
adds the endings, and M12 the history-dependent moves. None of them will
change `Game`'s public signatures; that is what the design you are building
is for.

Do not add a `MoveGenerator` yet, and do not give `Board` an opinion about
whose turn it is. `Board` stores; `Game` decides.
