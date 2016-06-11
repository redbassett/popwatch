package com.redbassett.popwatch.MovieApi;

import android.net.Uri;
import android.util.Log;

import com.redbassett.popwatch.BuildConfig;
import com.redbassett.popwatch.Movie;
import com.redbassett.popwatch.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TmdbApi extends MovieApi {
    private static final String LOG_TAG = TmdbApi.class.getSimpleName();

    private static final String BASE_URI = "http://api.themoviedb.org/3";
    private static final String MOVIE_URI = "movie";
    private static final String POPULAR_URI = "popular";
    private static final String TOP_URI = "top_rated";
    private static final String API_KEY_PARAM = "api_key";
    private static final String VIDEOS_URI = "videos";

    private static final String TMDB_RESULTS = "results";
    private static final String TMDB_VIDEO_KEY = "key";

    private static final String TMDB_IMG_ROOT = "http://image.tmdb.org/t/p/";
    private static final String TMDB_IMG_SIZE_PATH = "w185";

    private static final String YOUTUBE_IMAGE_ROOT = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_FILE = "0.jpg";

    public Movie[] getMovies(String type) {
        try {
            String typeUri = (type.equals(Utility.Prefs.PREF_SORT_BY_TOP)) ? TOP_URI : POPULAR_URI;

            Uri builtUri = Uri.parse(BASE_URI).buildUpon()
                    .appendPath(MOVIE_URI)
                    .appendPath(typeUri)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            String jsonStr = this.getJSONFromUrl(url);

            JSONObject resultJson = new JSONObject(jsonStr);
            JSONArray moviesJson = resultJson.getJSONArray(TMDB_RESULTS);
            int len = moviesJson.length();

            Movie[] movies = new Movie[len];

            for (int i = 0; i < len; i++) {
                // Get each movie from the array
                movies[i] = new Movie(moviesJson.getJSONObject(i));
            }

            return movies;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        } catch(MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    public String getMovieTrailer(long id) {
        try {
            Uri builtUri = Uri.parse(BASE_URI).buildUpon()
                    .appendPath(MOVIE_URI)
                    .appendPath(String.valueOf(id))
                    .appendPath(VIDEOS_URI)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            String jsonStr = this.getJSONFromUrl(url);

            JSONObject resultJson = new JSONObject(jsonStr);
            JSONArray videosJson = resultJson.getJSONArray(TMDB_RESULTS);
            int len = videosJson.length();

            if (len > 0) {
                JSONObject firstVideo = videosJson.getJSONObject(0);
                return firstVideo.getString(TMDB_VIDEO_KEY);
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    private String getJSONFromUrl(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            // Check if we got an empty buffer
            if (buffer.length() == 0) {
                return null;
            }

            jsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return jsonStr;
    }

    public static String generatePosterImageUrl(String path) {
        return Uri.parse(TMDB_IMG_ROOT).buildUpon().appendPath(TMDB_IMG_SIZE_PATH)
                .appendPath(path.replace("/","")).build().toString();
    }

    public static String generateTrailerThumbImageUrl(String trailerId) {
        return Uri.parse(YOUTUBE_IMAGE_ROOT).buildUpon().appendPath(trailerId)
                .appendPath(YOUTUBE_IMAGE_FILE).build().toString();
    }
}
