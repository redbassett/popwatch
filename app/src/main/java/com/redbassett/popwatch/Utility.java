package com.redbassett.popwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.redbassett.popwatch.data.PopwatchContract;

import java.util.HashMap;

public class Utility {
    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Prefs.PREF_SORT, (String) Prefs.DEFAULTS.get(Prefs.PREF_SORT));
    }

    public static class Prefs {
        public static final String PREF_SORT = "sort";
        public static final String PREF_SORT_BY_POPULAR = "popular";
        public static final String PREF_SORT_BY_TOP = "top";
        public static final String PREF_SORT_BY_FAV = "fav";

        public static final String PREF_FAV = "favorites";

        public static final HashMap<String, Object> DEFAULTS = new HashMap<String, Object>(){{
            put(PREF_SORT, PREF_SORT_BY_POPULAR);
            put(PREF_FAV, null);
        }};
    }

    // Constants
    public static class C {
        public static final String CONTENT_AUTHORITY = PopwatchContract.CONTENT_AUTHORITY;

        public static final String SYNC_ACCOUNT_TYPE = "popwatch.redbassett.com";

        public static final String YOUTUBE_VIDEO_ROOT = "http://youtube.com/watch?v=";
    }
}
