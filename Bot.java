import java.util.ArrayList;
import java.util.SortedMap;

public class Bot {
    /* Edit this, escaped characters (e.g newlines, quotes) are prohibited */
    private static final String BOT_NAME = "MyBot";

    /* Declare custom fields below */
    ChessBoard chessBoard;
    ArrayList<BoardPiece> whitePieces = new ArrayList<>();
    ArrayList<BoardPiece> blackPieces = new ArrayList<>();
    ArrayList<BoardPiece> blackCapturedPieces = new ArrayList<>(); // piesele capturate de negru
    ArrayList<BoardPiece> whiteCapturedPieces = new ArrayList<>(); // piesele capturate de alb

    /* Declare custom fields above */

    public Bot() {
        this.chessBoard = new ChessBoard(whitePieces, blackPieces);
        this.chessBoard.printChesssBoard();
    }

    /**
     * Record received move (either by enemy in normal play,
     * or by both sides in force mode) in custom structures
     * @param move received move
     * @param sideToMove side to move (either PlaySide.BLACK or PlaySide.WHITE)
     */
    public void recordMove(Move move, PlaySide sideToMove) {
        //e2-e3 sursa-destinatie
        System.out.println("Print chessboard before record move");
        chessBoard.printChesssBoard();

        if (move.getSource() == null) {
            System.out.println("source = null");
        } else {
            System.out.println("Am primit mutarea: src - " + move.getSource() + " " + move.getDestination());
        }

        if (move.isNormal()) {
            int columnDest = move.getDestination().get().charAt(0)-'a';
            int lineSrc = 7 - (move.getSource().get().charAt(1)-'1');
            int lineDest = 7 - (move.getDestination().get().charAt(1)-'1');
            int columnSrc = move.getSource().get().charAt(0)-'a';
            System.out.println("Am tradus mutarea: " + lineSrc + " " + columnSrc + " " + lineDest + " " + columnDest);
            if (chessBoard.isFree(new Position(lineDest, columnDest))) {
                chessBoard.board[lineDest][columnDest] = chessBoard.board[lineSrc][columnSrc];
                chessBoard.board[lineDest][columnDest].move(lineDest, columnDest);
                chessBoard.board[lineSrc][columnSrc] = null;
                // => move is capture
            } else {
                Position destination = new Position(lineDest, columnDest);
                ArrayList<BoardPiece> myCapturedPieces = switch (sideToMove) {
                    case WHITE -> whiteCapturedPieces;
                    case BLACK -> blackCapturedPieces;
                    default -> null;
                };
                ArrayList<BoardPiece> myOpponentPieces = switch (sideToMove) {
                    case WHITE -> blackPieces;
                    case BLACK -> whitePieces;
                    default -> null;
                };
                myOpponentPieces.remove(chessBoard.getBoardPiece(destination));
                chessBoard.board[lineDest][columnDest] = chessBoard.board[lineSrc][columnSrc];
                chessBoard.board[lineDest][columnDest].move(lineDest, columnDest);
                chessBoard.board[lineSrc][columnSrc] = null;
            }
        } else if (move.isPromotion() || move.isDropIn()) {
          //  chessBoard.board[lineDest][columnDest] = new BoardPiece(move.getReplacement().get(), sideToMove, new Position(lineDest, columnDest));
            //modify array lists
        }
        chessBoard.printChesssBoard();
    }
    /* TODO: check lateral effects
     * 1. piesa capurata de adaugat in array
     */
    /* You might find it useful to also separately record last move in another custom field */
        /*Conditii valide pentru mutari:
        1.Mutarea se afla in interiorul tablei de joc
        2.Piesa de pe pozitia sursa este de culoarea jucatorului care muta
        3.Piesa oponentului se afla pe pozitia destinatie si trb sa verificam daca piesa poate fi capturata
        4.Verificam daca tipul piesei respecta mutarea
        5.Verificam ca dupa o mutare, regele sa nu fie in sah
        7.Nebunul nu poate sari piese
         */

    /* returns a list of all the (attackerPosition, attackedPosition) on the board */
    public ArrayList<Pair> calculateAttackMoves(ArrayList<BoardPiece> pieces) {
        ArrayList<Pair> attackedPositions = new ArrayList<>();
        for (BoardPiece piece: pieces) {
            switch (piece.getPiece()) {
                case PAWN -> attackedPositions.addAll(piece.getPositionsPawn(chessBoard));
                case KNIGHT -> attackedPositions.addAll(piece.getPositionKnight(chessBoard));
                case BISHOP -> attackedPositions.addAll(piece.getPositionBishop(chessBoard));
                case ROOK -> attackedPositions.addAll(piece.getPositionRook(chessBoard));
                case QUEEN -> attackedPositions.addAll(piece.getPositionQueen(chessBoard));
                case KING -> attackedPositions.addAll(piece.getPositionKing(chessBoard));
            }
        }
        return attackedPositions;
    }

    /**
     * Calculate and return the bot's next move
     * @return your move
     */
    public Move calculateNextMove() {
        /* Calculate next move for the side the engine is playing (Hint: Main.getEngineSide())
        * Make sure to record your move in custom structures before returning.
        *
        * Return move that you are willing to submit
        * Move is to be constructed via one of the factory methods defined in Move.java */

        Move nextMove;

        PlaySide engineSide = Main.getEngineSide();
        PlaySide playerSide = engineSide == PlaySide.BLACK? PlaySide.WHITE : PlaySide.BLACK;

        System.out.println("Din calculate nextmove, engineside = " + engineSide);

        ArrayList<BoardPiece> enginePieces;
        ArrayList<BoardPiece> playerPieces;
        ArrayList<BoardPiece> engineCapturedPieces = new ArrayList<>(); // piesele capturate de catre engine
        ArrayList<BoardPiece> playerCapturedPieces = new ArrayList<>(); // player captured pieces

        if(engineSide == PlaySide.WHITE) {
            enginePieces = whitePieces;
            playerPieces = blackPieces;
            engineCapturedPieces = whiteCapturedPieces;
            playerCapturedPieces = blackCapturedPieces;
        } else if(engineSide == PlaySide.BLACK) {
            enginePieces = blackPieces;
            playerPieces = whitePieces;
            playerCapturedPieces = whiteCapturedPieces;
            engineCapturedPieces = blackCapturedPieces;
        } else {
            System.out.println("muie faru");
            return null;
        }

        /* verifiy every position that our opponent attacks
         should we add positions attacked by opponent's king? */


        ArrayList<Pair> engineAttackingPositions = calculateAttackMoves(enginePieces);
        ArrayList<Pair> playerAttackingPositions = calculateAttackMoves(playerPieces);

        /* verificam daca e aflam in sah */
        Position engineKingPosition = getKingPosition(engineSide);
        if (engineKingPosition == null)
            System.out.println("kjbvhavhk sjhio abufdvbukabuvbkhqfdvbqdvbadfbv;qadfvbluqdv");
        for (Pair<Position,Position> position : playerAttackingPositions) {
            if (engineKingPosition == position.getSecond()) {
                /* get out of check --
                 * 1. Move king to a valid position
                 * 2. Drop in a piece in a blocking position
                 * 3. Capture the piece that is checking us
                 */
                // TODO : escapeCheck
                Pair escapeCheckMove = chessBoard.escapeCheck(engineSide, engineKingPosition, playerAttackingPositions, engineAttackingPositions);

            }
        }


        // we are not in check => we can move any piece
        for (BoardPiece piece: enginePieces) {
            /* try to make normal move */
            for (Pair<Position, Position> firstAvailableMove : engineAttackingPositions) {
                /* make this move on an auxiliar ChessBoard to make further checks */
                // ar trebui facuta o functie care copiaza tabla de sah element cu element?
                ChessBoard auxChessBoard = new ChessBoard(chessBoard);
                if (auxChessBoard.moveIsValid(firstAvailableMove, engineKingPosition, playerAttackingPositions)) {

                    /* translate move to string */
                    String src = getPositionString(firstAvailableMove.getFirst());
                    String dest = getPositionString(firstAvailableMove.getSecond());
                    System.out.println("Src DEBUG: " + src); // --DEBUG
                    System.out.println("Dest DEBUG: " + dest);
                    /* check if move is a promotion by finding out what piece we are moving */
                    System.out.println("(" + firstAvailableMove.getFirst().getX() + " - " + firstAvailableMove.getFirst().getY());
                    chessBoard.printChesssBoard();
                    BoardPiece pieceToMove = chessBoard.getBoardPiece(firstAvailableMove.getFirst());
                    if (pieceToMove == null)
                        System.out.println("PIECE TO MOVE E NULL");
                    if (pieceToMove.getPiece() == Piece.PAWN)
                        System.out.println("pieceToMove = pion");
                    if (pieceToMove.getPiece() == Piece.PAWN &&
                            (firstAvailableMove.getSecond().getX() == 7 || firstAvailableMove.getSecond().getX() == 0) &&
                            (playerCapturedPieces.size() > 0)) {
                        System.out.println("Checking if promotion");
                        // replacement piece chose at random (first piece from the captured arrayList)
                        Piece replacementPiece = playerCapturedPieces.get(0).getPiece();
                        enginePieces.add(playerCapturedPieces.get(0));
                        playerCapturedPieces.remove(0);
                        recordMove(Move.promote(src, dest, replacementPiece), engineSide);
                        return Move.promote(src, dest, replacementPiece);
                    }
                    recordMove(Move.moveTo(src, dest), engineSide);
                    return Move.moveTo(src, dest);
                }
            }
            /* there is no available move => try drop in */
            for (BoardPiece boardPiece : engineCapturedPieces) {
                /* change piece color */
                boardPiece.changePlaySide();
                // drop a piece anywhere
                Position dropInPosition =  chessBoard.dropPieceAnywhere(boardPiece);
                Move moveToSend = Move.dropIn(getPositionString(dropInPosition), boardPiece.getPiece());
                recordMove(moveToSend, engineSide);
                return moveToSend;
            }
        }
        return null;
    }

    public static String getBotName() {
        return BOT_NAME;
    }

    /* returns position of the king of the specified playside color */
    public Position getKingPosition(PlaySide playSide) {
        ArrayList<BoardPiece> playSidePieces = null;
        if (playSide == PlaySide.BLACK)
            playSidePieces = this.blackPieces;
        else if (playSide == PlaySide.WHITE)
            playSidePieces = this.whitePieces;
        else System.out.println("PLAYSIDE E NULL IN PULA MEA");

        for (BoardPiece boardPiece : playSidePieces) {
            System.out.println("piesa: " + boardPiece.getPiece());
            if (boardPiece.getPiece() == Piece.KING)
                return boardPiece.getPosition();
        }
        return null;
    }


    /* primeste pozitia si returneaza stringul la care se traduce acea pozitie prin conventie */
    public static String getPositionString(Position position) {

        String move = null;

        switch (position.getY()) {
            case 0 -> move = "a";
            case 1 -> move = "b";
            case 2 -> move = "c";
            case 3 -> move = "d";
            case 4 -> move = "e";
            case 5 -> move = "f";
            case 6 -> move = "g";
            case 7 -> move = "h";
        }

        Integer line = 8 - position.getX();
        move += line.toString();

        return move;
    }
}

