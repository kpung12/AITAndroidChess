package games.chess;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;


public class chessCore {

    public boolean isSetUpMove() {
        return isSetUpMove;
    }

    public void setSetUpMove(boolean isSetUpMove) {
        this.isSetUpMove = isSetUpMove;
    }

    private boolean isSetUpMove = false;

    private enum pieceType {pawn, bishop, knight, rook, queen, king};

    public enum objectColour {black, white};

    public enum pieceState {alive, dead};

    private enum moveStatus {success, fail};

    private class player {

       private objectColour colour;
        private piece[] pieces;

        public boolean move(piece piece, cell moveTo, boolean isSetUpMove) throws Exception {
            if (piece == null || moveTo == null || moveTo == deadCell)
                return false;

            ArrayList<cell> availableMoves = piece.getAvailableMoves();
            if (availableMoves.contains(moveTo) || isSetUpMove) {
                moveStatus status = piece.tryMove(moveTo, isSetUpMove);
                return true;

            } else {
                return false;
            }
        }

        player(objectColour colour, piece[] pieces) {
            this.colour = colour;
            this.pieces = pieces;
     }
    }

    public class cell {
        private int x;

        public int getX() {
            return x;
        }

        private int y;

        public int getY() {
            return y;
        }

        private piece piece;

        public piece getPiece() {
            return piece;
        }

        public void setPiece(piece piece) {
            if (this.piece != null)
                this.piece.setPieceState(pieceState.dead);
            this.piece = piece;
        }

        cell(int x, int y) {
            this.x = x;
            this.y = y;
            this.piece = null;
        }
    }

    public class piece {

        public Drawable getPieceHolding() {
            return pieceHolding;
        }

        public void setPieceHolding(Drawable pieceHolding) {
            this.pieceHolding = pieceHolding;
        }

        private Drawable pieceHolding = null;

        private cell location;

        public cell getLocation() {
            return location;
        }

        private pieceState state;

        public void setPieceState(pieceState newState) {
            state = newState;
            if (newState == pieceState.dead) {
                location.piece = null;
                location = deadCell;
            }
        }

        private int imageResource;

        public int getImageResource() {
            return imageResource;
        }

        protected objectColour colour;

        public objectColour getPieceColour() {
            return colour;
        }

        private pieceType type;

        public boolean isEndangeredMoveTo(cell moveTo) {
            if (moveTo.getPiece() == null)
                return false;

            if (moveTo.getPiece().getPieceColour() == this.colour)
                return false;
            else
                return true;
        }

        public boolean isValidMove(cell moveTo) {
            if (moveTo.getPiece() != null)
                if (moveTo.getPiece().getPieceColour() == this.colour)
                    return false;

            // try move
            cell targetCell = board[moveTo.getX()][moveTo.getY()];
            piece oldPiece = targetCell.getPiece();
            cell sourceCell = this.getLocation();
            targetCell.setPiece(this);
            sourceCell.setPiece(null);
            this.location = targetCell;
            this.setPieceState(pieceState.alive);

            sourceCell.setPiece(this);
            targetCell.setPiece(oldPiece);
            this.location = sourceCell;
            this.setPieceState(pieceState.alive);
            if (oldPiece != null) {
                oldPiece.location = targetCell;
                oldPiece.setPieceState(pieceState.alive);
            }

            return true;
        }


        // move takes a valid cell on the board and tries to move the
        // piece to it. if the move is successful, returns true, if not
        // returns false
        public moveStatus tryMove(cell moveTo, boolean isSetUpMove) {
            if (!isValidMove(moveTo) && !isSetUpMove)
                return moveStatus.fail;
            if (isSetUpMove && moveTo.getPiece() != null)
                return moveStatus.fail;
            // move is valid; apply move and check for promotion if pawn
            // empty old location
            this.location.setPiece(null);
            // update new location
            moveTo.setPiece(this);
            // update self location
            this.location = moveTo;

            return moveStatus.success;
        }

        // gets a list of moves that don't go off the board or cause friendly
        // fire
        private availableMoves availMoves;

        public ArrayList<cell> getAvailableMoves() {
            return availMoves.getAvailableMoves(this);
        }

        piece(objectColour colour, pieceType type, cell location,
              availableMoves movementPattern, int imageResource) {
            this.colour = colour;
            this.type = type;
            this.location = location;
            this.availMoves = movementPattern;
            this.imageResource = imageResource;
        }

    }

    private interface availableMoves {
        public ArrayList<cell> getAvailableMoves(piece piece);
    }

    private class pawn implements availableMoves {
        public ArrayList<cell> getAvailableMoves(piece piece) {
            if (piece.getLocation() == deadCell)
                return null;
            ArrayList<cell> retList = new ArrayList<cell>();
            int currX = piece.getLocation().getX();
            int currY = piece.getLocation().getY();

            if (piece.getPieceColour() == objectColour.white) {
                // move forward one cell
                if (board[currX][currY - 1].getPiece() == null)
                    retList.add(board[currX][currY - 1]);

                // check moving left
                if (currX > 0
                        && board[currX - 1][currY - 1].getPiece() != null
                        && board[currX - 1][currY - 1].getPiece()
                        .getPieceColour() == objectColour.black)
                    retList.add(board[currX - 1][currY - 1]);

                // check moving right
                if (currX < 7
                        && board[currX + 1][currY - 1].getPiece() != null
                        && board[currX + 1][currY - 1].getPiece()
                        .getPieceColour() == objectColour.black)
                    retList.add(board[currX + 1][currY - 1]);

                // default location, making 4 available moves, rather than 3
                if (currY == 6 && board[currX][currY - 1].getPiece() == null
                        && board[currX][currY - 2].getPiece() == null)
                    retList.add(board[currX][currY - 2]);
            }
            if (piece.getPieceColour() == objectColour.black) {
                // COPY PASTA! YAAY! : (
                // move forward one cell
                if (board[currX][currY + 1].getPiece() == null)
                    retList.add(board[currX][currY + 1]);

                // check moving left
                if (currX > 0
                        && board[currX - 1][currY + 1].getPiece() != null
                        && board[currX - 1][currY + 1].getPiece().getPieceColour() == objectColour.white)
                    retList.add(board[currX - 1][currY + 1]);

                // check moving right
                if (currX < 7
                        && board[currX + 1][currY + 1].getPiece() != null
                        && board[currX + 1][currY + 1].getPiece().getPieceColour() == objectColour.white)
                    retList.add(board[currX + 1][currY + 1]);

                // default location, making 4 available moves, rather than 3
                if (currY == 1 && board[currX][currY + 1].getPiece() == null
                        && board[currX][currY + 2].getPiece() == null)
                    retList.add(board[currX][currY + 2]);
            }
            return retList;
        }
    }

    private class bishop implements availableMoves {
        public ArrayList<cell> getAvailableMoves(piece piece) {
            if (piece.getLocation() == deadCell)
                return null;
            ArrayList<cell> retList = new ArrayList<cell>();
            int currX = piece.getLocation().getX();
            int currY = piece.getLocation().getY();
            // set i to 1 because there's no point in checking whether
            // staying in place is a valid move. that's fucking stupid
            // even with colour checking
            int i = 1;

            // check the diagonal until you see a piece.
            // right, down
            while ((currX + i < 8 && currY + i < 8)
                    && (board[currX + i][currY + i].getPiece() == null || board[currX
                    + i][currY + i].getPiece().getPieceColour() != piece.getPieceColour())) {
                // if the piece is one of the opponent's, it is a valid move
                if (board[currX + i][currY + i].getPiece() != null
                        && board[currX + i][currY + i].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX + i][currY + i]);
                    break;
                }
                retList.add(board[currX + i][currY + i]);
                i++;
            }

            i = 1;
            // right, up
            while ((currX + i < 8 && currY - i > -1)
                    && (board[currX + i][currY - i].getPiece() == null || board[currX
                    + i][currY - i].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX + i][currY - i].getPiece() != null
                        && board[currX + i][currY - i].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX + i][currY - i]);
                    break;
                }
                retList.add(board[currX + i][currY - i]);
                i++;
            }

            i = 1;
            // left, down
            while ((currX - i > -1 && currY + i < 8)
                    && (board[currX - i][currY + i].getPiece() == null || board[currX
                    - i][currY + i].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX - i][currY + i].getPiece() != null
                        && board[currX - i][currY + i].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX - i][currY + i]);
                    break;
                }
                retList.add(board[currX - i][currY + i]);
                i++;
            }

            i = 1;
            // left, up
            while ((currX - i > -1 && currY - i > -1)
                    && (board[currX - i][currY - i].getPiece() == null || board[currX
                    - i][currY - i].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX - i][currY - i].getPiece() != null
                        && board[currX - i][currY - i].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX - i][currY - i]);
                    break;
                }
                retList.add(board[currX - i][currY - i]);
                i++;
            }

            return retList;
        }
    }

    private class knight implements availableMoves {
        public ArrayList<cell> getAvailableMoves(piece piece) {
            if (piece.getLocation() == deadCell)
                return null;
            ArrayList<cell> retList = new ArrayList<cell>();
            int currX = piece.getLocation().getX();
            int currY = piece.getLocation().getY();

            if (currX > 1
                    && currY > 0
                    && (board[currX - 2][currY - 1].getPiece() == null || board[currX - 2][currY - 1].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX - 2][currY - 1]);

            if (currX > 0
                    && currY > 1
                    && (board[currX - 1][currY - 2].getPiece() == null || board[currX - 1][currY - 2].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX - 1][currY - 2]);

            if (currX < 7
                    && currY > 1
                    && (board[currX + 1][currY - 2].getPiece() == null || board[currX + 1][currY - 2].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX + 1][currY - 2]);

            if (currX < 6
                    && currY > 0
                    && (board[currX + 2][currY - 1].getPiece() == null || board[currX + 2][currY - 1].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX + 2][currY - 1]);

            if (currX < 6
                    && currY < 7
                    && (board[currX + 2][currY + 1].getPiece() == null || board[currX + 2][currY + 1].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX + 2][currY + 1]);

            if (currX < 7
                    && currY < 6
                    && (board[currX + 1][currY + 2].getPiece() == null || board[currX + 1][currY + 2].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX + 1][currY + 2]);

            if (currX > 0
                    && currY < 6
                    && (board[currX - 1][currY + 2].getPiece() == null || board[currX - 1][currY + 2].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX - 1][currY + 2]);

            if (currX > 1
                    && currY < 7
                    && (board[currX - 2][currY + 1].getPiece() == null || board[currX - 2][currY + 1].getPiece().getPieceColour() != piece.getPieceColour()))
                retList.add(board[currX - 2][currY + 1]);

            return retList;
        }
    }

    private class rook implements availableMoves {
        public ArrayList<cell> getAvailableMoves(piece piece) {
            if (piece.getLocation() == deadCell)
                return null;
            ArrayList<cell> retList = new ArrayList<cell>();
            int currX = piece.getLocation().getX();
            int currY = piece.getLocation().getY();
            int i = 1;

            // right
            while (currX + i < 8
                    && (board[currX + i][currY].getPiece() == null || board[currX
                    + i][currY].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX + i][currY].getPiece() != null
                        && board[currX + i][currY].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX + i][currY]);
                    break;
                }
                retList.add(board[currX + i][currY]);
                i++;
            }

            i = 1;
            // left
            while (currX - i > -1
                    && (board[currX - i][currY].getPiece() == null || board[currX
                    - i][currY].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX - i][currY].getPiece() != null
                        && board[currX - i][currY].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX - i][currY]);
                    break;
                }
                retList.add(board[currX - i][currY]);
                i++;
            }

            i = 1;
            // down
            while (currY + i < 8
                    && (board[currX][currY + i].getPiece() == null || board[currX][currY
                    + i].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX][currY + i].getPiece() != null
                        && board[currX][currY + i].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX][currY + i]);
                    break;
                }
                retList.add(board[currX][currY + i]);
                i++;
            }

            i = 1;
            // up
            while (currY - i > -1
                    && (board[currX][currY - i].getPiece() == null || board[currX][currY
                    - i].getPiece().getPieceColour() != piece.getPieceColour())) {
                if (board[currX][currY - i].getPiece() != null
                        && board[currX][currY - i].getPiece().getPieceColour() != piece.getPieceColour()) {
                    retList.add(board[currX][currY - i]);
                    break;
                }
                retList.add(board[currX][currY - i]);
                i++;
            }
            return retList;
        }
    }

    private class queen implements availableMoves {
        private rook horizontalVerical;
        private bishop diagonal;

        public ArrayList<cell> getAvailableMoves(piece piece) {
            if (piece.getLocation() == deadCell)
                return null;
            horizontalVerical = new rook();
            diagonal = new bishop();
            ArrayList<cell> retList = horizontalVerical
                    .getAvailableMoves(piece);
            ArrayList<cell> moreMoves = diagonal.getAvailableMoves(piece);
            for (int i = 0; i < moreMoves.size(); i++) {
                retList.add(moreMoves.get(i));
            }
            return retList;
        }
    }

    private class king implements availableMoves {
        public ArrayList<cell> getAvailableMoves(piece piece) {
            if (piece.getLocation() == deadCell)
                return null;
            ArrayList<cell> retList = new ArrayList<cell>();
            int currX = piece.getLocation().getX();
            int currY = piece.getLocation().getY();

            if (currX > 0 && currY > 0
                    && (piece.isValidMove(board[currX - 1][currY - 1])))
                retList.add(board[currX - 1][currY - 1]);

            if (currY > 0 && (piece.isValidMove(board[currX][currY - 1])))
                retList.add(board[currX][currY - 1]);

            if (currX < 7 && currY > 0
                    && (piece.isValidMove(board[currX + 1][currY - 1])))
                retList.add(board[currX + 1][currY - 1]);

            if (currX < 7 && (piece.isValidMove(board[currX + 1][currY])))
                retList.add(board[currX + 1][currY]);

            if (currX < 7 && currY < 7
                    && (piece.isValidMove(board[currX + 1][currY + 1])))
                retList.add(board[currX + 1][currY + 1]);

            if (currY < 7 && (piece.isValidMove(board[currX][currY + 1])))
                retList.add(board[currX][currY + 1]);

            if (currX > 0 && currY < 7
                    && (piece.isValidMove(board[currX - 1][currY + 1])))
                retList.add(board[currX - 1][currY + 1]);

            if (currX > 0 && piece.isValidMove(board[currX - 1][currY]))
                retList.add(board[currX - 1][currY]);

            return retList;
        }
    }

    private cell deadCell;
    private cell board[][];
    private player white, black;
    private objectColour turn;

    public cell[][] getBoard() {
        return board;
    }

    public objectColour getTurn() {
        return turn;
    }

    // METHODS

    // checks for check on proposed board.

    public boolean move(int fromX, int fromY, int toX, int toY)
            throws Exception {
        player currentPlayer = turn == objectColour.white ? white : black;
        piece piece = board[fromX][fromY].getPiece();
        boolean success = currentPlayer.move(piece, board[toX][toY], isSetUpMove);
        if (success)
            turn = turn == objectColour.white ? objectColour.black
                    : objectColour.white;
        return success;
    }

    public piece selectedPiece(int fromX, int fromY) {
        piece piece = board[fromX][fromY].getPiece();
        return piece;
    }

    void populateBoard(cell[][] board, piece[] pieces) {

        piece blackPieces[] = new piece[16];
        piece whitePieces[] = new piece[16];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board[x][y] = new cell(x, y);
            }
        }

        for (int i = 0; i < 8; i++) {
            piece whitePawn = new piece(objectColour.white, pieceType.pawn,
                    board[i][6], new pawn(), R.drawable.wpawn);
            whitePieces[i] = whitePawn;
            board[i][6].setPiece(whitePawn);
            piece blackPawn = new piece(objectColour.black, pieceType.pawn,
                    board[i][1], new pawn(), R.drawable.bpawn);
            blackPieces[i] = blackPawn;
            board[i][1].setPiece(blackPawn);
        }

        // rooks
        whitePieces[8] = new piece(objectColour.white, pieceType.rook,
                board[0][7], new rook(), R.drawable.wrook);
        board[0][7].setPiece(whitePieces[8]);
        whitePieces[9] = new piece(objectColour.white, pieceType.rook,
                board[7][7], new rook(), R.drawable.wrook);
        board[7][7].setPiece(whitePieces[9]);
        blackPieces[8] = new piece(objectColour.black, pieceType.rook,
                board[0][0], new rook(), R.drawable.brook);
        board[0][0].setPiece(blackPieces[8]);
        blackPieces[9] = new piece(objectColour.black, pieceType.rook,
                board[7][0], new rook(), R.drawable.brook);
        board[7][0].setPiece(blackPieces[9]);
        // knights
        whitePieces[10] = new piece(objectColour.white, pieceType.knight,
                board[1][7], new knight(), R.drawable.wknight);
        board[1][7].setPiece(whitePieces[10]);
        whitePieces[11] = new piece(objectColour.white, pieceType.knight,
                board[6][7], new knight(), R.drawable.wknight);
        board[6][7].setPiece(whitePieces[11]);
        blackPieces[10] = new piece(objectColour.black, pieceType.knight,
                board[1][0], new knight(), R.drawable.bknight);
        board[1][0].setPiece(blackPieces[10]);
        blackPieces[11] = new piece(objectColour.black, pieceType.knight,
                board[6][0], new knight(), R.drawable.bknight);
        board[6][0].setPiece(blackPieces[11]);
        // bishops
        whitePieces[12] = new piece(objectColour.white, pieceType.bishop,
                board[2][7], new bishop(), R.drawable.wbishop);
        board[2][7].setPiece(whitePieces[12]);
        whitePieces[13] = new piece(objectColour.white, pieceType.bishop,
                board[5][7], new bishop(), R.drawable.wbishop);
        board[5][7].setPiece(whitePieces[13]);
        blackPieces[12] = new piece(objectColour.black, pieceType.bishop,
                board[2][0], new bishop(), R.drawable.bbishop);
        board[2][0].setPiece(blackPieces[12]);
        blackPieces[13] = new piece(objectColour.black, pieceType.bishop,
                board[5][0], new bishop(), R.drawable.bbishop);
        board[5][0].setPiece(blackPieces[13]);
        // queens
        whitePieces[14] = new piece(objectColour.white, pieceType.queen,
                board[3][7], new queen(), R.drawable.wqueen);
        board[3][7].setPiece(whitePieces[14]);
        blackPieces[14] = new piece(objectColour.black, pieceType.queen,
                board[3][0], new queen(), R.drawable.bqueen);
        board[3][0].setPiece(blackPieces[14]);
        // kings
        whitePieces[15] = new piece(objectColour.white, pieceType.king,
                board[4][7], new king(), R.drawable.wking);
        board[4][7].setPiece(whitePieces[15]);
        blackPieces[15] = new piece(objectColour.black, pieceType.king,
                board[4][0], new king(), R.drawable.bking);
        board[4][0].setPiece(blackPieces[15]);

        for (int i = 0; i < whitePieces.length; i++)
            pieces[i] = whitePieces[i];

        for (int i = 0; i < blackPieces.length; i++)
            pieces[i + whitePieces.length] = blackPieces[i];

        white = new player(objectColour.white, whitePieces);
        black = new player(objectColour.black, blackPieces);
    }


    public piece[] getPieces() {
        return pieces;
    }

    piece pieces[];

    void resetGame(){
        pieces = new piece[32];
        deadCell = new cell(-1, -1);
        board = new cell[8][8];
        populateBoard(board, pieces);
    }

    chessCore() {
        // if new game
        pieces = new piece[32];
        deadCell = new cell(-1, -1);
        board = new cell[8][8];
        populateBoard(board, pieces);
    }
}
