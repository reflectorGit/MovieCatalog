package com.example.moviecatalog.di;

import com.example.moviecatalog.viewModel.ViewModelFactory;
import dagger.Component;
import javax.inject.Singleton;

@Component( modules = {ApplicationModule.class, ViewModelModule.class})
@Singleton
public interface IComponent {
    ViewModelFactory inject();
}