package com.redbassett.popwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class MovieDetailActivity extends AppCompatActivity {
    private Movie mMovie;

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Bundle payload = intent.getExtras();
            mMovie = (Movie) payload.getParcelable("movie");

            ImageView posterView = (ImageView) findViewById(R.id.movie_poster_image);
            Picasso.with(this).load(mMovie.getPosterUrl()).into(posterView);

            TextView titleView = (TextView) findViewById(R.id.movie_title);
            titleView.setText(mMovie.getTitle());

            TextView summaryView = (TextView) findViewById(R.id.movie_summary);
            summaryView.setText(mMovie.summary);

            TextView releaseDate = (TextView) findViewById(R.id.movie_release_date);
            SimpleDateFormat releaseDateFormat = new SimpleDateFormat(getString(R.string.date_format));
            releaseDate.setText(releaseDateFormat.format(mMovie.getReleaseDate()));

            RatingBar ratingView = (RatingBar) findViewById(R.id.movie_rating);
            ratingView.setRating((float) mMovie.getRating()/2);
        }
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_movie_detail, container, false);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Bundle payload = intent.getExtras();
            mMovie = (Movie) payload.getParcelable("movie");

            ImageView posterView = (ImageView) rootView.findViewById(R.id.movie_poster_image);
            Picasso.with(this).load(mMovie.getPosterUrl()).into(posterView);

            TextView titleView = (TextView) rootView.findViewById(R.id.movie_title);
            titleView.setText(mMovie.getId());
        }
        return rootView;
    }
}
