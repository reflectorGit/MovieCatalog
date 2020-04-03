package com.example.moviecatalog.di;

import com.example.moviecatalog.api.IMoviesCall;
import com.example.moviecatalog.model.MoviesRepository;
import dagger.Module;
import dagger.Provides;

@Module (includes = MoviesApiModule.class)
public class RepositoryModule {
    @Provides
    MoviesRepository provideRepository(IMoviesCall api) {
        return new MoviesRepository(api);
    }
}
