package com.cs122b.group10.moviequiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {

    private static final int STARS = 1;
	private static final int MOVIES = 0;
	private static final int STARS_IN_MOVIE = 2;
	private static final String DB_NAME = "MovieQuizDB";
    private static final int DB_VERSION = 1;
    private static final String[] ASSETS = {"movies.csv", "stars.csv", "stars_in_movies.csv"};
    private static final String[] TABLES = {"movies", "stars", "stars_in_movies", "statistics"};
    private static final String[][] TABLE_COLS = {{"id", "title", "year", "director"}, {"id", "first_name", "last_name"}, {"star_id", "movie_id"}};
    private SQLiteDatabase sqlDB;
    private Context context;

    public DBAdapter(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        context = ctx;
        this.sqlDB = getWritableDatabase();
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

        for (String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        
        // create tables
        db.execSQL("CREATE TABLE movies(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, year INTEGER NOT NULL, director TEXT NOT NULL);");
        db.execSQL("CREATE TABLE stars_in_movies(star_id INTEGER NOT NULL, movie_id INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE stars(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, first_name TEXT, last_name TEXT);");
        db.execSQL("CREATE TABLE statistics(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, correct_cnt INTEGER, wrong_cnt INTEGER, duration LONG);");

        // populate tables
        for (int table = 0; table < 3; table++) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(ASSETS[table])));
                String line;
                System.out.println(TABLES[table]);
                while((line = in.readLine()) != null) {
//                	String temp ="\t";//DEBUG

                    ContentValues values = new ContentValues();
                    
                    for (int column = 0; column < TABLE_COLS[table].length; column++) {
                        String[] in_vals = line.split(",");
                        //lol i get it fix for int fields
                        if (table == STARS_IN_MOVIE || (table == MOVIES && (column == 0 || column == 2)) || (table == STARS && column == 0)) {
                        	values.put(TABLE_COLS[table][column], Integer.valueOf(in_vals[column]));
                        } else {
                        	values.put(TABLE_COLS[table][column], in_vals[column]);
                        }
                        
//                        temp += TABLE_COLS[table][column] + ": " + in_vals[column] + "\t ";//DEBUG
                    }

                    db.insert(TABLES[table], null, values);
//                    System.out.println(temp);//DEBUG
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Schema wont change
		onCreate(db);
	}
	
	public Cursor executeQuery(String sql) {
		return sqlDB.rawQuery(sql, null);
	}
}
