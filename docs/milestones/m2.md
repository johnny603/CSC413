# M2 — The `Piece` Hierarchy

**Course:** CSC 413 Software Development
**Milestone:** M2 · Week 4
**Objectives advanced:** 2 (inheritance, polymorphism, abstraction, interfaces), 3 (analyze designs for extensibility), 5 (a first look at the Factory pattern)
**Assigned:** Wednesday, September 16
**Due:** Monday, September 28, 11:59 PM

---

## The idea

M1's `Piece` knows what it is. M2 asks it where it can go — and the answer is
not a `switch`.

`Piece` becomes an **abstract** base class with one promise every kind of piece
must keep, `pseudoLegalMoves(board, from)`, and six subclasses keep it:
`Knight`, `Bishop`, `Rook`, `Queen`, `King`, `Pawn`. A `Move` record says what
a move is. A given `PieceFactory` is the one place that says `new` for a piece,
and a given `BoardFactory` uses it to set up a game.

When you are done, nothing in the engine asks a piece what kind it is. It asks
the piece for its moves, and the piece answers for itself. The design was
settled in class —
[session 6 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-06-piece-hierarchy/notes)
— and Monday September 21's session does the `Piece` refactor and `Knight`
live.

"Pseudo-legal" means geometrically correct, ignoring whether the move would
leave your own king in check. King safety is M5. Castling and en passant depend
on the game's history rather than the board, and are M12.

---

## Getting the milestone

The [weekly loop](https://goleador.github.io/CSC413/guide.html?g=git-workflow):

```bash
git fetch upstream --tags
git merge m2
./mvnw test        # seven copies of the same error — that error is the assignment
```

If you are working in a group, one member merges and pushes; the others pull.

**What arrives:**

- **Seven scaffolds** in `model`: `Move`, `Knight`, `Bishop`, `Rook`, `Queen`,
  `King`, `Pawn`. Signatures, javadoc, and the direction tables are there;
  every body throws `UnsupportedOperationException("M2: implement ...")`.
- **Two given classes** in a new `factory` package: `PieceFactory` and
  `BoardFactory`. Working. Read them; do not edit them.
- **Ten new tests** in `PieceMovementTest`, and a one-line change to each of
  `PieceTest` and `BoardTest`: the helper that said `new Piece(color, type)`
  now says `PieceFactory.create(type, color)`, because there is no such thing
  as `new Piece` once the class is abstract.

**What does not arrive:** `Piece` and `Board`. They are yours from M1 and stay
yours. You edit `Piece`; `Board` is unchanged this milestone.

Right after the merge:

```
Knight.java: method does not override or implement a method from a supertype
```

seven times. Your `Piece` has no `pseudoLegalMoves` for the subclasses to
override yet.

---

## What to build, in this order

**1. Refactor `Piece`** into the abstract base. Session 6 §§2, 4, 5.

```java
public abstract class Piece {
    protected Piece(Color color, PieceType type)                    // was public

    public abstract List<Move> pseudoLegalMoves(Board board, Position from);
    public boolean attacks(Board board, Position from, Position target)   // default: "can I move there?"

    protected List<Move> slidingMoves(Board board, Position from, int[][] directions)
    protected List<Move> steppingMoves(Board board, Position from, int[][] offsets)
}
```

`color()`, `type()`, `symbol()`, and `toString()` are unchanged. The two
helpers are the shared movement families: sliders glide along each direction
until the edge or a piece (capturing an enemy, blocked by a friend); steppers
try each offset once. Both use `Position.offsetOrNull` and `Board.pieceAt`,
nothing else.

The build now fails in `Main` instead: *"Piece is abstract; cannot be
instantiated."*

**2. `Main`** — replace M1's setup loop with `BoardFactory.standard()`. Delete
the loop. The suite now runs: `Tests run: 34, Failures: 0, Errors: 10`.

**3. `Move`** — the three static factories (`quiet`, `capture`, `promotion`),
`isCapture`, `isPromotion`, and `toString` in long algebraic notation:
`e2e4`, and `e7e8q` for a promotion, with the promotion letter lowercase.
Session 6 §7. Flips nothing by itself; every piece needs it.

**4. `Knight` and `King`** — one line each, on `steppingMoves` with the table
the scaffold gives you. Three tests turn green: `Errors: 7`.

**5. `Rook`, `Bishop`, `Queen`** — one line each, on `slidingMoves`.
`Errors: 5`.

**6. `Pawn`** — `pseudoLegalMoves` and the `attacks` override. The real work
of the milestone:

- one square forward, onto an empty square only;
- two squares forward from the starting rank, only if both squares are empty;
- captures diagonally forward, onto an enemy only — never straight ahead, and
  never diagonally onto an empty square;
- a move onto the last rank becomes **four** moves, one per promotion choice
  (queen, rook, bishop, knight), built with `Move.promotion`;
- `attacks` is geometry — one rank forward, one file to either side — and
  does not consult `pseudoLegalMoves`. A pawn attacks the diagonal whether or
  not anything stands there, and does not attack the square ahead.

`Color.pawnDirection()`, `pawnStartRank()`, and `promotionRank()` from M0b are
what these are written with.

---

## What "done" looks like

```bash
./mvnw test
```

```
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Thirty-four: M0b's eleven and M1's thirteen, **all still passing**, plus ten
in `PieceMovementTest`.

Then `./mvnw compile exec:java -Dexec.mainClass=edu.sfsu.csc413.chess.Main`
prints the same board as M1 — from `BoardFactory.standard()` this time, in
one line.

---

## What you submit

```bash
git tag submit-m2
git push origin main --tags
```

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m2`.

---

## How it is graded

| Criterion | Weight |
|---|---|
| All thirty-four tests green (`./mvnw test`, checked by clone-and-run) | 55% |
| M0b's and M1's twenty-four still passing — `Position`, `Color`, `Board` signatures untouched | 10% |
| `Piece` is `abstract`; the movement helpers live on the base class, not in an intermediate subclass | 10% |
| No `switch` or `instanceof` on piece type anywhere in `model` — pieces answer for themselves | 10% |
| The scaffolded signatures unchanged; the given `factory` files unedited | 10% |
| `submit-m2` tag pushed | 5% |

The two design criteria are graded by reading. If a method of yours asks
`piece.type()` in order to decide how something moves, the thing it decides
belongs inside that piece's class. The one `switch` on `PieceType` that is
allowed in the project is the one in `PieceFactory`, and it was given to you.

---

## Common problems

- **"method does not override or implement a method from a supertype" ×7** —
  `Piece` has no `pseudoLegalMoves`, or its signature differs. It must be
  exactly `public abstract List<Move> pseudoLegalMoves(Board board, Position
  from)`. Same for `attacks`, if `Pawn` is the one complaining.
- **"Piece is abstract; cannot be instantiated"** — a `new Piece(...)` survives
  somewhere, usually `Main`. Use the factory.
- **Merge conflict in `PieceTest` or `BoardTest`** — you edited a test file.
  Take the incoming version: `git checkout --theirs <file>`, then `git add`.
- **`knightInCentre` passes, `knightInCorner` throws** — you built
  `new Position(file + dx, rank + dy)`, which throws off the board, instead of
  `from.offsetOrNull(dx, dy)`, which returns null.
- **`rookBlocking`: "must not slide past a capture"** — add the capture, *then*
  `break`. You cannot slide through a piece you just took.
- **`queenCombinesDirections` gets 14 or 13, not 27** — one table. The queen
  has eight directions of her own; do not borrow the rook's and bishop's.
- **`pawnBlocked`: the pawn jumped the blocker** — check the one-step square
  is empty *before* considering the two-step.
- **`pawnPromotes` gets 1, not 4** — the last rank expands into four moves.
- **`pawnAttacksDifferFromMoves` fails** — `Pawn.attacks` must not call
  `pseudoLegalMoves`.
- **`e7e8q` prints as `e7e8Q`** — `Move.toString` lowercases the promotion
  letter.
- **M1's tests now fail** — you changed `symbol()`, `place`, or `pieceAt`
  while refactoring. Those are fixed; revert.

---

## A note on scope

No king safety, no check, no castling, no en passant, and no turn-taking.
`pseudoLegalMoves` answers a geometric question about one piece on the current
board; everything that depends on the *whole* board (is my king attacked?) or
on the *history* (has this rook moved? what was the last move?) belongs to a
class that does not exist yet. M5 and M12 add them — and the fact that they
will add them without editing `Knight`, `Bishop`, `Rook`, or `Queen` is the
point of the design you are building now.

Do not add an intermediate `SlidingPiece` or `SteppingPiece` class. Session 6
§4 says why; the graded criterion above says it counts.
