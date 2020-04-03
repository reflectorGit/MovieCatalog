package com.example.moviecatalog.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class Preferences {
    public static boolean isFavourite(Context context, int id) {
        boolean isFavourite = false;
        String key = Integer.toString(id);
        if(PreferenceManager.getDefaultSharedPreferences(context).contains(key)) {
            isFavourite = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
        }
        return isFavourite;
    }

    public static void setFavourite(Context context, int id, boolean value) {
        String key = Integer.toString(id);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

}
