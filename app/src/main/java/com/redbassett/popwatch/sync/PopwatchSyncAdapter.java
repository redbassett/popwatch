package com.redbassett.popwatch.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.redbassett.popwatch.Movie;
import com.redbassett.popwatch.MovieApi.TmdbApi;
import com.redbassett.popwatch.R;
import com.redbassett.popwatch.Utility;
import com.redbassett.popwatch.Utility.C;
import com.redbassett.popwatch.data.PopwatchContract;
import com.redbassett.popwatch.data.PopwatchContract.MovieEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PopwatchSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = PopwatchSyncAdapter.class.getSimpleName();

    // 60 minutes * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PopwatchSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public PopwatchSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {TmdbApi api = new TmdbApi();
        Movie[] popularMovies = api.getMovies(Utility.Prefs.PREF_SORT_BY_POPULAR);
        Movie[] topMovies = api.getMovies(Utility.Prefs.PREF_SORT_BY_TOP);

        if (popularMovies.length > 0) {
            ArrayList<ContentValues> cvVector = new ArrayList<ContentValues>(popularMovies.length);

            for (int i = 0; i < popularMovies.length; i++) {
                ContentValues vals = new ContentValues();

                DateFormat sqlDateFormat = SimpleDateFormat.getDateInstance();

                vals.put(MovieEntry.COLUMN_NAME_TITLE, popularMovies[i].getTitle());
                vals.put(MovieEntry.COLUMN_NAME_POSTER_PATH, popularMovies[i].getPosterUrl());
                vals.put(MovieEntry.COLUMN_NAME_RATING, popularMovies[i].getRating());
                vals.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, sqlDateFormat.format(
                        popularMovies[i].getReleaseDate()));
                vals.put(MovieEntry.COLUMN_NAME_SUMMARY, popularMovies[i].getSummary());

                cvVector.add(vals);
            }

            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);

                // Delete existing content to avoid duplicates
                getContext().getContentResolver().delete(
                        PopwatchContract.PopularMovieEntry.CONTENT_URI, null, null);

                getContext().getContentResolver().bulkInsert(
                        PopwatchContract.PopularMovieEntry.CONTENT_URI, cvArray);
            }
        }

        if (topMovies.length > 0) {
            ArrayList<ContentValues> cvVector = new ArrayList<ContentValues>(topMovies.length);

            for (int i = 0; i < topMovies.length; i++) {
                ContentValues vals = new ContentValues();

                DateFormat sqlDateFormat = SimpleDateFormat.getDateInstance();

                vals.put(MovieEntry.COLUMN_NAME_TITLE, topMovies[i].getTitle());
                vals.put(MovieEntry.COLUMN_NAME_POSTER_PATH, topMovies[i].getPosterUrl());
                vals.put(MovieEntry.COLUMN_NAME_RATING, topMovies[i].getRating());
                vals.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, sqlDateFormat.format(
                        topMovies[i].getReleaseDate()));
                vals.put(MovieEntry.COLUMN_NAME_SUMMARY, topMovies[i].getSummary());

                cvVector.add(vals);
            }

            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);

                // Delete existing content to avoid duplicates
                getContext().getContentResolver().delete(
                        PopwatchContract.TopMovieEntry.CONTENT_URI, null, null);

                getContext().getContentResolver().bulkInsert(
                        PopwatchContract.TopMovieEntry.CONTENT_URI, cvArray);
            }
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), C.CONTENT_AUTHORITY, bundle);
    }

    // Fake account factory
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name),
                C.SYNC_ACCOUNT_TYPE);

        // If no password is returned, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            // Attempt to add the account without password or user data
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        PopwatchSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, C.CONTENT_AUTHORITY, true);

        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTIme) {
        Account account = getSyncAccount(context);
        String authority = C.CONTENT_AUTHORITY;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTIme)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
