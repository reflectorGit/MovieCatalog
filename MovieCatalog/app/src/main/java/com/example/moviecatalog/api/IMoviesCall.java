package com.example.moviecatalog.api;

import com.example.moviecatalog.model.MovieApiResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IMoviesCall {
    @GET(ApiConstants.DISCOVER)
    Observable<MovieApiResult> discover(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(ApiConstants.SEARCH)
    Observable<MovieApiResult> search(@Query("api_key") String apiKey, @Query("query") String query, @Query("page") int page);
}
