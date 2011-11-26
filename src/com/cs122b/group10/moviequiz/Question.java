package com.cs122b.group10.moviequiz;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Question extends Activity {
	private static final long duration = 180000;// 3 minutes
//	private static final long duration = 20000;// 20 sec

	// Setup Timer
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
				seconds = seconds % 60;

				if (seconds < 10) {
					timerText.setText("" + minutes + ":0" + seconds);
				} else {
					timerText.setText("" + minutes + ":" + seconds);
				}

				mHandler.postAtTime(this, now + 1000);
			} else {
				// Game Over -- tally results and display
				updateScore();
				updateDialogs();
				submitStats();
				mHandler.removeCallbacks(this);
				showGameOver();
			}
		}
	};

	private long pausedTime;
	private long elapsedTime;

	private QuestionBuilder qb;

	// Game State
	private String currentQuestion;
	private int numAnsCorr;
	private int numAnsWron;
	private int correctAns;// 1,2,3,4

	private float score;
	private String scoreString;
	private String timePerQues;

	AlertDialog.Builder gameOverDialog;
	AlertDialog.Builder ansCorrect;
	AlertDialog.Builder ansWrong;
	AlertDialog.Builder exitDialog;

	AlertDialog alertCorrect;
	boolean showAlertCorrect;
	AlertDialog alertWrong;
	boolean showAlertWrong;
	AlertDialog alertExit;
	boolean showAlertExit;
	AlertDialog alertGameOver;
	boolean showAlertGameOver;

	TextView currentQuestionText;

	String ans1Str;
	String ans2Str;
	String ans3Str;
	String ans4Str;

	Button answer1;
	Button answer2;
	Button answer3;
	Button answer4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);

		numAnsCorr = 0;
		numAnsWron = 0;

		scoreString="00%";
		timePerQues="00:00";
		
		// Get answer buttons
		answer1 = (Button) findViewById(R.id.answer1);
		answer2 = (Button) findViewById(R.id.answer2);
		answer3 = (Button) findViewById(R.id.answer3);
		answer4 = (Button) findViewById(R.id.answer4);
		currentQuestionText = (TextView) findViewById(R.id.questionText);

		// initalize timer
		timerText = (TextView) this.findViewById(R.id.timerText);
		pausedTime = 0;
		elapsedTime = duration;

		// reset alert menus
		showAlertCorrect = false;
		showAlertWrong = false;
		showAlertExit = false;
		showAlertGameOver = false;

		qb = new QuestionBuilder(new DBAdapter(this));

		generateQuestion();

		// NOTE Uptime and not current time! used for 'now' in ::
		// mHandler.postAtTime(this, now + 1000);
		mStartTime = SystemClock.uptimeMillis();
		mHandler.post(quizTimer);
	}

	private void generateQuestion() {
		// create question
		Random rand = new Random();

		// generate correct answer and random wrong answers

		qb.nextQuestion();

		currentQuestion = "Question #" + String.valueOf(numAnsCorr + numAnsWron + 1) + "\n" + qb.getQuestion();

		correctAns = rand.nextInt(4) + 1;// should be random 1-4

		// save correct answer and random wrong answers
		String correctString = qb.getCorrectAnswer();
		String[] answers = qb.getAnswers();
		// for (String ans : answers) {
		// System.out.println(ans);// DEBUG
		// }
		ans1Str = answers[0];
		ans2Str = answers[1];
		ans3Str = answers[2];
		ans4Str = answers[3];

		switch (correctAns) {
		case 1:
			ans1Str = correctString;
			break;
		case 2:
			ans2Str = correctString;
			break;
		case 3:
			ans3Str = correctString;
			break;
		case 4:
			ans4Str = correctString;
			break;
		}

		displayQuestion();
	}

	private void displayQuestion() {
		// Set Answers
		answer1.setText(ans1Str);
		answer2.setText(ans2Str);
		answer3.setText(ans3Str);
		answer4.setText(ans4Str);

		// SetQuestion
		currentQuestionText.setText(currentQuestion);

		updateDialogs();

		// All answers start as wrong
		answer1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				showAlertWrong = true;
				alertWrong.show();
			}
		});
		answer2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				showAlertWrong = true;
				alertWrong.show();
			}
		});
		answer3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				showAlertWrong = true;
				alertWrong.show();
			}
		});
		answer4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				showAlertWrong = true;
				alertWrong.show();
			}
		});

		// Make correct answer
		switch (correctAns) {
		case 1:
			answer1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					showAlertCorrect = true;
					alertCorrect.show();
				}
			});

			break;
		case 2:
			answer2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					showAlertCorrect = true;
					alertCorrect.show();
				}
			});

			break;
		case 3:
			answer3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					showAlertCorrect = true;
					alertCorrect.show();
				}
			});

			break;
		case 4:
			answer4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					showAlertCorrect = true;
					alertCorrect.show();
				}
			});

			break;
		}
		updateScore();
	}

	private void updateDialogs() {
		// CORRECT
		ansCorrect = new AlertDialog.Builder(this);
		ansCorrect.setMessage("CORRECT!").setCancelable(false).setPositiveButton("NEXT!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				numAnsCorr++;
				updateScore();
				generateQuestion();
				showAlertCorrect = false;
				startTime();
				dialog.cancel();
			}
		});
		alertCorrect = ansCorrect.create();

		String correctString = "";

		switch (correctAns) {
		case 1:
			correctString = ans1Str;
			break;
		case 2:
			correctString = ans2Str;
			break;
		case 3:
			correctString = ans3Str;
			break;
		case 4:
			correctString = ans4Str;
			break;
		}

		// WRONG
		ansWrong = new AlertDialog.Builder(this);
		ansWrong.setMessage("WRONG!\nCorrect answer was:\n" + correctString).setCancelable(false).setPositiveButton("NEXT!",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						numAnsWron++;
						updateScore();
						generateQuestion();
						showAlertWrong = false;
						startTime();
						dialog.cancel();
					}
				});
		alertWrong = ansWrong.create();

		// GAME OVER

		gameOverDialog = new AlertDialog.Builder(Question.this);
		gameOverDialog.setMessage(
				"Game Over!\n\nScore: " + numAnsCorr + "/" + (numAnsCorr + numAnsWron) + " = " + scoreString + "\nTime Per Question: " + timePerQues).setCancelable(
				false).setPositiveButton("FINALLY!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showAlertGameOver = false;
				finish();
			}
		});
		alertGameOver = gameOverDialog.create();

		// EXIT
		exitDialog = new AlertDialog.Builder(this);
		exitDialog.setMessage("End current quiz session?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// submit stats
				showAlertExit = false;
				elapsedTime = SystemClock.uptimeMillis() - mStartTime;
				updateScore();
				updateDialogs();
				submitStats();
				mHandler.removeCallbacks(quizTimer);
				showGameOver();
				dialog.cancel();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showAlertExit = false;
				startTime();
				dialog.cancel();
			}
		});
		alertExit = exitDialog.create();
	}

	private void updateScore() {
		TextView scoreText = (TextView) findViewById(R.id.scoreText);
		long totalTime = Math.min(SystemClock.uptimeMillis() - mStartTime, elapsedTime);
		timePerQues = "0:00";
		score = 0.0f;
		if (numAnsCorr != 0 || numAnsWron != 0) {
			score = ((float) numAnsCorr / ((float) numAnsCorr + (float) numAnsWron)) * 100.0f;

			totalTime = totalTime / (numAnsCorr + numAnsWron);

			int seconds = (int) (totalTime / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			if (seconds < 10) {
				timePerQues = "" + minutes + ":0" + seconds;
			} else {
				timePerQues = "" + minutes + ":" + seconds;
			}
		}
		scoreString = String.valueOf(score);
		scoreString = scoreString.substring(0, Math.min(4, scoreString.length())) + "%";
		scoreText.setText(scoreString);
	}

	private void showGameOver() {
		showAlertCorrect = false;
		alertCorrect.dismiss();
		showAlertWrong = false;
		alertWrong.dismiss();
		showAlertExit = false;
		alertExit.dismiss();
		showAlertGameOver = true;
		alertGameOver.show();
	}

	public void onBackPressed() {
		// Alert user that they are leaving the quiz
		pauseTime();
		showAlertExit = true;
		alertExit.show();
	}

	private void submitStats() {

		SQLiteDatabase sqlDB = new DBAdapter(Question.this).getWritableDatabase();
		// statistics(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
		// correct_cnt INTEGER,
		// wrong_cnt INTEGER,
		// duration LONG

		ContentValues values = new ContentValues();

		values.put("correct_cnt", numAnsCorr);
		values.put("wrong_cnt", numAnsWron);
		values.put("duration", elapsedTime);

		sqlDB.insert("statistics", "", values);

		sqlDB.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// When the application is paused, the timer should also be paused and
		// it shouldn't count down.
		// stop timer
		if (!(showAlertCorrect || showAlertExit || showAlertGameOver || showAlertWrong)) {
			pauseTime();
		}
	}

	private void pauseTime() {
		mHandler.removeCallbacks(quizTimer);
		pausedTime = SystemClock.uptimeMillis();
	}

	@Override
	protected void onDestroy() {

		alertCorrect.dismiss();
		alertWrong.dismiss();
		alertExit.dismiss();
		alertGameOver.dismiss();
		qb.close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// start timer
		updateScore();
		updateDialogs();
		if (showAlertCorrect) {
			alertCorrect.show();
		} else if (showAlertWrong) {
			alertWrong.show();
		} else if (showAlertExit) {
			alertExit.show();
		} else if (showAlertGameOver) {
			alertGameOver.show();
		} else {
			startTime();
		}
	}

	private void startTime() {
		if (pausedTime != 0) {
			mStartTime += SystemClock.uptimeMillis() - pausedTime;
			pausedTime = 0;
		}
		mHandler.post(quizTimer);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save all values

		outState.putLong("mStartTime", mStartTime);
		outState.putLong("pausedTime", pausedTime);
		outState.putLong("elapsedTime", elapsedTime);

		updateScore();
		outState.putString("currentQuestion", currentQuestion);
		outState.putString("ans1Str", ans1Str);
		outState.putString("ans2Str", ans2Str);
		outState.putString("ans3Str", ans3Str);
		outState.putString("ans4Str", ans4Str);
		outState.putInt("correctAns", correctAns);
		outState.putInt("numAnsCorr", numAnsCorr);
		outState.putInt("numAnsWron", numAnsWron);
		outState.getString("scoreString", scoreString);
		outState.getString("timePerQues", timePerQues);
		outState.putBoolean("showAlertCorrect", showAlertCorrect);
		outState.putBoolean("showAlertWrong", showAlertWrong);
		outState.putBoolean("showAlertExit", showAlertExit);
		outState.putBoolean("showAlertGameOver", showAlertGameOver);

		alertCorrect.dismiss();
		alertWrong.dismiss();
		alertExit.dismiss();
		alertGameOver.dismiss();

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		// load all values and adjust clock like on resume

		// Stop recreated timer until loaded
		mHandler.removeCallbacks(quizTimer);

		// load game state
		currentQuestion = inState.getString("currentQuestion");
		ans1Str = inState.getString("ans1Str");
		ans2Str = inState.getString("ans2Str");
		ans3Str = inState.getString("ans3Str");
		ans4Str = inState.getString("ans4Str");

		correctAns = inState.getInt("correctAns");

		numAnsCorr = inState.getInt("numAnsCorr");
		numAnsWron = inState.getInt("numAnsWron");
		scoreString = inState.getString("scoreString");
		timePerQues = inState.getString("timePerQues");

		displayQuestion();

		showAlertCorrect = inState.getBoolean("showAlertCorrect");
		showAlertWrong = inState.getBoolean("showAlertWrong");
		showAlertExit = inState.getBoolean("showAlertExit");
		showAlertGameOver = inState.getBoolean("showAlertGameOver");

		// load timer
		mStartTime = inState.getLong("mStartTime");
		pausedTime = inState.getLong("pausedTime");
		elapsedTime = inState.getLong("elapsedTime");

		long elapsed = duration - (pausedTime - mStartTime);

		int seconds = (int) (elapsed / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;

		if (seconds < 10) {
			timerText.setText("" + minutes + ":0" + seconds);
		} else {
			timerText.setText("" + minutes + ":" + seconds);
		}

		super.onRestoreInstanceState(inState);
	}
}
