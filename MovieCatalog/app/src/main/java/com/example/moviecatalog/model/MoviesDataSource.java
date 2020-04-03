package com.example.moviecatalog.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.moviecatalog.api.ApiConstants;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoviesDataSource extends ItemKeyedDataSource<Integer, Movie> {
    private MoviesRepository repository;
    private MutableLiveData<String> initLiveStatus;
    private MutableLiveData<String> progressLiveStatus;
    private CompositeDisposable compositeDisposable;
    private int pageNumber = 1;
    private final String TAG = "MoviesDataSource";

    public MoviesDataSource(MoviesRepository repository) {
        this.repository = repository;
        this.compositeDisposable = new CompositeDisposable();
        this.initLiveStatus = new MutableLiveData<>();
        this.progressLiveStatus = new MutableLiveData<>();
    }

    public MutableLiveData<String> getProgressLiveStatus() {
        return progressLiveStatus;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Movie> callback) {
        initLiveStatus.postValue(ApiConstants.INITIAL_LOADING);
        Disposable moviesDisposable = repository.fetchAllMovies(pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> onMoviesLoaded(movies.getResults(), callback), this::onError);
        compositeDisposable.add(moviesDisposable);
    }

    private void onMoviesLoaded(List<Movie> movies, LoadInitialCallback<Movie> callback) {
        initLiveStatus.postValue(ApiConstants.FIRST_LOADED);
        pageNumber++;
        callback.onResult(movies);
    }

    private void onError(Throwable throwable) {
        initLiveStatus.postValue(ApiConstants.ERROR);
        Log.e(TAG, throwable.getMessage());
    }


    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Movie> callback) {
        progressLiveStatus.postValue(ApiConstants.LOADING_MORE);

        Disposable moviesDisposable = repository.fetchAllMovies(pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> onMoreMoviesLoaded(movies.getResults(), callback), this::onPaginationError);

        compositeDisposable.add(moviesDisposable);
    }

    private void onMoreMoviesLoaded(List<Movie> movies, LoadCallback<Movie> callback) {
        progressLiveStatus.postValue(ApiConstants.LOADED_MORE);
        pageNumber++;
        callback.onResult(movies);
    }

    private void onPaginationError(Throwable throwable) {
        progressLiveStatus.postValue(ApiConstants.ERROR);
        Log.e(TAG, throwable.getMessage());
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Movie> callback) {

    }

    @NonNull
    @Override
    public Integer getKey(@NonNull Movie item) {
        return pageNumber;
    }

    public void clear() {
        compositeDisposable.clear();
        pageNumber = 1;
    }

    public MutableLiveData<String> getInitLiveStatus() {
        return initLiveStatus;
    }

}