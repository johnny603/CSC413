package edu.sfsu.csc413.chess.model;
import java.util.List;
import java.util.ArrayList;

public abstract class Piece {
    // remember color first, subclasses depend on order
    private final Color color;
    private final PieceType type;

    // constructor
    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public abstract List<Move> pseudoLegalMoves(Board board, Position from);

    // Pawn would Override this as it attacks differently
    /** True if this piece could capture an enemy standing on {@code target}. */
    public boolean attacks(Board board, Position from, Position target) {
        for (Move move : pseudoLegalMoves(board, from)) {
            if (move.to().equals(target)) {
                return true;
            }
        }
        return false;
    }

    protected List<Move> slidingMoves(Board board, Position from, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        // consider each direction, not each square which takes up space and time
        for (int[] direction : directions) {
            // where a piece moved from
            int fileFrom = from.file();
            int rankFrom = from.rank();

            // it CAN repeatedly move in a direction until a condition is met
            while (true) {
                fileFrom += direction[0];
                rankFrom += direction[1];
                // positions are available
                if (!Position.isOnBoard(fileFrom, rankFrom)) {
                    break;
                }
                // make a new position and check it on the board
                Position target = new Position(fileFrom, rankFrom);
                Piece targetPiece = board.pieceAt(target);
                // check target point
                if (targetPiece == null) {
                    moves.add(Move.quiet(from, target, this));
                }
                else if (targetPiece.color() != color()) {
                    moves.add(Move.capture(from, target, this, targetPiece));
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    protected List<Move> steppingMoves(Board board, Position from, int[][] offsets) {
        List<Move> moves = new ArrayList<>();
        // for each offset
        for (int[] offset : offsets) {
            // get the starting point
            int fileFrom = from.file();
            int rankFrom = from.file();

            // target point, "steps"
            int fileTarget = fileFrom + offset[0];
            int rankTarget = rankFrom + offset[1];
            // positions are available
            if (!Position.isOnBoard(fileTarget, rankTarget)) {
                continue;
            }
            // check target point
            Position target = new Position(fileTarget, rankTarget);
            Piece targetPiece = board.pieceAt(target);
            if (targetPiece == null) {
                moves.add(Move.quiet(from, target, this));
            } else if (targetPiece.color() != color()) {
                moves.add(Move.capture(from, target, this, targetPiece));
            }
        }
        return moves;
    }

    // accessors
    public Color color() { return color; }
    public PieceType type() { return type; }
    public char symbol() {
        char letter = type.symbol();
        return color == Color.WHITE
                ? letter : Character.toLowerCase(letter);
    }

    // toString
    @Override
    public String toString() { return String.valueOf(symbol()); }
}

