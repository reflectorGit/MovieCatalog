package com.example.moviecatalog.model;

import com.example.moviecatalog.api.ApiConstants;
import com.example.moviecatalog.api.IMoviesCall;

import io.reactivex.Observable;

public class MoviesRepository {
    private IMoviesCall moviesCall;
    public MoviesRepository(IMoviesCall moviesCall) {
        this.moviesCall = moviesCall;
    }

    public Observable<MovieApiResult> fetchAllMovies(int page) {
        return moviesCall.discover(ApiConstants.API_KEY, page);
    }

    public Observable<MovieApiResult> SearchMovie(String query, int page) {
        return moviesCall.search(ApiConstants.API_KEY, query, page);
    }
}
