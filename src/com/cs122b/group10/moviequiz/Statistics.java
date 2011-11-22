package com.cs122b.group10.moviequiz;

import android.os.*;
import android.view.View;
import android.widget.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Statistics extends Activity {

	private AlertDialog.Builder builder;
	TextView overallScoreText;
	TextView numOfQuizText;
	TextView numberCorrectText;
	TextView numberWrongText;
	TextView avgTimePerQuestionText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		overallScoreText = (TextView) findViewById(R.id.overallScoreText);
		numOfQuizText = (TextView) findViewById(R.id.numOfQuizText);
		numberCorrectText = (TextView) findViewById(R.id.numberCorrectText);
		numberWrongText = (TextView) findViewById(R.id.numberWrongText);
		avgTimePerQuestionText = (TextView) findViewById(R.id.avgTimePerQuestionText);
		
		//TODO load stats .... from database?

		//Back to main menu button
		Button goBack = (Button) findViewById(R.id.goBackButton);
		goBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
		
		// build reset stats dialog
		Button resetStats = (Button) findViewById(R.id.resetStatsButton);
		builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to reset statistics?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// TODO RESET STATS

				// RESET display
				 overallScoreText.setText("00%");
				 numOfQuizText.setText("0");
				 numberCorrectText.setText("0");
				 numberWrongText.setText("0");
				 avgTimePerQuestionText.setText("00:00");
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
}
