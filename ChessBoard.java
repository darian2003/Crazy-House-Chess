import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessBoard {
    //jos in matrice sunt piesele albe
    /* r k b k q b k r | rook knight bishop king queen bishop knight rook
       p p p p p p p p | pawn pawn pawn pawn pawn pawn pawn pawn
       . . . . . . . .
       p p p p p p p p | pawn pawn pawn pawn pawn pawn pawn pawn
       r k b k q b k r | rook knight bishop king queen bishop knight rook
     */
    BoardPiece [][]board;
    public ChessBoard(ChessBoard auxBoard) {
        this.board = new BoardPiece[8][8];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = auxBoard.board[i][j];
            }
        }
    }
    //Print the Board
    public void printChesssBoard () {
        for (int i = 0; i < 8; i ++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j] == null) {
                    System.out.print("-");
                } else {
                    String piece = switch (board[i][j].getPiece()) {
                        case KING -> "x";
                        case QUEEN -> "q";
                        case ROOK -> "r";
                        case KNIGHT -> "k";
                        case BISHOP -> "b";
                        case PAWN -> "p";
                    };

                    if(board[i][j].getColor() == PlaySide.BLACK)
                        piece = piece.toUpperCase();
                    System.out.print(piece);
                }
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }
    public ChessBoard(ArrayList<BoardPiece> whitePieces, ArrayList<BoardPiece> blackPieces) {
        board = new BoardPiece[8][8];
        for (int j = 0; j < 8; j++) {
            board[1][j] = new BoardPiece(Piece.PAWN, PlaySide.BLACK, 1, j);
            blackPieces.add(board[1][j]);
            board[6][j] = new BoardPiece(Piece.PAWN, PlaySide.WHITE, 6, j);
            whitePieces.add(board[6][j]);
        }
        Piece []specialPieces = {Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.KING, Piece.QUEEN, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK};
        for (int j = 0; j < 8; j++) {
            board[0][j] = new BoardPiece(specialPieces[j], PlaySide.BLACK, 0, j);
            blackPieces.add(board[0][j]);
            board[7][j] = new BoardPiece(specialPieces[j], PlaySide.WHITE, 7, j);
            whitePieces.add(board[7][j]);
        }
    }

    /* returns the board piece at given position, or null if that square is free */
    public BoardPiece getBoardPiece(Position position) {
        if (isFree(position)) {
            System.out.println("in getBoardPiece: square is free");
            return null;
        }
        return board[position.getX()][position.getY()];
    }

    /* returneaza true daca nu exista nicio piesa pe pozitia precizata si false altfel */
    public boolean isFree(Position position) {
        if (this.board[position.getX()][position.getY()] == null)
            return true;
        return false;
    }
    /* returneaza true daca pozitia nu depaseste limitele tablei de sah si false altfel */
    public boolean isInBounds(Position position) {
        int x = position.getX();
        int y = position.getY();
        if (x >= 0 && x <= 7 && y >= 0 && y <= 7)
            return true;
        return false;
    }

    /* returns true if there is an opponent's piece on that square and false otherwise */
    public boolean isEnemy(Position position, PlaySide opponentColor) {
        BoardPiece boardPiece = getBoardPiece(position);
        if (boardPiece == null)
            return false;
        if (boardPiece.getColor() == opponentColor)
            return true;
        return false; // the piece on that square is our color
    }

    /* returns true if a piece at position piecePosition can be captured by an attacking piece, else false */
    public boolean canBeCaptured(ArrayList<Pair> attackingPositions, Position piecePosition) {
        for(Pair p: attackingPositions) {
            if(p.getSecond() == piecePosition)
                return true;
        }
        return false;
    }

    /* returns the Move neccessary to capture an enemy piece */
    public Pair capture(ArrayList<Pair> attackingPositions, Position piecePosition) {
        for(Pair p: attackingPositions) {
            if(p.getSecond() == piecePosition)
                return p;
        }
        return null;
    }

    public Pair escapeCheck(PlaySide playSide, Position kingPosition, ArrayList<Pair> playerAttackingPositions, ArrayList<Pair> engineAttackingPositions) {
        PlaySide engineSide = playSide == PlaySide.WHITE ? PlaySide.BLACK : PlaySide.WHITE;
        /* 3. Capture piece that is checking us */
        for (Pair p: playerAttackingPositions) {
            Pair captured = null;
            if(p.getSecond() == kingPosition && (captured = capture(engineAttackingPositions, (Position) p.getFirst())) != null) {
                return captured;
            }
        }

        /* 2.Drop in a piece in a blocking position */

        /* 1.Move king to a valid position */
        List<Pair> kingAttackPositions = engineAttackingPositions.stream().filter(p -> p.getFirst() == kingPosition).collect(Collectors.toList());
        for (Pair p: playerAttackingPositions) {
            for (Pair pKing : kingAttackPositions) {
                if (p.getSecond() != pKing.getFirst()) { //CU EQUALS
                    return pKing;
                }
            }
        }
        return null;
    }

        /* returns true if king is in check, and false if not */
    public boolean kingIsChecked(Position kingPosition, ArrayList<Pair> opponentAttackingPositions) {
        for (Pair possibleMove : opponentAttackingPositions) {
            if (kingPosition.equals(possibleMove.getSecond())) {
                return true;
            }
        }
        return false;
    }

    /* face o mutare imaginara si verifica daca acea mutare conduce la o pozitie valida */
    /* nu verifica sah prin descoperire
    pentru a verifica, trebuie sa calculam din nou opponent attacking pos
     */
    public boolean moveIsValid(Pair<Position, Position> move, Position kingPosition, ArrayList<Pair> opponentAttackingPositions) {

        Position src = move.getFirst();
        Position dest = move.getSecond();
        this.board[dest.getX()][dest.getY()] = this.board[src.getX()][src.getY()];
        this.board[src.getX()][src.getY()] = null;
        return !kingIsChecked(kingPosition, opponentAttackingPositions);
    }

    /* return position where the place was dropped */
    public Position dropPieceAnywhere(BoardPiece piece) {
        int minRow = 0, maxRow = 7;
        if (piece.getPiece() == Piece.PAWN) {
            minRow = 1;
            maxRow = 6;
        }
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = 0; j <= 7; j++) {
                if (this.isFree(new Position(i, j)))
                    return new Position(i, j);
            }
        }
        return null;
    }

}
