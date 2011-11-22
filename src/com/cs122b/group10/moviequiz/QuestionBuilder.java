package com.cs122b.group10.moviequiz;

import java.util.Random;

import android.database.Cursor;

// 1. Who directed the movie X?
// 2. When was the movie X released?
// 3. Which star (was/was not) in the movie X?
// 4. In which movie the stars X and Y appear together?
// 5. Who directed/did not direct the star X?
// 6. Which star appears in both movies X and Y?
// 7. Which star did not appear in the same movie with the star X?
// 8. Who directed the star X in year Y?

public class QuestionBuilder {

    private static final int NUMBER_OF_TYPES = 8;//Types of questions
	private DBAdapter db;
    private String[] answers;
    private String correct;
    private String question;
    private Random rand;
//    private String query;
//    private Cursor cursor;

    public QuestionBuilder(DBAdapter db) {
        this.db = db;
        rand = new Random();
        answers = new String[4];
    }

    public void nextQuestion() {
        answers = new String[4];
        correct = "";
        question = "";
        //buildQuestion(1 + (int)(Math.random() * (8)));

        // for test
        //        buildQuestion(1 + (int)(Math.random() * (2)));
        buildQuestion(rand.nextInt(NUMBER_OF_TYPES)+1);
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correct;
    }

    public String[] getAnswers() {
        return answers;
    }

    private void buildQuestion(int i) {
        switch (i) {
            case 1:
                buildWhoDirectedMovie();
                break;
            case 2:
                buildWhenMovieReleased();
                break;
            case 3:
                buildStarInOrNotOneMovie();
                break;
            case 4:
                buildMovieWithTwoStars();
                break;
            case 5:
                buildWhoDirectedOrNotStar();
                break;
            case 6:
                buildWhichStarInBothMovies();
                break;
            case 7:
                buildWhichStarNotInSameMovieWithStar();
                break;
            case 8:
                buildWhoDirectedStarInYear();
                break;
            default:
                buildWhoDirectedMovie();
                break;
        }
    }

    // 1. Who directed the movie X?
    private void buildWhoDirectedMovie() {
        String query = "SELECT title, director FROM movies ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.executeQuery(query);
        cursor.moveToFirst();

        String director = cursor.getString(1);
        String movie = cursor.getString(0);
        correct = cleanAnswer(director);
        question = "Who directed the movie\n" + movie + "?";

	    populateAnswersDirectors(director);
	    
    }

    // 2. When was the movie X released?
    private void buildWhenMovieReleased() {
        String query = "SELECT title, year FROM movies ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.executeQuery(query);

        cursor.moveToFirst();
        String title = cursor.getString(0);
        String year = cursor.getString(1);
        correct = year;
        question = "When was\n" + title + "\nreleased?";

        populateAnswersYears(year);
    }

    // 3. Which star (was/was not) in the movie X?
    private void buildStarInOrNotOneMovie() {
    	String first_name="";
    	String last_name="";
    	question="Which star (was/was not) in the movie X?";


    	//TODO check that all wrong answers don't answer question; many exist
    	populateAnswersStars(first_name, last_name);
    }

    // 4. In which movie the stars X and Y appear together?
    private void buildMovieWithTwoStars() {
        String title="";
        question = "In which movie the stars X and Y appear together?";

    	//TODO check that all wrong answers don't answer question; many exist
        populateAnswersTitles(title);
    }

    // 5. Who directed/did not direct the star X?
    private void buildWhoDirectedOrNotStar() {
    	String director="";
        question = "Who directed/did not direct the star X?";
        

    	//TODO check that all wrong answers don't answer question; many exist
    	populateAnswersDirectors(director);
    }

    // 6. Which star appears in both movies X and Y?
    private void buildWhichStarInBothMovies() {
    	String first_name="";
    	String last_name="";
    	question="Which star appears in both movies X and Y?";

    	//TODO check that all wrong answers don't answer question; many exist
    	populateAnswersStars(first_name, last_name);
    }

    // 7. Which star did not appear in the same movie with the star X?
    private void buildWhichStarNotInSameMovieWithStar() {
    	String first_name="";
    	String last_name="";
    	question="Which star did not appear in the same movie with the star X?";

    	//TODO check that all wrong answers don't answer question; many exist
    	populateAnswersStars(first_name, last_name);
    }

    // 8. Who directed the star X in year Y?
    private void buildWhoDirectedStarInYear() {
    	String director="";
        question = "Who directed the star X in year Y?";
        

    	//TODO check that all wrong answers don't answer question; many exist
    	populateAnswersDirectors(director);
    }

    // check potential answer against correct answer and exsiting answers
//    private boolean addAnswer(String str, int count) {
//        boolean add = true;
//        str = cleanAnswer(str);
//
//        if (!str.equals(correct)) {
//            for (String ans : answers) {
//                if (str.equals(ans))
//                    add = false;
//            }
//        }
//
//        if (add)
//            answers[count] = str;
//
//        return add;
//    }

    // remove surrounding quotes
    private String cleanAnswer(String str) {
        return str.substring(1, str.length()-1);
    }

    // prevent cursor out of bounds exception
//    private void moveCursor() {
//        if (cursor.getPosition() < cursor.getCount()) {
//            cursor.moveToNext();
//        } else {
//            cursor = db.executeQuery(query);
//            cursor.moveToFirst();
//        }
//    }

	//TODO matching against an array of valid directors to use or not use
	private void populateAnswersDirectors(String notThisDirector) {
		String query = "SELECT DISTINCT director FROM movies ORDER BY RANDOM() LIMIT 5";
		Cursor qCur = db.executeQuery(query);
		for (int count = 0; count < 4;) {
			qCur.moveToNext();
			String current = qCur.getString(0);
			if (!current.equals(notThisDirector)) {
				answers[count++] = cleanAnswer(current);
			}
		}
	}

	//TODO matching against an array of valid titles to use or not use
	private void populateAnswersTitles(String notThisTitle) {
		String query = "SELECT DISTINCT title FROM movies ORDER BY RANDOM() LIMIT 5";
		Cursor qCur = db.executeQuery(query);
		for (int count = 0; count < 4;) {
			qCur.moveToNext();
			String current = qCur.getString(0);
			if (!current.equals(notThisTitle)) {
				answers[count++] = cleanAnswer(current);
			}
		}
	}
	
	private void populateAnswersYears(String notThisYear) {
		String query = "SELECT DISTINCT year FROM movies ORDER BY RANDOM() LIMIT 5";
		Cursor qCur = db.executeQuery(query);
		for (int count = 0; count < 4;) {
			qCur.moveToNext();
			String current = qCur.getString(0);
			if (!current.equals(notThisYear)) {
				answers[count++] = current;
			}
		}
	}
	
	//TODO matching against an array of valid stars to use or not use
	private void populateAnswersStars(String notThisFirstName, String notThisLastName) {
		String query = "SELECT DISTINCT first_name, last_name FROM stars ORDER BY RANDOM() LIMIT 5";
		Cursor qCur = db.executeQuery(query);
		for (int count = 0; count < 4;) {
			qCur.moveToNext();
			String currentFN = qCur.getString(0);
			String currentLN = qCur.getString(1);
			if (!(currentFN.equals(notThisFirstName) && currentLN.equals(notThisLastName))) {
				answers[count++] = cleanAnswer(currentFN) + " " + cleanAnswer(currentLN);
			}
		}
	}
    public void close(){
        db.close();
    }
}
