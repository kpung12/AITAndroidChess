package games.chess;

import java.util.ArrayList;

import games.chess.chessCore.cell;
import games.chess.chessCore.piece;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class boardView extends View
{


    boolean pieceSelected;
    boolean reset;
    Drawable boardImageHolder = null;

    public boolean isSetUpMove() {
        return core.isSetUpMove();
    }

    public void setSetUpMove(boolean isSetUpMove) {
        core.setSetUpMove(isSetUpMove);
    }

    public piece getPieceHolder() {
        return pieceHolder;
    }

    public int endangeredWhitePieces;
    public int endangeredBlackPieces;

    piece pieceHolder = null;

    private int fromX, fromY, toX, toY, Yzero, width;

    private chessCore core;
    public void setCore(chessCore core)
    {
        if(core != null)
            this.core = core;
    }

    private TextView stats = null;
    public void setStatsView(TextView fromChess ) {
        this.stats = fromChess;
    }

    private Resources res;
    private Canvas canvas;

    private int getBoardX(int x)
    {
        return x * width / 8;
    }

    private int getBoardY(int y)
    {
        return y * width / 8 + Yzero;
    }




    private void drawBoard(cell[][] board)
    {
        super.invalidate();


        endangeredWhitePieces = 0;
        endangeredBlackPieces = 0;

        if (boardImageHolder == null)
            boardImageHolder = res.getDrawable(R.drawable.flatboard2);
        Drawable boardImg = boardImageHolder;
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float dp = 475f;
        int pixels = (int) (metrics.density *dp + 0.5f);
        width =  pixels;
        Yzero = (int) ( 0 * getResources().getDisplayMetrics().density);

        boardImg.setBounds(0, Yzero, width, width + Yzero);



        boardImg.draw(canvas);
        for(int x = 0; x < 8; x++)
        {
            for(int y = 0; y < 8; y++)
            {
                piece piece = board[x][y].getPiece();
                if(piece != null)
                {
                    if (piece.getPieceHolding() == null)
                        piece.setPieceHolding(res.getDrawable(piece.getImageResource()));

                    Drawable figure = piece.getPieceHolding();
                    figure.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
                    figure.setAlpha(80);
                    figure.draw(canvas);

                    ArrayList<cell> availMoves = piece.getAvailableMoves();

                    for(int i = 0; i < availMoves.size(); i ++) {
                        cell availMove = availMoves.get(i);
                        if (piece.isEndangeredMoveTo(availMove)) {
                            Drawable circle;
                            if(availMove.getPiece().getPieceColour() == chessCore.objectColour.black){
                                circle = res.getDrawable(R.drawable.freighendcircleblack);
                                endangeredBlackPieces += 1;}
                            else{
                                circle = res.getDrawable(R.drawable.freighendcirclewhite);
                                endangeredWhitePieces += 1;}

                            circle.setAlpha(75);
                            circle.setBounds(getBoardX(availMove.getX()), getBoardY(availMove.getY()), getBoardX(availMove.getX()) + width / 8, getBoardY(availMove.getY()) + width / 8);
                            circle.draw(canvas);
                        }
                    }
                }
            }
        }

        if (stats != null) {
            stats.setText(getContext().getString(R.string.whitedanger) + new Integer(endangeredWhitePieces).toString() + getContext().getString(R.string.blackdanger) + new Integer(endangeredBlackPieces).toString());
        }

    }

    private void drawAvailableMoves(cell[][] board, int x, int y)
    {
        piece piece = board[x][y].getPiece();
        if(piece != null)
        {
            Drawable selection = res.getDrawable(R.drawable.selected);
            selection.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
            selection.draw(canvas);

            if (core.isSetUpMove())
                return;

            ArrayList<cell> availMoves = piece.getAvailableMoves();
            for(int i = 0; i < availMoves.size(); i ++)
            {
                cell availMove = availMoves.get(i);
                if(piece.isValidMove(availMove))
                {
                    Drawable circle = res.getDrawable(R.drawable.selectioncircle);
                    circle.setBounds(getBoardX(availMove.getX()), getBoardY(availMove.getY()), getBoardX(availMove.getX()) + width/8, getBoardY(availMove.getY()) + width /8);
                    circle.draw(canvas);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);


        if(!pieceSelected)
        {
            fromX = (int)(event.getX() / (width / 8.0));
            fromY = (int)((event.getY() - Yzero) / (width / 8.0));
            if( fromX < 0 || fromX > 7 || fromY < 0 || fromY > 7) {
                return true;
            }
            pieceHolder = core.selectedPiece(fromX,fromY);
        }
        else
        {
            toX = (int)(event.getX() / (width / 8.0));
            toY = (int)((event.getY() - Yzero) / (width / 8.0));

            if( toX < 0 || toX > 7 || toY < 0 || toY > 7 || (toX == fromX && toY == fromY));
            else
            {
                try
                {
                    core.move(fromX, fromY, toX,toY);
                    pieceSelected = false;
                    pieceHolder = null;
                    return true;
                }catch( Exception ex)
                {
                    pieceSelected = false;
                    pieceHolder = null;
                    return true;

                }
            }
        }
        pieceSelected = !pieceSelected;
        return true;
    }

    boolean changed = true;

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (changed)
            this.canvas = canvas;
        this.drawBoard(core.getBoard());
        if (pieceSelected)
            drawAvailableMoves(core.getBoard(), fromX, fromY);
    }

    boardView(Context context)
    {
        super(context);

        pieceSelected = false;
        reset = true;
        fromX =-1;
        fromY = -1;
        toX = -1;
        toY = -1;
        res = getResources();

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

}
