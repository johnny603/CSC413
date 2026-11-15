**File:** m7-submission.md
**Course:** CSC 413 Software Development
**Milestone:** M7
**Objectives advanced:** 2 (interfaces, dependency inversion), 6 (separate presentation from business logic with MVC), 7 (test, debug, and maintain a medium-sized system), 8 (use professional tools)
**Assigned:** Monday, November 9
**Due:** Monday, November 16, 11:59 PM

--

### Iteration 0
1. Alone for now
2. Cursor model Grok 4.7
3. Other views planned: Http, Bordered, Glyph, 3D
4. Typed the scaffold and planned a prompt for the agent in AGENTS.md
5. A rule to read the game only though game and to update README.md to explain the design added to the scaffold
6. Did not run AI yet
7. No comparison yet
8. No applications yet
9. No debugging issues yet
10. I read the handout and the emoji view to make the scaffold

--

### Iteration 1
1. Alone
2. Cursor model Grok 4.7
3. The model built the views from the scaffold
4. Prompt:
Add views. I want to play by clicking a piece and then clicking the square to move it to. Show whose turn it is and messages like check or illegal moves. Follow AGENTS.md.
6. First I manually checked main BoardView, which had a switch expression taking in string values used to run a specific view. I checked all using the maven wrapper test, which all passed. I did further tests with this flow:
./mvnw -q compile
java -cp target/classes edu.sfsu.csc413.chess.Main
java -cp target/classes edu.sfsu.csc413.chess.Main name
7. Swings promotion always defaults to queen, which is acceptable
Issues:
- 3dview is not 3D in the terminal, but is just a tilted 2d view
- Borderedview is just making a border across the perimeter

--

### Iteration 2




