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

    private static final int NUMBER_OF_TYPES = 8; //Types of questions
    private DBAdapter db;
    private ArrayList<String> wrong;
    private String correct;
    private String question;
    private Random rand;
    private ArrayList<String> answers;
    private int correctIndex;

    public QuestionBuilder(DBAdapter db) {
        this.db = db;
        rand = new Random();
        wrong = new ArrayList<String>();
    }

    public void nextQuestion() {
        answers = new ArrayList<String>();
        wrong = new ArrayList<String>();
        correct = "";
        question = "";
        int type = rand.nextInt(NUMBER_OF_TYPES)+1;
        buildQuestion(type);
        randomizeAnswers();
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return correct;
    }

    public int getCorrectIndex() {
        return correctIndex;
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
        correct = cleanString(director);
        question = "Who directed the movie " + movie + "?";

        populateWrongAnswers(true, db.executeQuery("SELECT DISTINCT director FROM movies WHERE director != '"+director+"' ORDER BY RANDOM() LIMIT 5"));
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
        final String star_in_query = "SELECT DISTINCT stars.name FROM " + stars_movies_join + " AND movies.id = ? ORDER BY RANDOM() LIMIT 10";
        final String star_out_query = "SELECT DISTINCT s.name FROM stars AS s WHERE s.name NOT IN (SELECT DISTINCT stars.name FROM " + stars_movies_join + " AND movies.id = ?) ORDER BY RANDOM() LIMIT 10";
        final String movie_query = "SELECT title, id FROM movies ORDER BY RANDOM() LIMIT 1";
        final int state = rand.nextInt(2); // 0: is not in movie, 1: is in movie

        final Cursor movie_cursor = db.executeQuery(movie_query);
        movie_cursor.moveToFirst();
        final String movie = movie_cursor.getString(0);
        final int movie_id = movie_cursor.getInt(1);
        
        
        if (state == 0) {
            // Stars may not appear in 4 or more movies. So there may be less than 4 WRONG choices.
            question="Which star was NOT in "+movie+"?";
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

        correct = cleanString(movie);
        question = "In which movie did "+cleanString(star1)+" and "+cleanString(star2)+" appear together?";

        final String wrong_query = "SELECT DISTINCT m.title FROM movies AS m WHERE m.title NOT IN (SELECT movies.title FROM movies, stars AS stars1, stars AS stars2, stars_in_movies AS stars_in_movies1, stars_in_movies AS stars_in_movies2 WHERE stars_in_movies1.movie_id = movies.id AND stars_in_movies2.movie_id = movies.id AND stars_in_movies1.star_id = stars1.id AND stars_in_movies2.star_id = stars2.id AND stars1.id != stars2.id AND stars1.id = "+star1_id+" AND stars2.id = "+star2_id+") ORDER BY RANDOM() LIMIT 5";
        populateWrongAnswers(true, db.executeQuery(wrong_query));
    }

    // 5. Who directed/did not direct the star X?
    private void buildWhoDirectedOrNotStar() {
        final int state = rand.nextInt(2); // 0: did direct, 1: did not direct
        final String star_query = "SELECT DISTINCT stars.name, stars.id FROM stars ORDER BY RANDOM() LIMIT 1";
        final Cursor star_cursor = db.executeQuery(star_query);
        star_cursor.moveToFirst();
        final String star = star_cursor.getString(0);
        final int star_id = star_cursor.getInt(1);
        final String did_direct_query = "SELECT DISTINCT movies.director FROM movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id AND stars.id = ? ORDER BY RANDOM() LIMIT 10";
        final String not_direct_query = "SELECT DISTINCT m.director FROM movies AS m WHERE m.director NOT IN (select movies.director FROM movies, stars, stars_in_movies WHERE stars_in_movies.movie_id=movies.id AND stars_in_movies.star_id=stars.id AND stars.id=?) ORDER BY RANDOM() LIMIT 10";

        System.out.println(star + "(" + star_id +")");

        if (state == 0) {
            // The number of directors who worked with an actor maybe be less than 4. So there may be less than 4 WRONG choices.
            question = "Who has NOT directed "+cleanString(star)+"?";
            populateWrongAnswers(true, db.executeQuery(did_direct_query, Integer.toString(star_id)));
            populateCorrectAnswer(true, db.executeQuery(not_direct_query, Integer.toString(star_id)));
        } else {
            question = "Who has directed "+cleanString(star)+"?";
            populateWrongAnswers(true, db.executeQuery(not_direct_query, Integer.toString(star_id)));
            populateCorrectAnswer(true, db.executeQuery(did_direct_query, Integer.toString(star_id)));
        }
    }

    // 6. Which star appears in both movies X and Y?
    private void buildWhichStarInBothMovies() {
        final String movie1query = "SELECT stars2.name, stars2.id FROM stars AS stars2, movies AS movies2, stars_in_movies AS sim2 WHERE stars1.id = stars2.id AND stars2.id = sim2.star_id AND sim2.movie_id = ?";
        final String movie2query = "SELECT stars3.name, stars3.id FROM stars AS stars3, movies AS movies3, stars_in_movies AS sim3 WHERE stars1.id = stars3.id AND stars3.id = sim3.star_id AND sim3.movie_id = ?";
        final String not_both_query = "SELECT DISTINCT stars1.name FROM stars AS stars1 WHERE (NOT EXISTS (" + movie1query + ")) OR (NOT EXISTS (" + movie2query + ")) ORDER BY RANDOM() LIMIT 5";
        final String in_both_query = "SELECT DISTINCT stars.id, stars.name, m1.id, m1.title, m2.id, m2.title FROM stars, movies AS m1, movies AS m2, stars_in_movies AS i1, stars_in_movies AS i2 WHERE stars.id = i1.star_id AND stars.id = i2.star_id AND m1.id = i1.movie_id AND m2.id = i2.movie_id AND m1.id != m2.id ORDER BY RANDOM() LIMIT 1";

        final Cursor star_cursor = db.executeQuery(in_both_query);
        star_cursor.moveToFirst();
        final int star_id = star_cursor.getInt(0);
        final String star = star_cursor.getString(1);
        final int movie1_id = star_cursor.getInt(2);
        final String movie1 = star_cursor.getString(3);
        final int movie2_id = star_cursor.getInt(4);
        final String movie2 = star_cursor.getString(5);

        correct = cleanString(star);
        question = "Which star appears in both " + movie1 + " and " + movie2 + "?";
        populateWrongAnswers(true, db.executeQuery(not_both_query, Integer.toString(movie1_id), Integer.toString(movie2_id)));
    }

    // 7. Which star did not appear in the same movie with the star X?
    private void buildWhichStarNotInSameMovieWithStar() {
        final String twoStarsInMovie_query = "SELECT DISTINCT s2.name, s2.id FROM movies, stars AS s1, stars AS s2, stars_in_movies AS in1, stars_in_movies AS in2 WHERE movies.id = in1.movie_id AND movies.id = in2.movie_id AND in1.star_id = s1.id AND in2.star_id = s2.id AND s1.id != s2.id ORDER BY RANDOM() LIMIT 1";
        final Cursor star_cursor = db.executeQuery(twoStarsInMovie_query);
        star_cursor.moveToFirst();
        final int star_id = star_cursor.getInt(1);
        final String star = star_cursor.getString(0);
        final String starsWithStar_query = "SELECT DISTINCT s2.name FROM movies, stars AS s1, stars AS s2, stars_in_movies AS in1, stars_in_movies AS in2 WHERE movies.id = in1.movie_id AND movies.id = in2.movie_id AND in1.star_id = s1.id AND in2.star_id = s2.id AND s1.id != s2.id AND s1.id = ?";
        final String notInSame_query = "SELECT DISTINCT stars.name FROM stars WHERE NOT EXISTS ("+starsWithStar_query+" AND s2.id = stars.id) ORDER BY RANDOM() LIMIT 1";
        final Cursor correct_cursor = db.executeQuery(notInSame_query, Integer.toString(star_id));
        correct_cursor.moveToFirst();
        final String correct_star = correct_cursor.getString(0);

        System.out.println("star:" +star_id);

        question = "Which star did NOT appear in the same movie with " + cleanString(star) + "?";
        correct = cleanString(correct_star);
        populateWrongAnswers(true, db.executeQuery(starsWithStar_query, Integer.toString(star_id)));
    }

    // 8. Who directed the star X in year Y?
    private void buildWhoDirectedStarInYear() {
        final String director_query = "SELECT movies.director, movies.year, stars.id, stars.name FROM movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id ORDER BY RANDOM() LIMIT 1";
        final Cursor director_cursor = db.executeQuery(director_query);
        director_cursor.moveToFirst();
        final String director = director_cursor.getString(0);
        final int year = director_cursor.getInt(1);
        final int star_id = director_cursor.getInt(2);
        final String star = director_cursor.getString(3);
        final String notDirectInYear_query = "SELECT DISTINCT m.director FROM movies AS m WHERE m.director NOT IN (SELECT movies.director FROM movies, stars, stars_in_movies WHERE stars_in_movies.movie_id = movies.id AND stars_in_movies.star_id = stars.id AND year="+year+" AND stars.id = "+star_id+") ORDER BY RANDOM() LIMIT 5";

        question = "Who directed " + cleanString(star) + " in " + year + "?";
        correct = cleanString(director);
        populateWrongAnswers(true, db.executeQuery(notDirectInYear_query));
    }

    private void populateCorrectAnswer(boolean isString, Cursor cursor) {
        cursor.moveToNext();
        correct = isString ? cleanString(cursor.getString(0)) : cursor.getString(0);
    }

    // check potential answer against correct answer and exsiting answers
    // make sure that index 0 has the answer
    private void populateWrongAnswers(boolean isString, Cursor cursor) {
        while (wrong.size() < 3 && cursor.moveToNext()) {
            wrong.add(isString ? cleanString(cursor.getString(0)) : cursor.getString(0));
        }
    }

    private void randomizeAnswers() {
        System.out.println(question);
        System.out.println("correct: "+correct);
        for (String ans : wrong) { System.out.println("wrong: "+ans); }
        System.out.println("correct @ "+correctIndex);

        // choose a random int to put the correct answer
        int maxAns = wrong.size()+1;
        correctIndex = rand.nextInt(maxAns);

        // combine correct and wrong answers and prevent empty choices when displayed
        int index = 0;
        while (index < maxAns) {
            if (index != correctIndex) {
                answers.add(index, wrong.remove(wrong.size()-1));
            } else {
                answers.add(index, correct);
            }
            index++;
        }

        // if there are less than 4 answers, add empty strings to empty answers
        while (answers.size() < 4) {
            answers.add(answers.size(), "");
        }

        System.out.println("Displayed answers:");
        for (String ans : answers) { System.out.println("\t"+ans); }
    }

    // remove surrounding quotes
    private String cleanString(String str) {
        return str.substring(1, str.length()-1);
    }

    public void close(){
        db.close();
    }
    }
