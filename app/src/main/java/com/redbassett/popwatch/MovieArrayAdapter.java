package com.redbassett.popwatch;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redbassett.popwatch.MovieApi.TmdbApi;
import com.redbassett.popwatch.MovieListFragment.Projection;
import com.squareup.picasso.Picasso;

/**
 * Created by harry on 3/25/16.
 */
public class MovieArrayAdapter extends CursorAdapter {

    public MovieArrayAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        this.bindView(imageView, context, cursor);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Movie mov = new Movie();
        mov.setId(cursor.getInt(Projection.COL_MOVIE_ID));
        mov.setPosterUrl(cursor.getString(Projection.COL_POSTER_PATH));

        ImageView imageView = (ImageView) view;
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Picasso.with(context).load(TmdbApi.generatePosterImageUrl(
                mov.getPosterUrl())).into(imageView);
    }
}
