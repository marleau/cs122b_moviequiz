package com.cs122b.group10.moviequiz;
import android.app.Activity;
import android.os.*;
import android.widget.TextView;

public class Question extends Activity {
	private static final long duration = 180000;//3 minutes
	
	//Timer
	private TextView timerText;
	private Handler mHandler = new Handler();
	private long mStart;
	private Runnable updateTask = new Runnable() {	
		public void run() {
			long now = SystemClock.uptimeMillis();
			long elapsed = duration - (now - mStart);
			
			if (elapsed > 0) {
				int seconds = (int) (elapsed / 1000);
				int minutes = seconds / 60;
				seconds     = seconds % 60;

				if (seconds < 10) {
					timerText.setText("" + minutes + ":0" + seconds);
				} else {
					timerText.setText("" + minutes + ":" + seconds);            
				}

				mHandler.postAtTime(this, now + 1000);
			}
			else {
				mHandler.removeCallbacks(this);
				//TODO Game Over -- tally results and display
				
				finish();
			}
		}
	};
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        

        //initalize timer

        timerText = (TextView)this.findViewById(R.id.timerText);
        mStart = SystemClock.uptimeMillis();
        mHandler.post(updateTask);
        
        
        //create question
        /*	Who directed the movie X?
         *	When was the movie X released?
         *	Which star (was/was not) in the movie X?
         *	In which movie the stars X and Y appear together?
         *	Who directed/did not direct the star X?
         *	Which star appears in both movies X and Y?
         *	Which star did not appear in the same movie with the star X?
         *	Who directed the star X in year Y? */
        
        
        //load correct answer and random wrong answers
        
        //set success and failure conditions for buttons
        
        
    }
    

    @Override
    protected void onPause() {
        super.onPause();
        //TODO stop timer

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO start timer

    }
}
