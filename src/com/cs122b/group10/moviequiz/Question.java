package com.cs122b.group10.moviequiz;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.view.View;
import android.widget.Button;
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
				seconds = seconds % 60;

				if (seconds < 10) {
					timerText.setText("" + minutes + ":0" + seconds);
				} else {
					timerText.setText("" + minutes + ":" + seconds);
				}

				mHandler.postAtTime(this, now + 1000);
			} else {
				// Game Over -- tally results and display
				submitStats();
				mHandler.removeCallbacks(this);
				showGameOver();
			}
		}
	};

	private long pausedTime;

	private QuestionBuilder qb;
	
	// Game State
	private String currentQuestion;
	private int numAnsCorr;
	private int numAnsWron;
	private int correctAns;// 1,2,3,4

	private float score;
	private String scoreString;

	AlertDialog.Builder gameOverDialog;
	AlertDialog.Builder ansCorrect;
	AlertDialog.Builder ansWrong;

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

		// Get answer buttons
		answer1 = (Button) findViewById(R.id.answer1);
		answer2 = (Button) findViewById(R.id.answer2);
		answer3 = (Button) findViewById(R.id.answer3);
		answer4 = (Button) findViewById(R.id.answer4);
		currentQuestionText = (TextView) findViewById(R.id.questionText);

		// initalize timer
		timerText = (TextView) this.findViewById(R.id.timerText);
		pausedTime = 0;
		// NOTE Uptime and not current time! used for 'now' in ::
		// mHandler.postAtTime(this, now + 1000);
		mStartTime = SystemClock.uptimeMillis();
		mHandler.post(quizTimer);


		qb = new QuestionBuilder(new DBAdapter(this));
		
        generateQuestion();
        
    }

	@Override
	protected void onDestroy() {
		qb.close();
		super.onDestroy();
	}

	private void generateQuestion() {
		// create question
		/*
		 * Who directed the movie X? 
		 * When was the movie X released? Which star
		 * (was/was not) in the movie X? 
		 * In which movie the stars X and Y appear
		 * together? Who directed/did not direct the star X? 
		 * Which star appears in both movies X and Y? 
		 * Which star did not appear in the same movie with the star X?
		 * Who directed the star X in year Y?
		 */
		Random rand = new Random();
		
		// generate correct answer and random wrong answers
		
		qb.nextQuestion();
        		
		currentQuestion = "Question #"+String.valueOf(numAnsCorr+numAnsWron+1)+"\n"+qb.getQuestion();
		System.out.println(currentQuestion);
		
        correctAns = rand.nextInt(4) + 1;//should be random 1-4
        
        // save correct answer and random wrong answers
        String correctString = qb.getCorrectAnswer();
        String[] answers = qb.getAnswers();
        for (String ans : answers) {
        	System.out.println(ans);//DEBUG
        }
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
				AlertDialog alert = ansWrong.create();
				alert.show();
			}
		});
		answer2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				AlertDialog alert = ansWrong.create();
				alert.show();
			}
		});
		answer3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				AlertDialog alert = ansWrong.create();
				alert.show();
			}
		});
		answer4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pauseTime();
				AlertDialog alert = ansWrong.create();
				alert.show();
			}
		});

		// Make correct answer
		switch (correctAns) {
		case 1:
			answer1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					AlertDialog alert = ansCorrect.create();
					alert.show();
				}
			});

			break;
		case 2:
			answer2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					AlertDialog alert = ansCorrect.create();
					alert.show();
				}
			});

			break;
		case 3:
			answer3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					AlertDialog alert = ansCorrect.create();
					alert.show();
				}
			});

			break;
		case 4:
			answer4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					pauseTime();
					AlertDialog alert = ansCorrect.create();
					alert.show();
				}
			});

			break;
		}
		updateScore();
	}

	private void updateDialogs() {
		// Right and Wrong answer dialogs
		ansCorrect = new AlertDialog.Builder(this);
		ansCorrect.setMessage("CORRECT!").setCancelable(false).setPositiveButton("NEXT!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				numAnsCorr++;
				updateScore();
				generateQuestion();
				startTime();
				dialog.cancel();
			}
		});

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

		ansWrong = new AlertDialog.Builder(this);
		ansWrong.setMessage("WRONG!\nCorrect answer was:\n" + correctString).setCancelable(false).setPositiveButton("NEXT!",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						numAnsWron++;
						updateScore();
						generateQuestion();
						startTime();
						dialog.cancel();
					}
				});
	}

	private void updateScore() {
		TextView scoreText = (TextView) findViewById(R.id.scoreText);
		score = 0.0f;
		if (numAnsCorr != 0 || numAnsWron != 0) {
			score = ((float) numAnsCorr / ((float) numAnsCorr + (float) numAnsWron)) * 100.0f;
		}
		scoreString = String.valueOf(score);
		scoreString = scoreString.substring(0, Math.min(4, scoreString.length())) + "%";
		scoreText.setText(scoreString);
	}

	private void showGameOver() {
		updateScore();
		long totalTime=SystemClock.uptimeMillis() - mStartTime;
		String timePerQues="0:00";
		if (numAnsCorr != 0 || numAnsWron != 0) {			
			totalTime = totalTime/(numAnsCorr + numAnsWron);
			
			int seconds = (int) (totalTime / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			if (seconds < 10) {
				timePerQues = "" + minutes + ":0" + seconds;
			} else {
				timePerQues = "" + minutes + ":" + seconds;
			}
		}
		gameOverDialog = new AlertDialog.Builder(Question.this);
		gameOverDialog.setMessage("Game Over!\n\nScore: "+numAnsCorr+"/"+(numAnsCorr+numAnsWron)+" = " + scoreString +"\nTime Per Question: "+  timePerQues).setCancelable(false).setPositiveButton("FINALLY!",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		AlertDialog alert = gameOverDialog.create();
		alert.show();
	}
	
	public void onBackPressed() {
		// Alert user that they are leaving the quiz
		pauseTime();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("End current quiz session?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// submit stats

				submitStats();
				mHandler.removeCallbacks(quizTimer);
				showGameOver();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startTime();
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void submitStats() {
        long now = SystemClock.uptimeMillis();
        
		SQLiteDatabase sqlDB = new DBAdapter(Question.this).getWritableDatabase();
		//statistics(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
		//correct_cnt INTEGER, 
		//wrong_cnt INTEGER, 
		//duration LONG


        ContentValues values = new ContentValues();

        values.put("correct_cnt", numAnsCorr);
        values.put("wrong_cnt", numAnsWron);
        values.put("duration", (now - mStartTime));
		
		sqlDB.insert("statistics", "", values);
		
		sqlDB.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// When the application is paused, the timer should also be paused and
		// it shouldn't count down.
		// stop timer

		pauseTime();
	}

	private void pauseTime() {
		mHandler.removeCallbacks(quizTimer);
		pausedTime = SystemClock.uptimeMillis();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// start timer
		startTime();
	}

	private void startTime() {
		if (pausedTime != 0) {
			mStartTime += SystemClock.uptimeMillis() - pausedTime;
		}
		mHandler.post(quizTimer);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		/*
		 * When the user pauses the application, it can be killed by the system
		 * in extremely low memory situations. Your program should save
		 * everything (e.g., current question, timer, and statistics) properly
		 * and be able to resume later. Take a look at this page to learn more
		 * about life cycles in an Android program. You can override
		 * onSaveInstanceState() method to save the current state. Please refer
		 * to this example. A good way to test your application's ability to
		 * restore its state is to simply rotate the device so that the screen
		 * orientation changes. (You can simulate rotation on your emulator by
		 * pressing Ctrl-F11.) When the screen orientation changes, the system
		 * destroys and recreates the activity in order to apply alternative
		 * resources that might be available for the new orientation.
		 */
		// save all values
		outState.putString("currentQuestion", currentQuestion);
		outState.putString("ans1Str", ans1Str);
		outState.putString("ans2Str", ans2Str);
		outState.putString("ans3Str", ans3Str);
		outState.putString("ans4Str", ans4Str);
		outState.putInt("correctAns", correctAns);
		outState.putInt("numAnsCorr", numAnsCorr);
		outState.putInt("numAnsWron", numAnsWron);

		// save timer
		outState.putLong("mStartTime", mStartTime);
		outState.putLong("pausedTime", SystemClock.uptimeMillis());

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		// TODO load all values and adjust clock like on resume

		// load game state
		currentQuestion = inState.getString("currentQuestion");
		ans1Str = inState.getString("ans1Str");
		ans2Str = inState.getString("ans2Str");
		ans3Str = inState.getString("ans3Str");
		ans4Str = inState.getString("ans4Str");

		correctAns = inState.getInt("correctAns");
		numAnsCorr = inState.getInt("numAnsCorr");
		numAnsWron = inState.getInt("numAnsWron");

		displayQuestion();

		// load timer
		mStartTime = inState.getLong("mStartTime");
		if (pausedTime != 0) {
			mStartTime += SystemClock.uptimeMillis() - inState.getLong("pausedTime");
			mHandler.post(quizTimer);
		}

		super.onRestoreInstanceState(inState);
	}
}
