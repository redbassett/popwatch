package com.redbassett.popwatch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harry on 3/26/16.
 */
public class Movie {
    protected int id;
    protected String posterUrl;
    protected String title;
    protected String summary;
    protected double rating;
    protected Date releaseDate;
    protected boolean hasTrailer;


    protected static final String TMDB_ID = "id";
    protected static final String TMDB_POSTER_PATH = "poster_path";
    protected static final String TMDB_TITLE = "original_title";
    protected static final String TMDB_SUMMARY = "overview";
    protected static final String TMDB_RATING = "vote_average";
    protected static final String TMDB_RELEASE_DATE = "release_date";
    protected static final String TMDB_HAS_TRAILER = "video";

    private static final String LOG_TAG = Movie.class.getSimpleName();

    public Movie() {
        this.id = 0;
        this.posterUrl = "";
        this.title = "";
        this.summary = "";
        this.rating = 0.0;
        this.releaseDate = new Date();
        this.hasTrailer = false;
    }

    public Movie(JSONObject jsonData) {
        this._construct(jsonData);
    }

    public Movie(String jsonStr) {
        try {
            this._construct(new JSONObject(jsonStr));
        } catch (JSONException e) {
            Log.e(LOG_TAG, String.format("Failed to parse JSON Object: %s", e.getMessage()));
        }
    }

    // Delegate construction here so that both constructors can reference it. Movie(String jsonStr)
    // could have referenced Movie(JSONObject jsonObject), but Java requires calls to this() to be
    // the first statement in a method, so it couldn't be done inside the try.
    private void _construct(JSONObject jsonData) {
        try {
            this.setId(jsonData.getInt(TMDB_ID));
            this.setPosterUrl(jsonData.getString(TMDB_POSTER_PATH));
            this.setTitle(jsonData.getString(TMDB_TITLE));
            this.setSummary(jsonData.getString(TMDB_SUMMARY));
            this.setRating(jsonData.getDouble(TMDB_RATING));
            this.setReleaseDate(jsonData.getString(TMDB_RELEASE_DATE));
            this.setHasTrailer(jsonData.getBoolean(TMDB_HAS_TRAILER));
        } catch (JSONException e) {
            Log.e(LOG_TAG, String.format("Failed to parse JSON Object: %s", e.getMessage()));
        }
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public long getReleaseDateAsLong() {
        return this.releaseDate.getTime();
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setReleaseDate(String releaseDateString) {
        try {
            this.releaseDate = new SimpleDateFormat("y-M-d").parse(releaseDateString);
        } catch (ParseException e) {
            Log.e(LOG_TAG, String.format("Cannot parse date: %s", e.getMessage()));
            this.releaseDate = new Date();
        }
    }

    public boolean getHasTrailer() {
        return this.hasTrailer;
    }

    public void setHasTrailer(boolean hasTrailer) {
        this.hasTrailer = hasTrailer;
    }
}
