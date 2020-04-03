package com.example.moviecatalog.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

public class MoviesDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    private MutableLiveData<MoviesDataSource> moviesDataSourceLiveData = null;
    private MoviesDataSource moviesDataSource = null;

    public MoviesDataSourceFactory(MoviesRepository repository) {
        this.moviesDataSource = new MoviesDataSource(repository);
        this.moviesDataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, Movie> create() {
        this.moviesDataSourceLiveData.postValue(moviesDataSource);
        return this.moviesDataSource;
    }

    public MoviesDataSource getMoviesDataSource() {
        return this.moviesDataSource;
    }


}
