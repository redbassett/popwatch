package com.redbassett.popwatch;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.redbassett.popwatch.MovieApi.TmdbApi;
import com.redbassett.popwatch.data.PopwatchContract;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    public static class Projection {
        public static final String[] DETAIL_COLUMNS = {
                PopwatchContract.MovieEntry._ID,
                PopwatchContract.MovieEntry.COLUMN_NAME_POSTER_PATH,
                PopwatchContract.MovieEntry.COLUMN_NAME_TRAILER_URL,
                PopwatchContract.MovieEntry.COLUMN_NAME_RATING,
                PopwatchContract.MovieEntry.COLUMN_NAME_SUMMARY,
                PopwatchContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
                PopwatchContract.MovieEntry.COLUMN_NAME_TITLE
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_POSTER_PATH = 1;
        public static final int COL_TRAILER_URL = 2;
        public static final int COL_RATING = 3;
        public static final int COL_SUMMARY = 4;
        public static final int COL_RELEASE_DATE = 5;
        public static final int COL_TITLE = 6;
    }

    private ContentValues mMovieRow;

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mSummaryView;
    private TextView mReleaseDateView;
    private RatingBar mRatingView;
    private YouTubeThumbnailView mTrailerView;
    private ListView mReviewsList;

    private YouTubeThumbnailLoader mTrailerLoader;

    private MenuItem mActionFav;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        mMovieRow = new ContentValues();

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_poster_image);
        mTitleView = (TextView) rootView.findViewById(R.id.movie_title);
        mSummaryView = (TextView) rootView.findViewById(R.id.movie_summary);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.movie_release_date);
        mRatingView = (RatingBar) rootView.findViewById(R.id.movie_rating);
        mTrailerView = (YouTubeThumbnailView) rootView.findViewById(R.id.movie_trailer_thumbnail);
        mReviewsList = (ListView) rootView.findViewById(R.id.movie_review_list);

        mTrailerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                        BuildConfig.YOUTUBE_DATA_API_KEY, (String) v.getTag());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (mTrailerLoader != null) {
            mTrailerLoader.release();
            mTrailerLoader = null;
        }
        
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_fragment, menu);
        mActionFav = menu.findItem(R.id.action_fav);
        if (mMovieRow != null)
            updateFavIcon(isFavorited());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fav:
                toggleFavorite();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isFavorited() {
        long currentId = mMovieRow.getAsLong(Projection.DETAIL_COLUMNS[Projection.COL_MOVIE_ID]);

        Cursor result = getContext().getContentResolver().query(
                PopwatchContract.FavMovieEntry.CONTENT_URI,
                Projection.DETAIL_COLUMNS,
                "_ID = ?",
                new String[]{String.valueOf(currentId)},
                null
        );

        return result.moveToFirst();
    }

    private boolean toggleFavorite() {
        long currentId = mMovieRow.getAsLong(Projection.DETAIL_COLUMNS[Projection.COL_MOVIE_ID]);
        boolean faved = isFavorited();

        if (faved) {
            if (getContext().getContentResolver().delete(
                    PopwatchContract.FavMovieEntry.CONTENT_URI,
                    "_ID = ?",
                    new String[]{String.valueOf(currentId)}
            ) == 1) {
                updateFavIcon(false);
                return false;
            } else
                throw new android.database.SQLException(
                        "Attempted to unfavorite movie not found in favorites " +
                        String.valueOf(currentId));
        } else {
            ContentValues vals = mMovieRow;

            getContext().getContentResolver().insert(
                    PopwatchContract.FavMovieEntry.CONTENT_URI,
                    vals
            );

            updateFavIcon(true);
            return true;
        }

    }

    private void updateFavIcon(boolean faved) {
        mActionFav.setIcon((faved) ?
                R.drawable.ic_star_white_24px : R.drawable.ic_star_border_white_24px);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    Projection.DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(data, mMovieRow);

            if (mActionFav != null)
                updateFavIcon(isFavorited());

            String posterPath = data.getString(Projection.COL_POSTER_PATH);
            Picasso.with(getActivity()).load(TmdbApi.generatePosterImageUrl(posterPath)).into(mPosterView);

            float rating = data.getFloat(Projection.COL_RATING);
            mRatingView.setRating((float) rating/2);

            String summary = data.getString(Projection.COL_SUMMARY);
            mSummaryView.setText(summary);

            DateFormat releaseDateFormat = DateFormat.getDateInstance();
            try {
                Date releaseDate = releaseDateFormat.parse(data.getString(Projection.COL_RELEASE_DATE));
                mReleaseDateView.setText(releaseDateFormat.format(releaseDate));
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            String title = data.getString(Projection.COL_TITLE);
            mTitleView.setText(title);

            long movieId = data.getLong(Projection.COL_MOVIE_ID);

            FetchTrailerTask trailerTask = new FetchTrailerTask();
            trailerTask.execute(movieId);

            mReviewsList.setAdapter(null);
            FetchReviewTask reviewTask = new FetchReviewTask();
            reviewTask.execute(movieId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    // Async class to load movie trailers
    public class FetchTrailerTask extends AsyncTask<Long, Void, String>
        implements YouTubeThumbnailView.OnInitializedListener {

        @Override
        protected String doInBackground(Long... params) {
            if (params.length == 0)
                return null;

            return new TmdbApi().getMovieTrailer(params[0]);
        }

        @Override
        protected void onPostExecute(String trailerId) {
            mTrailerView.setTag(trailerId);
            mTrailerView.initialize(BuildConfig.YOUTUBE_DATA_API_KEY, this);
        }

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView view,
                                            YouTubeThumbnailLoader loader) {
            loader.setVideo((String) view.getTag());
            mTrailerLoader = loader;
        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView view,
                                            YouTubeInitializationResult error) {
            final String errMsg = error.toString();
            Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
        }
    }

    // Async class to load movie reviews
    public class FetchReviewTask extends AsyncTask<Long, Void, String[]> {
        @Override
        protected String[] doInBackground(Long... params) {
            if (params.length == 0)
                return null;

            return new TmdbApi().getMovieReviews(params[0]);
        }

        @Override
        protected void onPostExecute(String[] reviews) {
            if (reviews == null)
                return;

            ArrayAdapter<String> reviewAdapter = new ArrayAdapter<String>(getContext(),
                    R.layout.list_item_review, reviews);

            mReviewsList.setAdapter(reviewAdapter);
        }
    }
}
