package com.redbassett.popwatch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.redbassett.popwatch.data.PopwatchContract.MovieEntry;

public class PopwatchDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "popwatch.db";

    private static String createMovieTable(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_NAME_TITLE + " TEXT," +
                MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT," +
                MovieEntry.COLUMN_NAME_SUMMARY + " TEXT," +
                MovieEntry.COLUMN_NAME_RELEASE_DATE + " DATE," +
                MovieEntry.COLUMN_NAME_RATING + " REAL," +
                MovieEntry.COLUMN_NAME_TRAILER_URL + " TEXT" +
                " )";
    }

    private static String deleteMovieTable(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    private static final String[] TABLE_NAMES = {
            PopwatchContract.PopularMovieEntry.TABLE_NAME,
            PopwatchContract.TopMovieEntry.TABLE_NAME,
            PopwatchContract.FavMovieEntry.TABLE_NAME
    };

    public PopwatchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        for (String name : TABLE_NAMES) {
            db.execSQL(createMovieTable(name));
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String name : TABLE_NAMES) {
            db.execSQL(deleteMovieTable(name));
        }

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
