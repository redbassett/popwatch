package com.redbassett.popwatch.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopwatchDbHelper mDbHelper;

    // Codes
    private static final int MOVIES_CODE = 100;
    private static final int MOVIE_BY_ID_CODE = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopwatchContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PopwatchContract.MOVIE_PATH, MOVIES_CODE);
        matcher.addURI(authority, PopwatchContract.MOVIE_PATH + "/#", MOVIE_BY_ID_CODE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PopwatchDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_CODE:
                return PopwatchContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_ID_CODE:
                return PopwatchContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES_CODE: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        PopwatchContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_BY_ID_CODE: {
                String movieId = uri.getPathSegments().get(1);
                retCursor = mDbHelper.getReadableDatabase().query(
                        PopwatchContract.MovieEntry.TABLE_NAME,
                        projection,
                        "_ID = ?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case MOVIES_CODE:
                long _id = db.insert(PopwatchContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PopwatchContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        // null selection means delete all
        // By passing "1" to the SQLiteDatabase.delete() method, we delete all and return the
        // afffected number of rows.
        if (selection == null) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case MOVIES_CODE:
                rowsDeleted = db.delete(PopwatchContract.MovieEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case MOVIES_CODE:
                rowsUpdated = db.update(PopwatchContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case MOVIES_CODE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PopwatchContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
