package com.example.moviecatalog.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.example.moviecatalog.model.Movie;
import com.example.moviecatalog.model.MoviesDataSourceFactory;
import com.example.moviecatalog.model.SearchMoviesDataSourceFactory;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {
    private MoviesDataSourceFactory moviesDataSourceFactory = null;
    private LiveData<PagedList<Movie>> moviesLiveData = null;
    private final int INIT_PAGE_SIZE = 10;
    private final int PAGE_SIZE = 10;

    private SearchMoviesDataSourceFactory searchMoviesDataSourceFactory = null;
    private LiveData<PagedList<Movie>> searchedMoviesLiveData = null;
    private PagedList.Config searchedMoviesPagedListConfig = null;

    @Inject
    public MovieViewModel(MoviesDataSourceFactory moviesDataSourceFactory, SearchMoviesDataSourceFactory searchMoviesDataSourceFactory) {
        this.moviesDataSourceFactory = moviesDataSourceFactory;
        this.searchMoviesDataSourceFactory = searchMoviesDataSourceFactory;
        initializePaging();
    }

    private void initializePaging() {
        PagedList.Config allMoviesPagedListConfig =
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setInitialLoadSizeHint(INIT_PAGE_SIZE)
                        .setPageSize(PAGE_SIZE)
                        .build();

        this.moviesLiveData = new LivePagedListBuilder<>(this.moviesDataSourceFactory, allMoviesPagedListConfig).build();

        this.searchedMoviesPagedListConfig =
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setInitialLoadSizeHint(INIT_PAGE_SIZE)
                        .setPageSize(PAGE_SIZE)
                        .build();

        this.searchedMoviesLiveData = new LivePagedListBuilder<>(this.searchMoviesDataSourceFactory, searchedMoviesPagedListConfig).build();
    }

    public LiveData<String> getInitLoadStatus() {
        return this.moviesDataSourceFactory.getMoviesDataSource().getInitLiveStatus();
    }

    public LiveData<String> getProgressLoadStatus() {
        return this.moviesDataSourceFactory.getMoviesDataSource().getProgressLiveStatus();
    }


    public LiveData<PagedList<Movie>> getListLiveData() {
        return this.moviesLiveData;
    }

    public LiveData<String> getInitSearchStatus() {
        return this.searchMoviesDataSourceFactory.getSearchMoviesDataSource().getInitSearchLiveStatus();
    }

    public LiveData<String> getProgressSearchStatus() {
        return this.searchMoviesDataSourceFactory.getSearchMoviesDataSource().getProgressSearchLiveStatus();
    }

    public LiveData<PagedList<Movie>> getSearchedMoviesLiveData(String query) {
        this.searchMoviesDataSourceFactory.getSearchMoviesDataSource().clear();
        // Здаесь важен порядок:
        // Сначала изменяем datasourceFactory,
        this.searchMoviesDataSourceFactory.getSearchMoviesDataSource().setQueryString(query);
        // Затем на основе его, конструируем liveData.
        this.searchedMoviesLiveData = new LivePagedListBuilder<>(this.searchMoviesDataSourceFactory, searchedMoviesPagedListConfig).build();
        return this.searchedMoviesLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.moviesDataSourceFactory.getMoviesDataSource().clear();
        this.searchMoviesDataSourceFactory.getSearchMoviesDataSource().clear();
    }
}
