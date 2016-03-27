package com.redbassett.popwatch;

import android.content.Context;
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
        String path = getItem(position).getPosterUrl();
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(path).into(imageView);
        return imageView;
    }

    @Override
    public void addAll(Movie[] results) {
        super.addAll(results);
    }
}