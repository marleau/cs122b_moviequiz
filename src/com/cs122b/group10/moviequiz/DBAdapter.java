package com.cs122b.group10.moviequiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {

    private static final String DB_NAME = "MovieQuizDB";
    private static final int DB_VERSION = 1;
    private static final String[] ASSETS = {"movies.csv", "stars_in_movies.csv", "stars.csv"};
    private static final String[] TABLES = {"movies", "stars_in_movies", "stars"};
    private static final String[][] TABLE_COLS = {{"id", "title", "year", "director"}, {"star_id", "movie_id"}, {"id", "first_name", "last_name"}};
    private SQLiteDatabase sqlDB;
    private Context context;

    public DBAdapter(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        context = ctx;
        this.sqlDB = getWritableDatabase();
    }

    @Override
	public void onCreate(SQLiteDatabase db) {
        // create tables
        db.execSQL("CREATE TABLE movies(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, year INTEGER NOT NULL, director TEXT NOT NULL);");
        db.execSQL("CREATE TABLE stars_in_movies(star_id INTEGER NOT NULL, movie_id INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE stars(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, first_name TEXT, last_name TEXT);");

        // populate tables
        for (int i = 0; i < 3; i++) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(ASSETS[i])));
                String line;
                while((line = in.readLine()) != null) {
                    for (int j = 0; j < TABLE_COLS[i].length; j++) {
                        String[] in_vals = line.split(",");
                        ContentValues values = new ContentValues();
                        values.put(TABLE_COLS[i][j], line);
                        db.insert(TABLES[i], null, values);
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }
}
