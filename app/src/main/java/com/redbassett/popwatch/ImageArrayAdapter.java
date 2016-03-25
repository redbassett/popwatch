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
public class ImageArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;

    public ImageArrayAdapter(Context c, int r, ArrayList<String> t) {
        super(c, r, t);
        mContext = c;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String path = getItem(position);
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(path).into(imageView);
        return imageView;
    }
}