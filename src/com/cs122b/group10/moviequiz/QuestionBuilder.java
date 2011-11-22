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

    public QuestionBuilder(DBAdapter db) {
        this.db = db;
        answers = new String[4];
    }
    
    public void nextQuestion() {
        //buildQuestion(1 + (int)(Math.random() * (8)));

        // for test
        buildQuestion(1);
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
        String query = "SELECT title, director FROM movies ORDER BY RANDOM() LIMIT 10";
        Cursor cur = db.executeQuery(query);
        cur.moveToFirst();
        String director = cur.getString(1);
        String movie = cur.getString(0);
        correct = director.substring(1, director.length()-1);
        int count = 0;

        while (count < 4) {
            cur.moveToNext();
            String currentD = cur.getString(1);
//            if (!currentD.equals(director)) {
                answers[count] = currentD;
                count++;
//            }
        }

        question = "Who directed the movie\n" + movie + "?";
    }

// 2. When was the movie X released?
    private void buildWhenMovieReleased() {
        question = "";
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

}
