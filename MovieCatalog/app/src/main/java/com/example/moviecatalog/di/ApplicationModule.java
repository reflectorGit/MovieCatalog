package com.example.moviecatalog.di;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;


@Module
public class ApplicationModule {
    private Application mApplication;
    public ApplicationModule(Application lifecycleOwner) {
        mApplication = lifecycleOwner;
    }
    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }
}