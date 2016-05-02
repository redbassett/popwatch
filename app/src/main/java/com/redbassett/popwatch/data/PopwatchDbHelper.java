package com.redbassett.popwatch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.redbassett.popwatch.data.PopwatchContract.MovieEntry;

public class PopwatchDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "popwatch.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieEntry.COLUMN_NAME_TITLE + " TEXT," +
            MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT," +
            MovieEntry.COLUMN_NAME_SUMMARY + " TEXT," +
            MovieEntry.COLUMN_NAME_RELEASE_DATE + " DATE," +
            MovieEntry.COLUMN_NAME_RATING + " REAL," +
            MovieEntry.COLUMN_NAME_TRAILER_URL + " TEXT," +
            MovieEntry.COLUMN_NAME_REFERENCE_ID + " INTEGER" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public PopwatchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
