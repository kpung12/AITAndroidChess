package games.chess;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;


public class tutorial extends Activity {
    private int counter = 0;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tutorial);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        image = (ImageView) findViewById(R.id.tut1);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(counter == 0){
                image.setImageResource(R.drawable.firstselect);
            }
            else if (counter == 1){
                image.setImageResource(R.drawable.deleted1piece);
            }
            else if (counter == 2){
                image.setImageResource(R.drawable.setupmode1);
            }
            else if (counter == 3){
                image.setImageResource(R.drawable.setupmode2danger);
            }
            else if (counter == 4){
                image.setImageResource(R.drawable.setupmode2);
            }
            else if (counter == 5){
                image.setImageResource(R.drawable.setupmode3);
            }
            else if (counter == 6){
                Intent intent = new Intent(tutorial.this, chess.class);
                startActivity(intent);
            }
            counter++;
            return true;
        } else {
            return false;
        }
    }

}
