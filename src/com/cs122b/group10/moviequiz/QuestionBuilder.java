package com.cs122b.group10.moviequiz;

import java.util.ArrayList;
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

    private static final int NUMBER_OF_TYPES = 3;//Types of questions
    private DBAdapter db;
    private String[] answers;
    private String correct;
    private String question;
    private Random rand;

    public QuestionBuilder(DBAdapter db) {
        this.db = db;
        rand = new Random();
        answers = new String[4];
    }

    public void nextQuestion() {
        for (int i = 0; i < 4; i++) { answers[i] = ""; }
        correct = "";
        question = "";
        int type = rand.nextInt(NUMBER_OF_TYPES)+1;
        System.out.println("Q Type: "+type);
        buildQuestion(type);
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
        question = "Who directed the movie " + movie + "?";

        //populateAnswersDirectors(director);
        //cursor = db.executeQuery("SELECT DISTINCT director FROM movies ORDER BY RANDOM() LIMIT 10");
        populateWrongAnswers(true, db.executeQuery("SELECT DISTINCT director FROM movies WHERE director != "+director+" ORDER BY RANDOM() LIMIT 5"));
    }

    // 2. When was the movie X released?
    private void buildWhenMovieReleased() {
        String query = "SELECT title, year FROM movies ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.executeQuery(query);

        cursor.moveToFirst();
        String title = cursor.getString(0);
        String year = cursor.getString(1);
        correct = year;
        question = "When was " + title + " released?";

        //populateAnswersYears(year);
        //cursor = db.executeQuery("SELECT DISTINCT year FROM movies ORDER BY RANDOM() LIMIT 10");
        populateWrongAnswers(false, db.executeQuery("SELECT DISTINCT year FROM movies WHERE year != "+year+" ORDER BY RANDOM() LIMIT 5"));
    }
    
    // 3. Which star (was/was not) in the movie X?
    private void buildStarInOrNotOneMovie() {
    	final String stars_movies_join = "movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id";
    	final String star_in_query = "SELECT DISTINCT stars.name FROM " + stars_movies_join + " AND movies.id = ? ORDER BY RANDOM()";
    	final String star_out_query = "SELECT DISTINCT stars.name FROM " + stars_movies_join + " AND movies.id != ? ORDER BY RANDOM()";
    	final String movie_query = "SELECT title, id FROM movies ORDER BY RANDOM() LIMIT 1";
        final int state = rand.nextInt(2); // 0: is not in movie, 1: is no movie
        
        final Cursor movie_cursor = db.executeQuery(movie_query);
        movie_cursor.moveToFirst();
        final String movie = movie_cursor.getString(0);
        final int movie_id = movie_cursor.getInt(1);

        if (state == 0) {
        	question="Which star was not in "+movie+"?";
            populateWrongAnswers(true, db.executeQuery(star_in_query, Integer.toString(movie_id)));
            populateCorrectAnswer(true, db.executeQuery(star_out_query, Integer.toString(movie_id)));
        } else {
        	question="Which star was in "+movie+"?";
        	populateCorrectAnswer(true, db.executeQuery(star_in_query, Integer.toString(movie_id)));
            populateWrongAnswers(true, db.executeQuery(star_out_query, Integer.toString(movie_id)));
        }
    }

    // 4. In which movie the stars X and Y appear together?
    private void buildMovieWithTwoStars() {
        String title="";
        question = "In which movie the stars X and Y appear together?";
    }

    // 5. Who directed/did not direct the star X?
    private void buildWhoDirectedOrNotStar() {
        String director="";
        question = "Who directed/did not direct the star X?";
    }

    // 6. Which star appears in both movies X and Y?
    private void buildWhichStarInBothMovies() {
        String first_name="";
        String last_name="";
        question="Which star appears in both movies X and Y?";
    }

    // 7. Which star did not appear in the same movie with the star X?
    private void buildWhichStarNotInSameMovieWithStar() {
        String first_name="";
        String last_name="";
        question="Which star did not appear in the same movie with the star X?";
    }

    // 8. Who directed the star X in year Y?
    private void buildWhoDirectedStarInYear() {
        String director="";
        question = "Who directed the star X in year Y?";
    }

    private void populateCorrectAnswer(boolean isString, Cursor cursor) {
    	cursor.moveToNext();
    	correct = isString ? cleanAnswer(cursor.getString(0)) : cursor.getString(0);
    }
    
    // check potential answer against correct answer and exsiting answers
    // make sure that index 0 has the answer
    private void populateWrongAnswers(boolean isString, Cursor cursor) {
    	int count = 0;
    	
        while (count < 4 && cursor.moveToNext()) {
        	answers[count++] = isString ? cleanAnswer(cursor.getString(0)) : cursor.getString(0);
        }
    }

    // remove surrounding quotes
    private String cleanAnswer(String str) {
        return str.substring(1, str.length()-1);
    }

    public void close(){
        db.close();
    }
}
