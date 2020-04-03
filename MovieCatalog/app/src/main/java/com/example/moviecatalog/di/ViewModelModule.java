package com.example.moviecatalog.di;


import android.arch.lifecycle.ViewModel;

import com.example.moviecatalog.model.MoviesDataSourceFactory;
import com.example.moviecatalog.model.MoviesRepository;
import com.example.moviecatalog.model.SearchMoviesDataSourceFactory;
import com.example.moviecatalog.viewModel.MovieViewModel;
import com.example.moviecatalog.viewModel.ViewModelFactory;

import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.Provides;

import javax.inject.Provider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Module(includes = {RepositoryModule.class })
public class ViewModelModule {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    @Provides
    ViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {
        return new ViewModelFactory(providerMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(MovieViewModel.class)
    ViewModel viewModel1(MoviesRepository moviesRepository) {
        MoviesDataSourceFactory moviesDataSourceFactory = new MoviesDataSourceFactory(moviesRepository);
        SearchMoviesDataSourceFactory searchMoviesDataSourceFactory = new SearchMoviesDataSourceFactory(moviesRepository);
        return new MovieViewModel(moviesDataSourceFactory, searchMoviesDataSourceFactory);
    }
}