package com.example.moviecatalog.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApiConstants {
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY = "6ccd72a2a8fc239b13f209408fc31c33";
    // api возвращает только относительные пути изображений.
    public static final String IMAGES_PATH = "https://image.tmdb.org/t/p/w500/";
    public static final String DISCOVER = "discover/movie";
    public static final String SEARCH = "search/movie";


    public static final String INITIAL_LOADING = "Initial loading";
    public static final String FIRST_LOADED = "First data loaded";
    public static final String NOT_FOUND = "Not found";
    public static final String LOADING_MORE = "Loading";
    public static final String LOADED_MORE = "Loaded more";
    public static final String ERROR = "Error";

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
