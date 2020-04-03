package com.example.moviecatalog.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

public class SearchMoviesDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    private MutableLiveData<SearchMoviesDataSource> searchMoviesDataSourceMutableLiveData = null;
    private SearchMoviesDataSource searchMoviesDataSource = null;

    public SearchMoviesDataSourceFactory(MoviesRepository repository) {
        this.searchMoviesDataSource = new SearchMoviesDataSource(repository);
        this.searchMoviesDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<SearchMoviesDataSource> getMutableLiveData() {
        return searchMoviesDataSourceMutableLiveData;
    }

    @Override
    public DataSource<Integer, Movie> create() {
        this.searchMoviesDataSourceMutableLiveData.postValue(searchMoviesDataSource);
        return this.searchMoviesDataSource;
    }

    public SearchMoviesDataSource getSearchMoviesDataSource() {
        return this.searchMoviesDataSource;
    }
}