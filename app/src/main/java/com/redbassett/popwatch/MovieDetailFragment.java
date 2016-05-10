package com.redbassett.popwatch;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
                PopwatchContract.MovieEntry.COLUMN_NAME_REFERENCE_ID,
                PopwatchContract.MovieEntry.COLUMN_NAME_TRAILER_URL,
                PopwatchContract.MovieEntry.COLUMN_NAME_RATING,
                PopwatchContract.MovieEntry.COLUMN_NAME_SUMMARY,
                PopwatchContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
                PopwatchContract.MovieEntry.COLUMN_NAME_TITLE
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_POSTER_PATH = 1;
        public static final int COL_MOVIE_REFERENCE_ID = 2;
        public static final int COL_TRAILER_URL = 3;
        public static final int COL_RATING = 4;
        public static final int COL_SUMMARY = 5;
        public static final int COL_RELEASE_DATE = 6;
        public static final int COL_TITLE = 7;
    }

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mSummaryView;
    private TextView mReleaseDateView;
    private RatingBar mRatingView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_poster_image);
        mTitleView = (TextView) rootView.findViewById(R.id.movie_title);
        mSummaryView = (TextView) rootView.findViewById(R.id.movie_summary);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.movie_release_date);
        mRatingView = (RatingBar) rootView.findViewById(R.id.movie_rating);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
