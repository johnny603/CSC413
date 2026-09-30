# M4 — `MoveGenerator`: the Generation Loop Leaves `Game`

**Course:** CSC 413 Software Development
**Milestone:** M4 · Week 6
**Objectives advanced:** 2 (SOLID design principles), 3 (analyze designs for cohesion, coupling, and responsibility assignment), 4 (refactor toward cleaner software)
**Assigned:** Monday, September 28
**Due:** Monday, October 12, 11:59 PM

---

## The idea

M3's `Game` does four things: it holds the board, knows whose turn it is,
remembers the moves played, and generates the moves the side to move may
play. The first three belong together; undo needs all of them. The fourth
does not. Look at what the generation loop reads:

```java
List<Move> moves = new ArrayList<>();
for (Position from : board.positionsOf(sideToMove)) {
    moves.addAll(board.pieceAt(from).pseudoLegalMoves(board, from));
}
return moves;
```

A board and a colour. Not the history, not the turn as a *turn*, only the
colour whose pieces to ask. The loop's own signature is telling you it is a
different concern: a question about a *position*, not about a *game*.

M4 moves it. A new class in `engine`, `MoveGenerator`, holds the loop;
`Game.legalMoves()` becomes one line that asks it. Nothing that calls `Game`
changes, and `Board` is not touched. Session 9 explains why this cut and not
another: [session 9 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-09-cohesion-coupling/notes).

This is a **refactor**: the behaviour is identical before and after, and the
tests prove it. The forty-two you have stay green from the first step to the
last. What changes is where the code lives, and next week that matters: M5
adds king safety, a rule that needs the whole board and no single piece can
enforce, and it goes into `MoveGenerator` and nowhere else.

---

## Getting the milestone

The [weekly loop](https://goleador.github.io/CSC413/guide.html?g=git-workflow):

```bash
git fetch upstream --tags
git merge m4
./mvnw test
```

If you are working in a group, one member merges and pushes; the others pull.

**What arrives:**

- **One scaffold** in `engine`: `MoveGenerator`. Two static methods, both
  throwing `UnsupportedOperationException("M4: implement ...")`. No fields,
  and a private constructor; there is nothing to construct.
- **Six new tests** in `engine/MoveGeneratorTest`.

**What does not arrive:** `Game`. It is yours from M3, and this milestone
you change one method in it. `Board` and everything in `model` stay exactly
as they are.

Right after the merge the build compiles. The suite runs and reports:

```
Tests run: 48, Failures: 0, Errors: 6
```

with every error reading `M4: implement MoveGenerator.legalMoves` or
`MoveGenerator.pseudoLegalMoves`. Forty-two are still green. That is the
assignment: keep them green while the loop moves.

Read the scaffold before writing anything. Two things about it are the
lesson:

```java
public final class MoveGenerator {

    private MoveGenerator() { }

    public static List<Move> legalMoves(Board board, Color color)
           static List<Move> pseudoLegalMoves(Board board, Color color)
}
```

**Static, with a private constructor.** `MoveGenerator` holds no state. It
has no board of its own, no side to move, no history; everything it needs
arrives as a parameter. A class with nothing to remember has nothing to
construct, so its methods are `static` and `new MoveGenerator()` is a
compile error.

**One method has no access modifier.** `pseudoLegalMoves` is
**package-private**: visible to `engine`, and to nothing outside it. Session
5 promised we would use that. `Game` may call it, and the tests may, because
they live in `engine` too. `Main` and the view cannot, and should not: they
ask `Game`, which decides what "legal" means. `legalMoves` is `public`
because M5's tests and, later, an AI will want it.

---

## What to build, in this order

The counts are what `./mvnw test` prints after each step.

**1. `MoveGenerator.pseudoLegalMoves`.** Cut the loop out of
`Game.legalMoves()` and paste it here, with two edits: `sideToMove` becomes
the `color` parameter, and `board` is the parameter rather than the field.
Add the imports the compiler asks for.

*Still `Errors: 6`.* Every test reaches `legalMoves` first, and that still
throws. Nothing is green yet, and that is expected.

**2. `MoveGenerator.legalMoves`.** One line:

```java
return pseudoLegalMoves(board, color);
```

This week "legal" and "pseudo-legal" are the same list. M5 puts the
king-safety filter between them, in this method, and callers never notice.

*All six green: `Errors: 0`.* Including `gameAgreesWithTheGenerator`, which
passes even though `Game` still has its own copy of the loop. Two copies of
the same loop agree with each other. That is not done.

**3. `Game.legalMoves()` delegates.** Replace its body with one line:

```java
return MoveGenerator.legalMoves(board, sideToMove);
```

Delete the loop. Delete the imports `Game` no longer needs; IntelliJ greys
them out. Nothing else in `Game` changes: not a signature, not a field, not
`play` or `undoLastMove`.

*Still `Errors: 0`.* A refactor that changes a test result was not a
refactor.

**4. Read the diff.** `git diff` before you commit. `Game` should be shorter
by the loop and longer by one line. `MoveGenerator` should contain the loop
once. If the loop appears anywhere twice, you copied when you should have
moved.

---

## What "done" looks like

```bash
./mvnw test
```

```
Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Forty-eight: M0b's eleven, M1's thirteen, M2's ten, and M3's eight, **all
still passing**, plus six in `MoveGeneratorTest`.

`Main` does not change. It never knew about the loop; it talked to `Game`,
and `Game`'s signatures are the same. If you find yourself editing `Main`
this milestone, stop and ask why.

---

## What you submit

```bash
git add -A
git commit -m "M4: <what you did>"
git tag submit-m4
git push origin main --tags
```

**Commit before you tag.** A tag points at a commit, so anything still
uncommitted when you tag is not in your submission; `git status` should be
clean first.

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m4`.

---

## How it is graded

| Criterion | Weight |
|---|---|
| All forty-eight tests green (`./mvnw test`, checked by clone-and-run) | 45% |
| The forty-two earlier tests still passing; nothing in `model` or `factory` edited | 10% |
| `Game.legalMoves()` is a single delegation to `MoveGenerator.legalMoves`; the loop appears in the project exactly once | 20% |
| `MoveGenerator` has no fields, its constructor is private, `pseudoLegalMoves` is still package-private, and the scaffolded signatures are unchanged | 10% |
| No other method of `Game` changed; no `switch` or `instanceof` on piece type anywhere new | 10% |
| `submit-m4` tag pushed | 5% |

The middle rows are graded by reading, and they are the milestone. The
tests cannot tell whether the loop moved or was copied, because two copies
agree. Your diff can.

---

## Common problems

- **`legalMoves` green but `pseudoLegalMoves` still throws** — you wrote the
  loop in `legalMoves` directly. Move it down; `legalMoves` is one line.
- **`asksOnlyTheGivenColour` fails with 29** — the loop uses a colour of its
  own, or loops over both. It must ask `positionsOf(color)` for the parameter
  it was given.
- **`openingHasTwentyMoves` gets something other than 20** — an M2 piece is
  wrong, and the thirty-four earlier tests will say which. M4 cannot cause
  this; the loop did not change.
- **"`pseudoLegalMoves(Board,Color)` is not public in `MoveGenerator`;
  cannot be accessed from outside package"** — something outside `engine`
  called it. That is the modifier doing its job. Call `Game.legalMoves()`
  instead, or, if you are experimenting in `Main`, `MoveGenerator.legalMoves`.
- **"`MoveGenerator()` has private access"** — you wrote
  `new MoveGenerator()`. The methods are static: `MoveGenerator.legalMoves(...)`.
- **`Game` still imports `ArrayList` and `Position`** — leftover from the
  loop. Not an error, but a sign it was copied rather than moved. Remove
  them.
- **The M3 tests fail after the change** — `Game.legalMoves()` passes the
  wrong colour, or you edited `play` or `undoLastMove` while you were in the
  file. Those did not need to change; revert.

---

## A note on scope

Still no king safety, no check, no endings. `legalMoves` returns
`pseudoLegalMoves` unchanged, and the tests are written to hold under
exactly that rule. M5 adds `Board.kingPosition`, `MoveGenerator.isAttacked`
and `isInCheck`, and the filter that makes `legalMoves` mean what its name
says. All of it lands in `MoveGenerator`, which is why it exists a week
early.

Do not add `isAttacked` yet, and do not give `MoveGenerator` a field. If it
needs to remember something between calls, it has stopped being a generator
and started being a game.
