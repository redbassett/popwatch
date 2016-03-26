package com.redbassett.popwatch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by harry on 3/26/16.
 */
public class Movie {
    protected int id;
    protected String posterUrl;

    private final String LOG_TAG = Movie.class.getSimpleName();

    public Movie() {
        this.id = 0;
        this.posterUrl = "";
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
        final String TMDB_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";

        try {
            this.setId(jsonData.getInt(TMDB_ID));
            this.setPosterUrl(jsonData.getString(TMDB_POSTER_PATH));
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
        // The Movie Databse API sends poster Uris with a leading slash, but we don't want it
        this.posterUrl = posterUrl.replace("/","");
    }
}
