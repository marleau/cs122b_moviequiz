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

    private static final int NUMBER_OF_TYPES = 8;//Types of questions
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
        //int type = rand.nextInt(NUMBER_OF_TYPES)+1;
        int type = 5;
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

        populateWrongAnswers(false, db.executeQuery("SELECT DISTINCT year FROM movies WHERE year != "+year+" ORDER BY RANDOM() LIMIT 5"));
    }

    // 3. Which star (was/was not) in the movie X?
    private void buildStarInOrNotOneMovie() {
        final String stars_movies_join = "movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id";
        final String star_in_query = "SELECT DISTINCT stars.name FROM " + stars_movies_join + " AND movies.id = ? ORDER BY RANDOM() LIMIT 5";
        final String star_out_query = "SELECT DISTINCT stars.name FROM " + stars_movies_join + " AND movies.id != ? ORDER BY RANDOM() LIMIT 5";
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

        final String two_stars_query = "SELECT movies.id, movies.title, stars1.id, stars1.name, stars2.id, stars2.name FROM movies, stars AS stars1, stars AS stars2, stars_in_movies AS stars_in_movies1, stars_in_movies AS stars_in_movies2 WHERE stars_in_movies1.movie_id = movies.id AND stars_in_movies2.movie_id = movies.id AND stars_in_movies1.star_id = stars1.id AND stars_in_movies2.star_id = stars2.id AND stars1.id != stars2.id ORDER BY RANDOM() LIMIT 1";
        final Cursor two_stars_cursor = db.executeQuery(two_stars_query);
        two_stars_cursor.moveToFirst();
        final int movie_id = two_stars_cursor.getInt(0);
        final String movie = two_stars_cursor.getString(1);
        final int star1_id = two_stars_cursor.getInt(2);
        final String star1 = two_stars_cursor.getString(3);
        final int star2_id = two_stars_cursor.getInt(4);
        final String star2 = two_stars_cursor.getString(5);
        System.out.println("movie id: "+movie_id+"\t"+"star id:" + star1_id + ", " + star2_id);

        correct = cleanAnswer(movie);
        question = "In which movie, did "+cleanAnswer(star1)+" and "+cleanAnswer(star2)+" appear together?";

        final String wrong_query = "SELECT DISTINCT movies.title FROM movies, stars_in_movies AS stars1, stars_in_movies AS stars2  WHERE stars1.movie_id = movies.id AND stars2.movie_id = movies.id AND movies.id != ? AND stars1.star_id != ? AND stars2.star_id != ? ORDER BY RANDOM() LIMIT 5";
        populateWrongAnswers(true, db.executeQuery(wrong_query, Integer.toString(movie_id), Integer.toString(star1_id), Integer.toString(star2_id)));
    }

    // 5. Who directed/did not direct the star X?
    private void buildWhoDirectedOrNotStar() {
        final int state = rand.nextInt(2); // 0: did direct, 1: did not direct
        final String star_query = "SELECT DISTINCT stars.name, stars.id FROM stars ORDER BY RANDOM() LIMIT 1";
        final Cursor star_cursor = db.executeQuery(star_query);
        star_cursor.moveToFirst();
        final String star = star_cursor.getString(0);
        final int star_id = star_cursor.getInt(1);
        final String did_direct_query = "SELECT DISTINCT movies.director FROM movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id AND stars.id = ? ORDER BY RANDOM() LIMIT 5";
        final String not_direct_query = "SELECT DISTINCT movies.director FROM movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id AND stars.id != ? ORDER BY RANDOM() LIMIT 5";

        if (state == 0) {
            question = "Who did not direct "+cleanAnswer(star)+"?";
            populateWrongAnswers(true, db.executeQuery(did_direct_query, Integer.toString(star_id)));
            populateCorrectAnswer(true, db.executeQuery(not_direct_query, Integer.toString(star_id)));
        } else {
            question = "Who directed "+cleanAnswer(star)+"?";
            populateWrongAnswers(true, db.executeQuery(not_direct_query, Integer.toString(star_id)));
            populateCorrectAnswer(true, db.executeQuery(did_direct_query, Integer.toString(star_id)));
        }
    }

    // 6. Which star appears in both movies X and Y?
    private void buildWhichStarInBothMovies() {
        question="Which star appears in both movies X and Y?";
    }

    // 7. Which star did not appear in the same movie with the star X?
    private void buildWhichStarNotInSameMovieWithStar() {
        question="Which star did not appear in the same movie with the star X?";
    }

    // 8. Who directed the star X in year Y?
    private void buildWhoDirectedStarInYear() {
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
