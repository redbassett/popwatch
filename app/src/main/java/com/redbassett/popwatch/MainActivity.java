package com.redbassett.popwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    // The adapter that handles the image URLS for the posters
    private ImageArrayAdapter mPosterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPosterAdapter = new ImageArrayAdapter(this, R.layout.activity_main, new ArrayList<String>());

        GridView posterGrid = (GridView) findViewById(R.id.poster_gird);
        posterGrid.setAdapter(mPosterAdapter);

        posterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updatePopularMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePopularMovies() {
        new FetchPopularMoviesTask().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePopularMovies();
    }

    /**
     * The FetchPopularMoviesTask class connects to the API from The Movie Database asynchronously
     * so as to avoid blocking the UI thread.
     */
    public class FetchPopularMoviesTask extends AsyncTask<Void, Void, String[]> {
        /**
         * LOG_TAG provides a constant to pass to Log methods indicating the class that the log
         * message was generated in.
         */
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        /**
         * doInBackground is overridden from AsyncTask and handles the query in a background thread.
         *
         * @return String[] of movie poster URLs.
         */
        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Raw JSON response.
            String jsonStr = null;

            try {
                final String BASE_URI = "http://api.themoviedb.org/3";
                final String MOVIE_URI = "movie";
                final String POPULAR_URI = "popular";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URI).buildUpon()
                        .appendPath(MOVIE_URI)
                        .appendPath(POPULAR_URI)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Opening connection with URL: " + url.toString());

                // Create the connection, set the type, and connect
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

                // Get the buffer output as a string
                jsonStr = buffer.toString();

                Log.v(LOG_TAG, "Got result: " + jsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e);
                // We didn't get the result, so don't parse
                return null;
            } finally {
                // Close connections if they are still open
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

            // Attempt to parse JSON
            try {
                return getMoviesFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // If everythign else fails:
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mPosterAdapter.clear();
                // Unlike the Sunshine app example, the min SDK is 15, and addAll requires 11 and
                // above, so no for loops for us here
                mPosterAdapter.addAll(result);
            }
        }

        private String[] getMoviesFromJson(String jsonStr) throws JSONException {
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";

            final String TMDB_IMG_ROOT = "http://image.tmdb.org/t/p/";
            final String TMDB_IMG_SIZE_PATH = "w185";

            JSONObject resultJson = new JSONObject(jsonStr);
            JSONArray moviesJson = resultJson.getJSONArray(TMDB_RESULTS);
            int len = moviesJson.length();

            String[] posterStrs = new String[len];

            for (int i = 0; i < len; i++) {
                // Get each movie from the array
                JSONObject movie = moviesJson.getJSONObject(i);
                // Get the poster filename and strip the leading slash off
                String posterPathStr = movie.getString(TMDB_POSTER_PATH).replace("/", "");

                Uri builtUri = Uri.parse(TMDB_IMG_ROOT).buildUpon()
                        .appendPath(TMDB_IMG_SIZE_PATH)
                        .appendPath(posterPathStr)
                        .build();

                String posterUrl = builtUri.toString();

                posterStrs[i] = posterUrl;

                Log.v(LOG_TAG, "Poster: " + posterUrl);
            }

            return posterStrs;
        }
    }
}
