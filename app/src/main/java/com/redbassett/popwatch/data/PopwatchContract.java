package com.redbassett.popwatch.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PopwatchContract {
    // No new instances for you
    public PopwatchContract() {}

    // The content authority for the MovieProvider
    public static final String CONTENT_AUTHORITY = "com.redbassett.popwatch";

    // The base ocntent uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths for content URIs
    public static final String MOVIE_PATH = "movie";

    public static abstract class MovieEntry implements BaseColumns {

        // Movie table name
        public static final String TABLE_NAME = "movie";

        // The TMDB ID of the movie, stored as a long
        public static final String COLUMN_NAME_REFERENCE_ID = "ref_id";

        // Title of the movie, stored as a string
        public static final String COLUMN_NAME_TITLE = "title";

        // Poster image path, stored as a string
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";

        // Summary, stored as a string
        // This is called the "overview" in TMDB API
        public static final String COLUMN_NAME_SUMMARY = "summary";

        // Release date, stored as a date
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";

        // Ratings, stored as a float
        // This is called the "vote_average" in TMDB API
        public static final String COLUMN_NAME_RATING = "rating";

        // Trailer url, stored as a string
        public static final String COLUMN_NAME_TRAILER_URL = "trailer_url";

        // The movie content uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();

        // Content types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
    }

    public static abstract class PopularMovieEntry extends MovieEntry {
        public static final String TABLE_NAME = "popular_movies";
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon().appendPath("pop")
                .build();
    }

    public static abstract class TopMovieEntry extends MovieEntry {
        public static final String TABLE_NAME = "top_movies";
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon().appendPath("top")
                .build();
    }

    public static abstract class FavMovieEntry extends MovieEntry {
        public static final String TABLE_NAME = "fav_movies";
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon().appendPath("fav")
                .build();
    }
}
