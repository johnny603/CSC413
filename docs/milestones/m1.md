# M1 — The Domain Model

**Course:** CSC 413 Software Development
**Milestone:** M1 · Week 4 (week 3's milestone, opened a week late — see below)
**Objectives advanced:** 1 (OO software in modern Java), 2 (encapsulation, abstraction, composition), 3 (analyze designs for responsibility assignment)
**Assigned:** Monday, September 14
**Due:** Monday, September 21, 11:59 PM

> **Calendar note.** The Wednesday September 9 class was cancelled, so this
> milestone opens on Monday September 14 instead and is due one week later.
> M2 opens on Wednesday September 16 and is due Monday September 28.

---

## The idea

M0b gave you the two values the game is written in: `Position`, a square, and
`Color`, a side. M1 builds the things that *hold* them — the board and the
pieces standing on it — and ends with a chessboard printed from your own
terminal.

Three classes, all in `edu.sfsu.csc413.chess.model`, all written by you:

1. **`PieceType`** — an enum of the six kinds, each carrying its FEN letter,
   and the lookup that reads a letter back.
2. **`Piece`** — a color and a type. Immutable. It knows *what it is*, not yet
   how it moves.
3. **`Board`** — an 8×8 grid that *has* an array rather than *being* one.

Plus two classes you are **given**, in `edu.sfsu.csc413.chess.view`, which you
read but do not edit: `PieceGlyphs` and `TextBoardRenderer`. They draw the
board; the model does not.

Unlike M0b there are **no stubs**. The merge brings tests, the two view files,
and nothing else; the three model classes are written from empty files. The
design was settled in class —
[session 5 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-05-domain-model/notes),
§§1–4 — and the tests specify the rest.

---

## Getting the milestone

The [weekly loop](https://goleador.github.io/CSC413/guide.html?g=git-workflow):

```bash
git fetch upstream --tags
git merge m1
./mvnw test        # a compile error in PieceGlyphs.java naming Piece — that error is the assignment
```

If you are working in a group, one member merges and pushes; the others pull.

The first error is in a file you did not write: the given view is asking for
the model it draws. Write the class it names.

---

## What to build, in this order

Each step makes the next one's failures readable. Do not skip ahead.

**1. `PieceType`** — the six constants, each with its uppercase letter:
`P` `N` `B` `R` `Q` `K` — `N` for knight, because the king already has `K`.
A `private final char symbol` field, the constructor, `symbol()`, and
`static PieceType fromSymbol(char letter)`: the inverse, accepting either
case and throwing `IllegalArgumentException` for a letter that names no piece.
Session 5 §2.

**2. `Piece`** — `color` and `type`, both `final`. A constructor
`Piece(Color color, PieceType type)` — **color first**; next week's subclasses
depend on the order — accessors `color()` and `type()`, `symbol()` (uppercase
for white, lowercase for black), and `toString()` returning the symbol as a
one-character string. Session 5 §1–2. No `hasMoved`, no position, no movement.

**3. `Board`** — a `private final Piece[][] squares`, indexed `[file][rank]`,
both 0-based, the same order as `Position`. Then `pieceAt`, `isEmpty`,
`place`, and `positionsOf`:

```java
public Board()                                       // empty
public Piece pieceAt(Position position)              // null if the square is empty
public boolean isEmpty(Position position)
public void place(Position position, Piece piece)    // replaces; null clears
public List<Position> positionsOf(Color color)       // every square holding that color
```

Every one of them takes a `Position`, never two ints: M0b already guarantees a
`Position` is on the board, so none of these needs a bounds check. Session 5 §3.

**4. `Board.toString()`** — the FEN placement field: ranks 8 down to 1
separated by `/`, a letter per piece, a digit for each run of empty squares.
An empty board is `8/8/8/8/8/8/8/8`; the starting position is
`rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR`. This is the only place in
`Board` that counts ranks downward. Session 5 §4.

**5. `Main`** — set up the starting position and print it. Sixteen pieces a
side; write it as a loop over the files with a back-rank array, not
thirty-two calls to `place`. Then:

```java
System.out.println(new TextBoardRenderer(PieceGlyphs.LETTERS).render(board));
```

```
8 r n b q k b n r
7 p p p p p p p p
6 . . . . . . . .
5 . . . . . . . .
4 . . . . . . . .
3 . . . . . . . .
2 P P P P P P P P
1 R N B Q K B N R
  a b c d e f g h
```

`Main` is not tested. It is the point of the milestone.

---

## What "done" looks like

From inside your repository:

```bash
./mvnw test
```

ends green:

```
Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Twenty-four: three from `ToolchainTest`, five from `PositionTest`, three from
`ColorTest` — **all of M0b's eleven still passing** — plus thirteen new ones:
three in `PieceTypeTest`, three in `PieceTest`, seven in `BoardTest`.

That M0b's tests still run is the point. A milestone that breaks an earlier
milestone is not done; keeping old tests green while adding new code is what
regression testing means, and it is why the suite grows all semester.

---

## What you submit

```bash
git tag submit-m1
git push origin main --tags
```

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m1`.

---

## How it is graded

| Criterion | Weight |
|---|---|
| All twenty-four tests green (`./mvnw test`, checked by clone-and-run) | 60% |
| M0b's eleven tests still passing — no edits to `Position` or `Color` | 10% |
| `Board`'s array is `private` and reachable only through its methods | 10% |
| No rule logic, printing, or legality checks in `model`; the two `view` files unedited | 10% |
| The signatures above unchanged — M2 builds on them | 5% |
| `submit-m1` tag pushed | 5% |

The design criteria are graded by reading, not by the tests, and they are the
ones this milestone is really about. `Board` may not print, may not decide
whether a move is legal, and may not hand out its array. If a method of yours
would fail the single-sentence test — *"`Board` stores which piece is on which
square"* — it belongs in a class that does not exist yet, which means it does
not belong in M1.

---

## Common problems

- **`cannot find symbol: class Piece` in `PieceGlyphs.java`** (or
  `class Board` in `TextBoardRenderer.java`) — a given file asking for a
  class you have not written yet. Write it, in `model`, with that exact name.
- **The board prints mirrored or upside-down** — the renderer is given and
  correct, so `place` or `pieceAt` has file and rank swapped. `placeThenPieceAt`
  says so: `g1` is file 6, rank 0; `a7` is what you get with the two swapped.
- **`emptyBoardToStringIsEightEights` fails** — a digit per empty square
  instead of per *run*; or the digit at the end of a rank is missing; or there
  is a `/` after rank 1.
- **`NullPointerException` in `toString` or `positionsOf`** — you called
  `piece.symbol()` or `piece.color()` without a null check. Thirty-two squares
  are empty at the start, and `null` is what empty *is*.
- **`ArrayIndexOutOfBoundsException: 8`** — a loop using `<=` where it needs
  `<`.
- **Both kings print as `K`** — `Piece.symbol()` is not lowercasing for black.
- **M0b's tests suddenly fail** — you edited `Position` or `Color`. Those
  signatures are fixed contracts; revert the change and solve it in `Board`.

---

## A note on scope

You will be tempted to write `movePiece`, or a `canMove`, or a `switch` on piece
type that returns where a piece may go. **Do not.** That is M2, it opens on
Wednesday September 16, and it is built with inheritance and polymorphism rather
than a switch — which is exactly the lesson. Code written now in the wrong
shape gets deleted next week.

Likewise no `hasMoved` flag, no `remove`, and no `startingPosition()` on
`Board`: each of those has a home in a later milestone, and it is not here.

M1's `Piece` knows *what it is*. It does not yet know *how it moves*.
