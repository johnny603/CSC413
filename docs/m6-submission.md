**File:** m6-submission.md
**Course:** CSC 413 Software Development
**Milestone:** M6
**Objectives advanced:** 2 (interfaces, dependency inversion), 3 (responsibility assignment), 5 (Command), 6 (separate presentation from business logic with MVC), 7 (test a medium-sized system)
**Assigned:** Monday, November 2
**Due:** Monday, November 9, 11:59 PM

### Who decides, who shows?
The Model (MoveGenerator.status()) decides whether the game is in checkmate by checking the king's safety and available legal moves. 
The Controller (GameController) detects the resulting CHECKMATE status and tells the View (BoardView, implemented by ConsoleView) to display the checkmate message. 
This follows MVC: the Model manages game rules and state, the View displays information, and the Controller coordinates interactions between them.

### The dependency rule
Main connects the engine to the view, so it imports classes from edu.sfsu.csc413.chess.view. 
GameController works with the BoardView interface and other view-related classes. 
Classes in engine, model, and factory should not depend on the view because game logic and data should remain independent of how the game is displayed.

### Test 2 on my engine
In M7, I would add a new SwingView class that implements BoardView and update Main to allow users to select it. 
I would not need to change GameController or anything in engine because the controller already communicates through the BoardView interface. 
This allows a new graphical interface to reuse the existing game logic and controller.