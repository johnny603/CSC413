package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The pawn — the piece that breaks every rule the others follow.
 *
 * <p>It is the only piece that moves in just one direction, the only one whose
 * capture differs from its move, the only one with a special first move, and
 * the only one that turns into something else. It is worth noticing that all of
 * that awkwardness is contained in this one file. No other class in the engine
 * knows that pawns are strange. That containment is the payoff of polymorphism:
 * the irregular case costs one class, not a special case in every method that
 * touches a piece.
 *
 * <p>En passant is not handled here. Like castling, it depends on the previous
 * move rather than on the current board, so it waits for Week 15 when
 * {@code Game} owns the move history.
 */
public class Pawn extends Piece {

    /**
     * What a pawn may become on reaching the far rank.
     */
    // must use when the pawn moves
    private static final PieceType[] PROMOTION_CHOICES = { PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT };

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        // possible ways the Pawn can move
        final int direction = color() == Color.WHITE ? 1 : -1;
        final int oneStepRank = from.rank() + direction;
        final int twoStepRank = from.rank() + direction * 2;
        boolean onStartingRank = (color() == Color.WHITE && from.rank() == 1) || (color() == Color.BLACK && from.rank() == 6);

        // make sure types match
        Position oneStep = new Position(from.file(), oneStepRank);

        // can only move one step ONCE
        if (board.isEmpty(oneStep)) {

            // can promote via one step
            boolean promotes = (color() == Color.WHITE && oneStep.rank() == 7) || (color() == Color.BLACK && oneStep.rank() == 0);

            if (promotes) {
                for (PieceType promotion : PROMOTION_CHOICES) {
                    moves.add(Move.promotion(from, oneStep, this, null, promotion));
                }
            } else {
                moves.add(Move.quiet(from, oneStep, this));
            }

            if (onStartingRank) {
                Position twoStep = new Position(from.file(), twoStepRank);
                if (board.isEmpty(twoStep)) {
                    moves.add(Move.quiet(from, twoStep, this));
                }
            }
        }

        // consider diagonal moves
        for (int fileDelta : new int[] { -1, 1 }) {
            int targetFile = from.file() + fileDelta;
            int targetRank = from.rank() + direction;
            if (!Position.isOnBoard(targetFile, targetRank)) {
                continue;
            }
            Position target = new Position(targetFile, targetRank);
            Piece targetPiece = board.pieceAt(target);
            if (targetPiece != null && targetPiece.color() != color()) {
                moves.add(Move.capture(from, target, this, targetPiece));
            }
        }
        return moves;
        }

    /**
     * A pawn attacks the two squares diagonally ahead of it, whether or not
     * anything stands there.
     *
     * <p>This override exists because the inherited version answers "can this
     * piece move to that square", and for a pawn that is the wrong question.
     * An empty square in front of a pawn is a square the pawn can move to but
     * does <em>not</em> attack — which matters enormously for king safety: a
     * king may not be blocked from a square merely because a pawn could advance
     * onto it, but it certainly may not step onto a square a pawn guards.
     */
    @Override
    public boolean attacks(Board board, Position from, Position target) {
        final int direction = color() == Color.WHITE ? 1 : -1;

        // checks diagonally if it can capture
        for (int fileDelta : new int[] { -1, 1 }) {
            int targetFile = from.file() + fileDelta;
            int targetRank = from.rank() + direction;

            if (!Position.isOnBoard(targetFile, targetRank)) {
                continue;
            }

            Position attackSquare = new Position(targetFile, targetRank);

            if (attackSquare.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
