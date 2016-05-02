package com.redbassett.popwatch;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.redbassett.popwatch.data.PopwatchContract;
import com.redbassett.popwatch.data.PopwatchContract.MovieEntry;
import com.redbassett.popwatch.sync.PopwatchSyncAdapter;

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int MOVIE_LOADER = 0;

    public static class Projection {
        public static final String[] MOVIE_COLUMNS = {
                MovieEntry._ID,
                MovieEntry.COLUMN_NAME_POSTER_PATH,
                MovieEntry.COLUMN_NAME_REFERENCE_ID
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_POSTER_PATH = 1;
        public static final int COL_MOVIE_REFERENCE_ID = 2;
    }

    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }

    private MovieArrayAdapter mPosterAdapter;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mPosterAdapter = new MovieArrayAdapter(getActivity(), null, 0);

        GridView posterGrid = (GridView) rootView.findViewById(R.id.poster_grid);
        posterGrid.setAdapter(mPosterAdapter);
        posterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieEntry.buildMovieUri(
                                    cursor.getInt(Projection.COL_MOVIE_ID)
                            ));
                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // The following two cases must be scoped because they define the same variable
            // See for more information: http://stackoverflow.com/a/10810847/817496
            case R.id.action_sort_popular: {
                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                prefEditor.putString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
                prefEditor.apply();
                updateMovieFeed();
                return true;
            }
            case R.id.action_sort_top: {
                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                prefEditor.putString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_top));
                prefEditor.apply();
                updateMovieFeed();
                return true;
            }
            case R.id.action_refresh:
                updateMovieFeed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovieFeed() {
        PopwatchSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = PopwatchContract.MovieEntry._ID + " ASC";
        Uri movieListUri = MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieListUri,
                Projection.MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPosterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
    }
}
