package com.cs122b.group10.moviequiz;

import android.os.*;
import android.view.View;
import android.widget.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Statistics extends Activity {

	private AlertDialog.Builder builder;
	TextView overallScoreText;
	TextView numOfQuizText;
	TextView numberCorrectText;
	TextView numberWrongText;
	TextView avgTimePerQuestionText;
	private SQLiteDatabase sqlDB;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		
		sqlDB = new DBAdapter(Statistics.this).getWritableDatabase();

		overallScoreText = (TextView) findViewById(R.id.overallScoreText);
		numOfQuizText = (TextView) findViewById(R.id.numOfQuizText);
		numberCorrectText = (TextView) findViewById(R.id.numberCorrectText);
		numberWrongText = (TextView) findViewById(R.id.numberWrongText);
		avgTimePerQuestionText = (TextView) findViewById(R.id.avgTimePerQuestionText);

		loadStats();

		// Back to main menu button
		Button goBack = (Button) findViewById(R.id.goBackButton);
		goBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});

		// build reset stats dialog
		Button resetStats = (Button) findViewById(R.id.resetStatsButton);
		builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to reset database?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//  RESET STATS
//				sqlDB.delete("statistics",null,null);
				//RELOAD DB
				DBAdapter db = new DBAdapter(Statistics.this);
				db.onCreate(db.getWritableDatabase());
				// RESET display
				loadStats();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		resetStats.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// ask for confirmation and reset stats
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	private void loadStats() {
		// load stats
		// statistics(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
		// correct_cnt INTEGER,
		// wrong_cnt INTEGER,
		// duration LONG

		String overall = "0.0%";
		int numOfQuiz = 0;
		int totNumCorr = 0;
		int totNumWron = 0;
		Long totalTime = 0l;
		String timePerQues = "0:00";

		//Get Values
		Cursor queryResults;
		queryResults = sqlDB.query("statistics", new String[] { "count(*)" }, null, null, null, null, null);
		queryResults.moveToNext();
		numOfQuiz = queryResults.getInt(0);

		queryResults = sqlDB.query("statistics", new String[] { "sum(correct_cnt)" }, null, null, null, null, null);
		queryResults.moveToNext();
		totNumCorr = queryResults.getInt(0);

		queryResults = sqlDB.query("statistics", new String[] { "sum(wrong_cnt)" }, null, null, null, null, null);
		queryResults.moveToNext();
		totNumWron = queryResults.getInt(0);

		queryResults = sqlDB.query("statistics", new String[] { "sum(duration)" }, null, null, null, null, null);
		queryResults.moveToNext();
		totalTime = queryResults.getLong(0);

		//Calculate
		if (totNumCorr != 0 || totNumWron != 0) {
			overall = String.valueOf( ((float) totNumCorr / ((float) totNumCorr + (float) totNumWron)) * 100f);
			overall = overall.substring(0, Math.min(5, overall.length())) + "%";
			
			totalTime = totalTime/(totNumCorr + totNumWron);
			
			int seconds = (int) (totalTime / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			if (seconds < 10) {
				timePerQues = "" + minutes + ":0" + seconds;
			} else {
				timePerQues = "" + minutes + ":" + seconds;
			}
		}

		overallScoreText.setText(overall);
		numOfQuizText.setText(String.valueOf(numOfQuiz));
		numberCorrectText.setText(String.valueOf(totNumCorr));
		numberWrongText.setText(String.valueOf(totNumWron));
		avgTimePerQuestionText.setText(timePerQues);


	}

	@Override
	protected void onDestroy() {
		sqlDB.close();
		super.onDestroy();
	}
}
