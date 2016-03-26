package com.redbassett.popwatch;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by harry on 3/25/16.
 */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private Context mContext;

    public MovieArrayAdapter(Context c, int r, ArrayList<Movie> t) {
        super(c, r, t);
        mContext = c;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final String TMDB_IMG_ROOT = "http://image.tmdb.org/t/p/";
        final String TMDB_IMG_SIZE_PATH = "w185";

        String path = getItem(position).getPosterUrl();
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        String posterURL = Uri.parse(TMDB_IMG_ROOT).buildUpon()
                .appendPath(TMDB_IMG_SIZE_PATH)
                .appendPath(path)
                .build().toString();

        Picasso.with(mContext).load(posterURL).into(imageView);
        return imageView;
    }

    @Override
    public void addAll(Movie[] results) {
        super.addAll(results);
    }
}