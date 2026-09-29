package edu.sfsu.csc413.chess.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {
    // has-a board
    private final Board board;

    // has-a move history
    private final List<Move> history = new ArrayList<>();

    // has-a sideToMove, which alternates
    private Color sideToMove;

    // standard position, white to move first
    public Game() {
        this.board = new Board();
        this.sideToMove = Color.WHITE;
    }

    // any position; tests use this
    public Game(Board board, Color sideToMove) {
        this.board = board;
        this.sideToMove = sideToMove;
    }

    // defined in Board class (2d array)
    public Board board() {
        return board;
    }

    // order defined in color class, do not need to here
    public Color sideToMove() {
        return sideToMove;
    }


    // defined in Moves class using the List interface
    public List<Move> history() {
        return List.copyOf(history);
    }

    // §2's loop, for sideToMove
    public List<Move> legalMoves() {
        throw new UnsupportedOperationException("M3: Not implemented yet");
    }

    public Optional<Move> findLegalMove(String notation) {
        throw new UnsupportedOperationException("M3: Not implemented yet");
    }

    // throws if not in legalMoves()
    public void play(Move move)     {
        throw new UnsupportedOperationException("M3: Not implemented yet");
    }

    public Optional<Move> undoLastMove() {
        throw new UnsupportedOperationException("M2: Not implemented yet");
    }
}