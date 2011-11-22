package com.cs122b.group10.moviequiz;

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

    private DBAdapter db;
    private String[] answers;
    private String correct;
    private String question;
    private String query;
    private Cursor cursor;

    public QuestionBuilder(DBAdapter db) {
        this.db = db;
        answers = new String[4];
    }
    
    public void nextQuestion() {
        answers = new String[4];
        correct = "";
        question = "";
        //buildQuestion(1 + (int)(Math.random() * (8)));

        // for test
//        buildQuestion(1 + (int)(Math.random() * (2)));
        buildQuestion(2);
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
            case 2:
                 buildWhenMovieReleased();
            case 3:
                 buildStarInOrNotOneMovie();
            case 4:
                 buildMovieWithTwoStars();
            case 5:
                 buildWhoDirectedOrNotStar();
            case 6:
                 buildWhichStarInBothMovies();
            case 7:
                 buildWhichStarNotInSameMovieWithStar();
            case 8:
                 buildWhoDirectedStarInYear();
            default:
            	 buildWhoDirectedMovie();
        }
    }

// 1. Who directed the movie X?
    private void buildWhoDirectedMovie() {
        query = "SELECT title, director FROM movies ORDER BY RANDOM() LIMIT 10";
        cursor = db.executeQuery(query);
        cursor.moveToFirst();
        
        String director = cursor.getString(1);
        String movie = cursor.getString(0);
        correct = cleanAnswer(director);
        question = "Who directed the movie\n" + movie + "?";

        int count = 0;
        while (count < 4) {
        	moveCursor();
            String current = cursor.getString(1);
            if (addAnswer(current, count))
                count++;
        }
    }

// 2. When was the movie X released?
    private void buildWhenMovieReleased() {
        query = "SELECT title, year FROM movies ORDER BY RANDOM() LIMIT 10";
        cursor = db.executeQuery(query);

        cursor.moveToFirst();
        String title = cursor.getString(0);
        String year = cursor.getString(1);
        correct = cleanAnswer(year);
        question = "When was\n" + title + "\nreleased?";

        int count = 0;
        while (count < 4) {
        	moveCursor();
            String current = cursor.getString(1);
            if (addAnswer(current, count)) 
                count++;
        }
    }

// 3. Which star (was/was not) in the movie X?
    private void buildStarInOrNotOneMovie() {
        question = "";
    }

// 4. In which movie the stars X and Y appear together?
    private void buildMovieWithTwoStars() {
        question = "";
    }

// 5. Who directed/did not direct the star X?
    private void buildWhoDirectedOrNotStar() {
        question = "";
    }

// 6. Which star appears in both movies X and Y?
    private void buildWhichStarInBothMovies() {
        question = "";
    }

// 7. Which star did not appear in the same movie with the star X?
    private void buildWhichStarNotInSameMovieWithStar() {
        question = "";
    }

// 8. Who directed the star X in year Y?
    private void buildWhoDirectedStarInYear() {
        question = "";
    }

    // check potential answer against correct answer and exsiting answers
    private boolean addAnswer(String str, int count) {
        boolean add = true;
        str = cleanAnswer(str);

        if (!str.equals(correct)) {
            for (String ans : answers) {
                if (str.equals(ans))
                    add = false;
            }
        }

        if (add)
            answers[count] = str;
        
        return add;
    }

    // remove surrounding quotes
    private String cleanAnswer(String str) {
        return str.substring(1, str.length()-1);
    }
    
    // prevent cursor out of bounds exception
    private void moveCursor() {
        if (cursor.getPosition() < cursor.getCount()) {
            cursor.moveToNext();
        } else {
            cursor = db.executeQuery(query);
            cursor.moveToFirst();
        }
    }
}
