import java.util.ArrayList;

public class BoardPiece {
    private Piece piece;
    private PlaySide color;
    private Position position;
    private int score;

    public BoardPiece(Piece piece, PlaySide color, Position position) {
        this.piece = piece;
        this.color = color;
        this.position = position;

        this.score = switch (piece) {
            case KING -> 100;
            case QUEEN -> 9;
            case ROOK -> 5;
            case KNIGHT, BISHOP -> 3;
            case PAWN -> 1;
        };
    }

    public BoardPiece(Piece piece, PlaySide color, int line, int col) {
        this.piece = piece;
        this.color = color;
        this.position = new Position(line, col);

        this.score = switch (piece) {
            case KING -> 100;
            case QUEEN -> 9;
            case ROOK -> 5;
            case KNIGHT, BISHOP -> 3;
            case PAWN -> 1;
        };
    }

    public Piece getPiece() {
        return piece;
    }

    public PlaySide getColor() {
        return color;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void move(int line, int column) {
        this.position = new Position(line, column);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) { this.position = position; }

    public PlaySide getOpponentColor() {
        return this.color == PlaySide.BLACK ? PlaySide.WHITE : PlaySide.BLACK;
    }

    /* MOVES:
     * one step forward
     * two step forawrd at start
     * capture left & right
     * TODO en passent
     */
    public ArrayList<Pair> getPositionsPawn(ChessBoard chessBoard) {

        ArrayList<Pair> positions = new ArrayList<>();
        Position actualPosition = this.position;
        Position auxPosition = null;

        if (this.color == PlaySide.BLACK) {
            /* daca pionul se afla in pozitia de start poate muta doi pasi in fata */
            if (this.position.getX() == 1) {
                /* TODO: check en passent */
                auxPosition = new Position(3, getPosition().getY());
                if (chessBoard.isFree(auxPosition))
                    positions.add(new Pair<>(this.position, new Position(3, actualPosition.getY())));
            }

            /* check normal move (one step forward) */
            auxPosition = new Position(actualPosition.getX() + 1, actualPosition.getY());
            if (chessBoard.isFree(auxPosition))
                positions.add(new Pair<>(this.position, auxPosition));

            /* check capture move (one diagonal left / right) */
            auxPosition = new Position(getPosition().getX() + 1, getPosition().getY() + 1);
            if (chessBoard.isInBounds(auxPosition) && chessBoard.isEnemy(auxPosition, PlaySide.WHITE))
                positions.add(new Pair<>(this.position, auxPosition));
            auxPosition = new Position(getPosition().getX() + 1, getPosition().getY() - 1);
            if (chessBoard.isInBounds(auxPosition) && chessBoard.isEnemy(auxPosition, PlaySide.WHITE))
                positions.add(new Pair<>(this.position, auxPosition));
        }

        if (this.color == PlaySide.WHITE) {
            /* daca pionul se afla in pozitia de start poate muta doi pasi in fata */
            if (this.position.getX() == 6) {
                /* TODO: check en passent */
                auxPosition = new Position(4, getPosition().getY());
                if (chessBoard.isFree(auxPosition))
                    positions.add(new Pair<>(this.position, new Position(4, actualPosition.getY())));
            }
            /* check normal move (one step forward) */
            auxPosition = new Position(actualPosition.getX() - 1, actualPosition.getY());
            if (chessBoard.isFree(auxPosition))
                positions.add(new Pair<>(this.position, auxPosition));
            /* check capture move (one diagonal left / right) */
            auxPosition = new Position(getPosition().getX() - 1, getPosition().getY() + 1);
            if (chessBoard.isInBounds(auxPosition) && chessBoard.isEnemy(auxPosition, PlaySide.BLACK))
                positions.add(new Pair<>(this.position, auxPosition));
            auxPosition = new Position(getPosition().getX() - 1, getPosition().getY() - 1);
            if (chessBoard.isInBounds(auxPosition) && chessBoard.isEnemy(auxPosition, PlaySide.BLACK))
                positions.add(new Pair<>(this.position, auxPosition));

        }
        return positions;
    }


    /* can move if the specified position is free or has an opponent's piece on it */
    public ArrayList<Pair> getPositionKnight(ChessBoard chessBoard) {
        ArrayList<Pair> positions = new ArrayList<Pair>();
        int[] possibleXOffsets = {-2, -1, 1, 2};
        int[] possibleYOffsets = {-2, -1, 1, 2};
        for (int i : possibleXOffsets) {
            for (int j : possibleYOffsets) {
                /* eliminate illegal moves (such as (-2 ,2), (1, 1) etc */
                if ((i + j) % 2 == 0)
                    continue;

                /* check table */
                Position auxPosition = new Position(getPosition().getX()+i, getPosition().getY()+j);
                if (!chessBoard.isInBounds(auxPosition))
                    continue;
                if (chessBoard.isFree(auxPosition) || chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
            }
        }
        return positions;
    }

    public ArrayList<Pair> getPositionRook(ChessBoard chessBoard) {
        ArrayList<Pair> positions = new ArrayList<Pair>();
        /* go up */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX() + i, getPosition().getY());
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }
        /* go down */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX() - i, getPosition().getY());
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }
        /* go left */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX(), getPosition().getY() - i);
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }
        /* go right */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX(), getPosition().getY() + i);
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }
        return positions;
    }

    public ArrayList<Pair> getPositionBishop(ChessBoard chessBoard) {
        ArrayList<Pair> positions = new ArrayList<Pair>();
        /* go up-left */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX() + i, getPosition().getY() - i);
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }

        /* go up-right */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX() + i, getPosition().getY() + i);
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }

        /* go down-left */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX() - i, getPosition().getY() - i);
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }

        /* go down-right */
        for (int i = 1; i <= 7; i++) {
            Position auxPosition = new Position(getPosition().getX() - i, getPosition().getY() + i);
            if (!chessBoard.isInBounds(auxPosition))
                break;
            if (!chessBoard.isFree(auxPosition)) {
                // Can't go past a piece. Can eventually capture it
                if (chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
                break;
            }
        }
        return positions;
    }

    public ArrayList<Pair> getPositionQueen(ChessBoard chessBoard) {
        ArrayList<Pair> positions = new ArrayList<Pair>();
        positions.addAll(this.getPositionBishop(chessBoard));
        positions.addAll(this.getPositionRook(chessBoard));
        return positions;
    }

    public ArrayList<Pair> getPositionKing(ChessBoard chessBoard) {
        ArrayList<Pair> positions = new ArrayList<Pair>();
        int[] possibleOffsetX = {-1, 0 ,1};
        int[] possibleOffsetY = {-1, 0 ,1};
        for (int i : possibleOffsetX) {
            for (int j : possibleOffsetY) {
                if (i == 0 & j == 0)
                    continue;
                Position auxPosition = new Position(getPosition().getX() + i, getPosition().getY()+j);
                /* check table */
                if (!chessBoard.isInBounds(auxPosition))
                    continue;
                if (chessBoard.isFree(auxPosition) || chessBoard.isEnemy(auxPosition, getOpponentColor()))
                    positions.add(new Pair<>(this.position, auxPosition));
            }
        }
        return positions;
    }

    /* change captured piece to own color (usually before drop-in) */
    public void changePlaySide() {
        if (this.getColor() == PlaySide.BLACK)
            this.color = PlaySide.WHITE;
        else this.color = PlaySide.BLACK;
    }

}
