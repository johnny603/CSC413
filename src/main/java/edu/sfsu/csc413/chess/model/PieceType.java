package edu.sfsu.csc413.chess.model;

// small finite set
public enum PieceType {

    // consts
    PAWN('P'), KNIGHT('N'), BISHOP('B'), ROOK('R'), QUEEN('Q'), KING('K');


    // symbol()
    private final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }

    /** The uppercase letter for this type, as used in FEN and algebraic notation. */
    public char symbol() {
        return symbol;
    }


    // fromSymbol()
    /** The inverse: the type for a letter, in either case. Throws if it names no piece. */
    public static PieceType fromSymbol(char letter) {
        letter = Character.toUpperCase(letter);

        for (PieceType type : values()) {
            if (type.symbol == letter) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown piece symbol: " + letter);
    }
}


