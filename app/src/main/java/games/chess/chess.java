package games.chess;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;


import static games.chess.chessCore.*;


public class chess extends Activity implements OptionsFragment.OptionsFragmentInterface {

	private chessCore core;
	private boardView board;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView stats = new TextView(this);
        board = new boardView(this);
        board.setStatsView(stats);

        core = new chessCore();
        chessCore.piece[] corePieces;
        corePieces = core.getPieces();
        Resources res = getResources();

        for (int i = 0; i < corePieces.length; i++) {
                chessCore.piece onePiece = corePieces[i];
                onePiece.setPieceHolding(res.getDrawable(onePiece.getImageResource()));
        }

        board.setCore(core);

        LinearLayout LL = new LinearLayout(this);
        LL.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams LLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LL.setWeightSum(7f);
        LL.setLayoutParams(LLParams);



        LinearLayout insideLL = new LinearLayout(this);
        ViewGroup.LayoutParams insideLLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        insideLL.setWeightSum(6f);
        insideLL.setOrientation(LinearLayout.HORIZONTAL);
        LL.setLayoutParams(insideLLParams);

        Button deleteBtn  = new Button(this);
        deleteBtn.setText(getString(R.string.buttonDelete));
        LinearLayout.LayoutParams b1Params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f);
        deleteBtn.setLayoutParams(b1Params);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chessCore.piece piece = board.getPieceHolder();
                if (piece != null) {
                    piece.setPieceState(pieceState.dead);
                }
            }
        });

        final Button setupBtn  = new Button(this);
        setupBtn.setText(getString(R.string.buttonSetup));
//        LinearLayout.LayoutParams b2Params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f);
        setupBtn.setLayoutParams(b1Params);
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (board.isSetUpMove()) {
                    board.setSetUpMove(false);
                    setupBtn.setTextColor(Color.BLACK);
                } else {
                    board.setSetUpMove(true);
                    setupBtn.setTextColor(Color.RED);
                }

            }
        });

        Button b3  = new Button(this);
        b3.setText(getString(R.string.buttonMore));
        b3.setLayoutParams(b1Params);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OptionsFragment().show(
                        getFragmentManager(), OptionsFragment.TAG);
            }
        });

        insideLL.setPadding(0,0,0,0);
        insideLL.addView(deleteBtn);
        insideLL.addView(setupBtn);
        insideLL.addView(b3);

        board.setPadding(0,0,0,0);


        insideLL.setBackgroundColor(Color.BLACK);
        board.setBackgroundColor(Color.BLACK);

        LinearLayout.LayoutParams LadderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        LinearLayout.LayoutParams BoardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 5f);

        LadderParams.setMargins(0, 0, 0, 0);
        BoardParams.setMargins(0, 0, 0, 0);

        BoardParams.gravity = Gravity.CENTER;


        insideLL.setLayoutParams(LadderParams);
        board.setLayoutParams(BoardParams);
        stats.setLayoutParams(LadderParams);

        LL.addView(board);
        LL.addView(stats);
        LL.addView(insideLL);


        setContentView(LL);
    }

    @Override
    public void onOptionsFragmentResult(String option) {
        if ("Tutorial".equals(option)){
            Intent StartGameIntent = new Intent(this, tutorial.class);
            finish();
            startActivity(StartGameIntent);
        }
        else if (option.equals("Reset")){
            core.resetGame();
        }
        else if (option.equals("About")){

            Toast.makeText(this, getString(R.string.aboutSection), Toast.LENGTH_LONG).show();
        }

    }
}

