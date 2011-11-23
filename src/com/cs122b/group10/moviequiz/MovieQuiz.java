package com.cs122b.group10.moviequiz;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MovieQuiz extends Activity {
	DBAdapter db;
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button takeTheQuiz = (Button) findViewById(R.id.takeTheQuizButton);
        
        takeTheQuiz.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
    			Intent intent = new Intent(MovieQuiz.this, Question.class);
    			startActivity(intent);
            }

        });
        
        Button stats = (Button) findViewById(R.id.statsOfQuizzesButton);
        
        stats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
    			Intent intent = new Intent(MovieQuiz.this, Statistics.class);
    			startActivity(intent);
            }

        });

        
        //Setting up inital DB
        db = new DBAdapter(this);
        //Recreate DB, slow; uncomment to use
//        db.onCreate(db.getWritableDatabase());
    }

	@Override
	protected void onDestroy() {
		// TODO Close DB
		db.close();
		super.onDestroy();
	}
    
    
    
}