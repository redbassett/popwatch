package com.redbassett.popwatch;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harry on 3/26/16.
 */
public class Movie implements Parcelable {
    protected int id;
    protected String posterUrl;
    protected String title;
    protected String summary;
    protected double rating;
    protected Date releaseDate;

    protected static final String TMDB_IMG_ROOT = "http://image.tmdb.org/t/p/";
    protected static final String TMDB_IMG_SIZE_PATH = "w185";
    protected static final String TMDB_ID = "id";
    protected static final String TMDB_POSTER_PATH = "poster_path";
    protected static final String TMDB_TITLE = "original_title";
    protected static final String TMDB_SUMMARY = "overview";
    protected static final String TMDB_RATING = "vote_average";
    protected static final String TMDB_RELEASE_DATE = "release_date";

    private static final String LOG_TAG = Movie.class.getSimpleName();

    public Movie() {
        this.id = 0;
        this.posterUrl = "";
        this.title = "";
        this.summary = "";
        this.rating = 0.0;
        this.releaseDate = new Date();
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
        posterUrl = posterUrl.replace("/","");

        this.posterUrl = Uri.parse(this.TMDB_IMG_ROOT).buildUpon()
                .appendPath(this.TMDB_IMG_SIZE_PATH)
                .appendPath(posterUrl)
                .build().toString();
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

    /**
     * This code below implements Parcelable
     *
     * Based on http://www.developerphil.com/parcelable-vs-serializable/
     * (and a number of similar StackOverflow contributions)
     */
    public Movie(Parcel in) {
        this.id = in.readInt();
        this.posterUrl = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.id);
        out.writeString(this.posterUrl);
        out.writeString(this.title);
        out.writeString(this.summary);
        out.writeDouble(this.rating);
        out.writeLong(this.releaseDate.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
