# M5 — King Safety: Check and the Legal-Move Filter

**Course:** CSC 413 Software Development
**Milestone:** M5 · Week 10
**Objectives advanced:** 2 (design principles), 3 (responsibility assignment), 7 (test a medium-sized system), 9 (communicate designs with UML)
**Assigned:** Monday, October 26
**Due:** Monday, November 2, 11:59 PM

---

## The idea

Since M2 your engine has known how every piece moves, and since M3 it has
let anyone play any of those moves. That includes moves no chess player is
allowed to make: stepping a king onto a square a rook covers, or moving a
knight that is the only thing standing between its king and a queen.

The rule is one sentence: **a move may not leave your own king attacked.**
No piece can enforce it. A knight on e2 knows where it can jump; whether
jumping exposes the king on e1 depends on a rook on e8 the knight never
looks at. The rule is about the whole board, so it belongs to the class
that looks at the whole board: `MoveGenerator`.

That is why M4 moved the loop there. `legalMoves` has been returning
`pseudoLegalMoves` unchanged, with a javadoc that said M5 would add the
filter "here and only here". M5 is that filter, and the two questions it
has to ask first: *where is the king?* and *is that square attacked?*
Session 11 §6 argued for this order of work, refactoring first and the
feature second: [session 11 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-11-refactoring/notes).

M5 also asks you to draw what you built. Session 14 introduces UML; you
hand in one class diagram and one sequence diagram of your engine.

---

## Getting the milestone

The [weekly loop](https://goleador.github.io/CSC413/guide.html?g=git-workflow):

```bash
git fetch upstream --tags
git merge m5
./mvnw test
```

If you are working in a group, one member merges and pushes; the others pull.

**What arrives:**

- **A new `engine/MoveGeneratorTest`**, replacing M4's: sixteen tests. Five
  are M4's (the filter must not break them); eleven are about attacks,
  check, and the filter.
- **Three new tests** in `model/KingPositionTest`.

**What does not arrive:** any scaffold. `Board` is yours from M1 and
`MoveGenerator` is yours from M4; M5 adds methods to both, and you write
the signatures yourself from the next section.

Right after the merge, and like M1 and M2, the tests **do not compile**:

```
[ERROR] .../engine/MoveGeneratorTest.java:[104,33] cannot find symbol
  symbol:   method isAttacked(edu.sfsu.csc413.chess.model.Board,edu.sfsu.csc413.chess.model.Position,edu.sfsu.csc413.chess.model.Color)
  location: class edu.sfsu.csc413.chess.engine.MoveGenerator
```

fifteen of them, naming `kingPosition`, `isInCheck`, and `isAttacked`. That
is the assignment.

---

## What to build, in this order

The counts are what `./mvnw test` prints after each step.

**0. The three signatures, as stubs.** Add each with a body that throws
`UnsupportedOperationException`, as the M3 and M4 scaffolds did:

```java
// in Board
public Position kingPosition(Color color)

// in MoveGenerator
public static boolean isInCheck(Board board, Color color)
public static boolean isAttacked(Board board, Position target, Color attacker)
```

Write a javadoc for each one now, before its body. The descriptions below
are what they promise.

*The build compiles: `Tests run: 61, Failures: 5, Errors: 8`.* Forty-eight
are green: the forty-two from M0b to M3, M4's five in the new file, and
`legalMovesLeaveTheBoardAsFound`, which cannot fail until there is a filter
to get wrong.

**1. `Board.kingPosition(color)`** — the square holding that colour's
king, or `null` if it has none. A real game always has both kings; the
tests build partial positions without them all the time, so `null` is a
normal answer, not an error. The board already knows how to list every
square a colour occupies, and every piece knows its type.

*`Errors: 5`.* `KingPositionTest` is green.

**2. `MoveGenerator.isAttacked(board, target, attacker)`** — true if any
piece of colour `attacker` attacks `target`. Not "could move there":
*attacks*. The difference is the pawn, which moves straight ahead and
captures diagonally. M2 gave every piece `attacks(board, from, target)` for
exactly this, and `Pawn` overrides it. Ask each piece; do not ask what kind
of piece it is.

*`Errors: 3`.* `pawnAttacksDiagonallyNotForward` and
`blockedLineIsNotAttacked` are green.

**3. `MoveGenerator.isInCheck(board, color)`** — true if `color`'s king is
attacked by the other colour. Two calls you just wrote, and a decision about
the side with no king: it is not in check.

*`Errors: 0`, `Failures: 5`.* What is left is the filter.

**4. The filter, in `legalMoves`.** Keep each pseudo-legal move that does not
leave `color`'s own king in check, and drop the others. The only way to find
out is to try it. `Board.apply` plays a move and `Board.undo` takes it back
exactly (M3 built them for this), so: play the candidate, ask `isInCheck`,
take it back, decide. Every move you try must be taken back before you try
the next, and before `legalMoves` returns. The caller's board must be
exactly as it was.

`pseudoLegalMoves` does not change. `Game` does not change: it already asks
`MoveGenerator.legalMoves`, and from this step on it gets the real answer.

*All green: `Tests run: 61, Failures: 0, Errors: 0`.*

**5. Try it on a game.** In `Main`, or a scratch test, start a `Game` and
play `f2f3`, `e7e5`, `g2g4`, `d8h4` through `findLegalMove` and `play`. Then
print `game.legalMoves()`. Before M5 it listed white moves; now it is
empty, because every white move leaves the king attacked by the queen on
h4. What the program should *say* about that is M8's job.

**6. Draw it.** Last, and in its own commit: see the next section.

---

## The diagrams

Create `docs/design.md` in your repository with two diagrams in
[Mermaid](https://mermaid.js.org/) syntax, which GitHub draws when it shows the
file. Session 14 covers both kinds and the notation.

**A class diagram of `engine` and `model`.** Every class and interface in
those two packages. For each class, its public methods; fields only where they
explain a relationship. Then the relationships, each with the right arrow:

- which classes extend `Piece`;
- what `Game` holds, and what it only uses;
- what `MoveGenerator` depends on, given that it holds nothing;
- what a `Move` refers to.

The six piece subclasses may be drawn without their methods; they all have
the same ones.

**A sequence diagram of `game.legalMoves()`**, as your code runs it after
step 4. Lifelines for the caller, `Game`, `MoveGenerator`, and `Board`; one
`Piece` lifeline stands for all of them. Show the loop over candidates as a
`loop` fragment and the keep-or-drop decision as an `alt`.

Draw your code, not the reference's. If your `Game` holds something the list
above does not mention, draw it. If you added `BoardView` after M4, it belongs
in the class diagram, with the arrow that says who implements it and the
arrow that says who returns it.

Check that GitHub renders both: open `docs/design.md` on github.com. A
Mermaid syntax error shows up there as an error box instead of a picture.

---

## What "done" looks like

```bash
./mvnw test
```

```
Tests run: 61, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Sixty-one: M0b's eleven, M1's thirteen, M2's ten, M3's eight, and the
nineteen from M5 (sixteen in `MoveGeneratorTest`, three in
`KingPositionTest`).

The diff since `submit-m4`: `Board` gains one method, `MoveGenerator` gains
two and its `legalMoves` body changes, and `docs/design.md` appears. Nothing
else. If `Game`, `Main`, or a piece class changed, ask why.

---

## What you submit

```bash
git add -A
git commit -m "M5: <what you did>"
git tag submit-m5
git push origin main --tags
```

**Commit before you tag.** A tag points at a commit, so anything still
uncommitted when you tag is not in your submission; `git status` should be
clean first.

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m5`.

---

## How it is graded

| Criterion | Weight |
|---|---|
| All sixty-one tests green (`./mvnw test`, checked by clone-and-run) | 40% |
| The forty-two earlier tests still passing; `Game`, `model` (except `Board.kingPosition`), and `factory` unchanged | 10% |
| `isInCheck` is built from `kingPosition` and `isAttacked`; `isAttacked` asks `Piece.attacks`; no `switch` or `instanceof` on piece type anywhere new | 15% |
| The filter tries each move and takes it back; `MoveGenerator` still has no fields, a private constructor, and a package-private `pseudoLegalMoves` | 10% |
| `docs/design.md`: a class diagram and a sequence diagram that render on GitHub and match your code | 20% |
| `submit-m5` tag pushed | 5% |

The tests cannot read your diagrams. The grader reads them next to your
code, and a diagram that shows a relationship the code does not have, or
misses one it does, loses marks the same way a wrong method would.

---

## Common problems

- **Four `GameTest` errors, `IllegalArgument Illegal move: e2e4`, and
  `legalMovesLeaveTheBoardAsFound` failing** — the filter plays a candidate
  and does not take it back. By the end of the loop the board is full of
  moved pieces, and `Game` is asking about a position nobody played. Every
  `apply` needs its `undo` before the next candidate.
- **Only `pawnAttacksDiagonallyNotForward` fails** — `isAttacked` asks for
  the piece's *moves*. A pawn on e4 moves to e5 but does not attack it. Ask
  `attacks`.
- **`detectsCheck` and five filter tests fail, but `noCheckAtStart`
  passes** — `isInCheck` asks whether `color`'s own pieces attack the king.
  They never do, so nothing is ever in check. The attacker is the other
  colour; `Color` has a method for that.
- **`asksOnlyTheGivenColour` or the opening counts change** — the filter is
  removing moves it should keep. Print the position and the removed moves;
  usually `isInCheck` is asked about the wrong colour, or the board was not
  restored after an earlier candidate.
- **"cannot find symbol: method kingPosition" with the method in `Board`** —
  the signature differs from step 0: a different return type or parameter.
  The tests call exactly those three signatures.
- **The diagram shows as a code block on GitHub** — the fence must say
  ` ```mermaid `, not ` ```java ` or nothing.

---

## A note on scope

Still no checkmate, stalemate, or game-over message: those are M8, and they
fall out of what you build here. When a side has no legal moves, whether it
is in check is the only difference between the two. Still no castling and no
en passant (M12). A king may not castle out of check; that rule will use your
`isAttacked`.

Do not add `status` or `GameStatus` yet, and do not give `MoveGenerator` a
field to remember the king's square between calls. The board knows where the
king is. Ask it.
