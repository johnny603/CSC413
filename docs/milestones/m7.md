# M7 — Views, Generated and Compared

**Course:** CSC 413 Software Development
**Milestone:** M7 · Week 12
**Objectives advanced:** 2 (interfaces, dependency inversion), 6 (separate presentation from business logic with MVC), 7 (test, debug, and maintain a medium-sized system), 8 (use professional tools)
**Assigned:** Monday, November 9
**Due:** Monday, November 16, 11:59 PM

---

## The idea

You wrote the model and the controller by hand. In M7 a coding agent writes
the views, and you decide what stays.

The test of your design from session 16 is the acceptance test here: **a new
view changes nothing else.** If a Swing window fits behind `BoardView` with
no change to your controller or your rules, your split works.

The work is a loop:

1. **Generate** a view with written instructions.
2. **Check** it with checks you can run.
3. **Compare** it with another run.
4. **Apply** what you learned, and write down what you did.

Background:
[session 16 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-16-mvc/notes)
(the rules and the checks),
[session 17 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-17-ai-views/notes)
(instructions, reviewing, comparing runs), and
[session 18 notes](https://goleador.github.io/CSC413/guide.html?d=lectures/session-18-debugging/notes)
(stack traces, the debugger, logging).

---

## Before you start

**M6 has to work.** You need `./mvnw test` at 83 green and a game you can
play to checkmate in the console. If it doesn't, finish M6 first, by hand.
An agent may not write or change `GameController`, in M6 or in M7.

If you fix an M6 bug after `submit-m6`, do it by hand, before you generate
anything, in its own commit with a message starting `M6 fix:`. Say so in
`m7-submission.md`.

---

## Getting the milestone

```bash
git fetch upstream --tags
git merge m7
./mvnw test
```

Only this handout arrives (`docs/milestones/m7.md`). There is no new code
and there are no new tests: still 83, all green.

---

## What to build

| An agent generates | You must not change |
|---|---|
| A Swing view | `model/`, `engine/`, `factory/` |
| At least one other view | `GameController` |
| Tests for the views, if you want them | `BoardView`, `PlayerAction`, `ConsoleView`, `EmojiView` |
| | `pom.xml` |

A coding agent running in your repository is the goal. A chat is allowed,
and the checks are the same. You are also free to edit generated code by
hand.

**The Swing view.** It runs with:

```bash
./mvnw -q compile
java -cp target/classes edu.sfsu.csc413.chess.Main swing
```

- An 8×8 board. Every piece is easy to see on both square colours.
- You play by clicking a piece, then the square to move it to.
- It shows whose turn it is, check, checkmate, stalemate, and illegal-move
  messages.
- An Undo button sends `new PlayerAction.Undo()`.
- A promotion doesn't crash. Always choosing a queen is acceptable.
- `start` builds the window and returns. It never calls
  `controller.runLoop()`.

**The other view.** Any `BoardView` that is not Swing and is more than a new
glyph set for the existing views: its own class, playable to the end of a
game through your controller, chosen by a word on the command line. Name that
word in `m7-submission.md`. It must use only the JDK. Some ideas:

- a terminal board with a border, drawn from the side to move;
- a terminal board in ANSI colour;
- a view that plays a game from a file of moves;
- a page in a browser, served with the JDK's `com.sun.net.httpserver`.

---

## Your instructions file

Start from the `AGENTS.md` in
[session 17 §3](https://goleador.github.io/CSC413/guide.html?d=lectures/session-17-ai-views/notes),
with the corrected rule about modal dialogs from §5. Change it to fit your
engine: your `Main` may not have a `chooseView` method, and your package
layout may differ. Commit it before your first run.

Claude Code reads `CLAUDE.md`; a one-line `CLAUDE.md` containing `@AGENTS.md`
is enough. In a chat, paste the file at the top of your message.

---

## Groups and runs

Groups have one to three people, and you choose your own. Everyone keeps
their own repository and submits individually. Name your groupmates in
`m7-submission.md`; that is the sign-up.

Each run gets its own branch and a fresh agent session, starting from your
`main`:

```bash
git switch main
git switch -c m7-run-1
```

| | In a group of 2 or 3 | Alone |
|---|---|---|
| **Runs** | at least 1 each | at least 3 |
| **Compare** | your view with each groupmate's | your own runs, one change between each |
| **Apply** | at least one idea from a groupmate | at least one idea from a run you did not keep |

**One change per run**, if you make more than one: the model or one
instruction, not both.

**Reading a groupmate's code.** Give each other read access: on GitHub, your
repository → Settings → Collaborators. Read their code yourself.

- Do not copy it. Adopting an idea means writing it yourself and citing it.
- Do not give it to an agent or a chat. The syllabus does not allow putting
  another student's work into an AI service.

Merge the view you keep into `main`.

---

## Check every run

| Rule | Check |
|---|---|
| The model runs with no screen | `./mvnw test`: 83 or more green, no test changed or deleted |
| A new view changes nothing else | `git diff --stat submit-m6 HEAD`, explained below |
| The view never changes the model | search the new files for `game.play` and `undoLastMove`: no matches |
| The view never works out a rule | read it: legal moves come from `game.legalMoves()`, check and checkmate from `game.status()` |
| A window never waits inside a click | search for `runLoop` in the Swing view: no matches |
| It works | play Fool's Mate (`f2f3`, `e7e5`, `g2g4`, `d8h4`), one illegal move, and an Undo |

The diff should list only these: new files under `view/` (and new tests under
`src/test/.../view/`), `Main.java`, `AGENTS.md`, `CLAUDE.md`,
`m7-submission.md`, `docs/milestones/m7.md`, and any `M6 fix:` commit you
declared.

**Keep the console open while you test a window.** An exception inside a
click prints a stack trace on `AWT-EventQueue-0`, and the window keeps
running. The tests do not open your window, and the agent may not have
either.

---

## Own what you keep

Change one behaviour of your Swing view **by hand, with no agent**, in its
own commit with a message starting `M7 by hand:`. For example, draw the
board from black's side, change the highlight, or add coordinates. You will
explain your view in the final defense (M10).

---

## `m7-submission.md`

Add it at the top of your repository. It is also your AI-use disclosure.
Specific beats long: a file and line number shows you read the code.

1. **Group.** Your groupmates' names and GitHub usernames, or "alone".
2. **Tools.** The agent or chat, and the model.
3. **Other view.** The command-line word that starts it.
4. **Runs.** For each: the branch, the request you gave, word for word, and
   what you changed from the previous run.
5. **`AGENTS.md`.** One rule you added or changed, and why: something a run
   did, or something you wanted to make sure a run wouldn't do.
6. **Review.** Choose at least three places in the generated code that matter,
   each with a file and line. For each: what it does, how you checked it, and
   your decision (kept, changed, or rejected) and why. A well-checked "kept"
   counts the same as a fix. Don't invent problems.
7. **Comparison.** For the views you compared, with file and line:
   - Where does the state of a half-finished move (the selected square) live?
   - How does each view find out which moves are legal?
   - What does each do on a promotion?
   - Which is easier to change, and why? Which did you keep?
8. **Applied.** What you took, from whom or from which run, and where it is in
   your code.
9. **Debugger evidence.** If a view had a real bug, describe it: the symptom,
   the evidence (the stack trace frame you opened, the breakpoint and what it
   showed, or the log line), the cause, and the fix. If you found none, set a
   breakpoint in the click handler, follow one move through it, and report
   what you predicted and which values you saw.
10. **By hand.** The commit, and what you had to read to make the change.

---

## What you submit

Push your run branches as well as `main`. `--tags` does not push branches.

```bash
git add -A
git commit -m "M7: <what you did>"
git tag submit-m7
git push origin main m7-run-1 --tags      # list every m7-run branch
```

**Commit before you tag.** `git status` should be clean first.

**The tag is the submission.** Verify on GitHub: your repository → Tags →
`submit-m7`, and your branches are listed.

---

## How it is graded

| Criterion | Weight |
|---|---|
| Tests green with none changed or deleted (`./mvnw test`, clone-and-run) | 10% |
| The diff: nothing outside the list above | 20% |
| Swing view meets the list, and the view rules hold | 20% |
| The other view is playable and meets the description | 10% |
| `m7-submission.md`: specific, with file and line, and matches your code and branches | 30% |
| `M7 by hand:` commit, written by you | 5% |
| `submit-m7` tag and run branches pushed | 5% |

---

## Common problems

- **`UnsupportedOperationException: SwingView is event-driven, so it does not
  answer nextAction()`** — the view called `controller.runLoop()`. Its `start`
  should build the window and return.
- **A `NullPointerException` from inside `Board` or `MoveGenerator` when the
  window opens, different from run to run** — the same mistake. `runLoop` on
  `main` and `render` on Swing's thread are using one `Game` at once. Don't
  change the model; remove the `runLoop` call.
- **The window is blank, or the board never appears** — the view may be
  drawing before `render` has been called. Put a breakpoint in `render` and see
  if it arrives.
- **Clicks do nothing, and there's a trace on `AWT-EventQueue-0`** — find the
  first frame in your own package and start there.
- **Every click says it is the wrong square or piece** — check the conversion
  from mouse coordinates to a `Position`. Screen rows count down; ranks count
  up.
- **The window freezes** — something is waiting inside a click handler, such
  as a loop or a blocking read.
- **`HeadlessException`** — Java can't reach a display. If you are on WSL or
  an SSH session, run it from IntelliJ on your own desktop.
- **`git diff` lists a file from the "must not change" column** — restore
  it with `git restore --source=submit-m6 <file>` and find out why the agent
  changed it. Tell it to stop and ask, in `AGENTS.md`.
- **A merge conflict in `Main.java` when you merge your chosen run** — you
  changed `Main` on `main` after the branch started. Keep both view cases.

---

## A note on scope

Still no castling and no en passant; those are M9. The rules stay in the
model, and any new rule is a change to the model, which M7 doesn't allow.
A computer opponent is a bonus, and more views are in the
[bonus handout](https://goleador.github.io/CSC413/guide.html?d=assignments/bonus-alternative-views/handout).
