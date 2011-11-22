package com.cs122b.group10.moviequiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.widget.TextView;

public class Question extends Activity {
	private static final long duration = 180000;//3 minutes
	
	//Setup Timer
	private TextView timerText;
	private Handler mHandler = new Handler();
	private long mStartTime;
	private Runnable quizTimer = new Runnable() {	
		public void run() {
			long now = SystemClock.uptimeMillis();
			long elapsed = duration - (now - mStartTime);
			
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
				AlertDialog alert = gameOverDialog.create();
				alert.show();
//				finish();
			}
		}
	};

	private long pausedTime;
	
	AlertDialog.Builder gameOverDialog;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        
        //end game dialog
        //TODO DISPLAY SCORE
        gameOverDialog = new AlertDialog.Builder(this);
        gameOverDialog.setMessage("Game Over!")
               .setCancelable(false)
               .setPositiveButton("Finally!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        finish();
                   }
               });


        //initalize timer
        timerText = (TextView)this.findViewById(R.id.timerText);
        pausedTime = 0;
        //NOTE Uptime and not current time! used for 'now' in :: mHandler.postAtTime(this, now + 1000);
        mStartTime = SystemClock.uptimeMillis();
        mHandler.post(quizTimer);

        
        //TODO create question
        /*	Who directed the movie X?
         *	When was the movie X released?
         *	Which star (was/was not) in the movie X?
         *	In which movie the stars X and Y appear together?
         *	Who directed/did not direct the star X?
         *	Which star appears in both movies X and Y?
         *	Which star did not appear in the same movie with the star X?
         *	Who directed the star X in year Y? */
        
        
        //TODO load correct answer and random wrong answers
        
        //TODO set success and failure conditions for buttons
        
        
    }
    
    public void onBackPressed() {
    	//Alert user that they are leaving the quiz
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("End current quiz session?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        finish();
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //When the application is paused, the timer should also be paused and it shouldn't count down.
        //stop timer

		mHandler.removeCallbacks(quizTimer);
		pausedTime = SystemClock.uptimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start timer
        if (pausedTime != 0){
        	mStartTime += SystemClock.uptimeMillis() - pausedTime;
        }
        mHandler.post(quizTimer);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*	When the user pauses the application, it can be killed by the system in extremely low memory situations.
         *  Your program should save everything (e.g., current question, timer, and statistics) properly and be able
         *  to resume later. Take a look at  this page to learn more about life cycles in an Android program. You can 
         *  override  onSaveInstanceState() method to save the current state. Please refer to  this example. A good 
         *  way to test your application's ability to restore its state is to simply rotate the device so that the 
         *  screen orientation changes. (You can simulate rotation on your emulator by pressing Ctrl-F11.) When the 
         *  screen orientation changes, the system destroys and recreates the activity in order to apply alternative 
         *  resources that might be available for the new orientation.*/ 
        //TODO save all values

    	//save timer
        outState.putLong("mStartTime", mStartTime);
        outState.putLong("pausedTime", SystemClock.uptimeMillis());
        

        super.onSaveInstanceState(outState);
    }
    

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
    	//TODO load all values and adjust clock like on resume
    	
    	
    	//load timer
    	mStartTime = inState.getLong("mStartTime");
        if (pausedTime != 0){
        	mStartTime += SystemClock.uptimeMillis() - inState.getLong("pausedTime");
        }
        mHandler.post(quizTimer);
        
        super.onRestoreInstanceState(inState);
    }
}
