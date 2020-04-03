package com.example.moviecatalog;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import com.example.moviecatalog.adapter.MovieAdapter;
import com.example.moviecatalog.api.ApiConstants;
import com.example.moviecatalog.databinding.ActivityMainBinding;
import com.example.moviecatalog.di.ApplicationModule;
import com.example.moviecatalog.di.DaggerIComponent;
import com.example.moviecatalog.di.IComponent;
import com.example.moviecatalog.model.Movie;
import com.example.moviecatalog.viewModel.MovieViewModel;
import com.example.moviecatalog.viewModel.ViewModelFactory;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private MovieAdapter adapter;
    private ActivityMainBinding binding;
    private String query;
    private final String QUERY_STRING_KEY = "query_string";

    @Inject
    MovieViewModel movieViewModel = null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY_STRING_KEY, this.query);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            this.query = savedInstanceState.getString(QUERY_STRING_KEY, null);
        }

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initComponents();

        initControls();

        checkConnectionAndRun();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity.this.query = query;
                search(MainActivity.this.query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                loadAllMovies();
            }
        });

        return true;
    }



    private void initComponents() {
        IComponent viewModelFactoryComponent = DaggerIComponent
                .builder()
                .applicationModule(new ApplicationModule(this.getApplication()))
                .build();
        ViewModelFactory viewModelFactory = viewModelFactoryComponent.inject();
        movieViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieViewModel.class);
    }

    private void initControls() {
        this.binding.moviesRv.setLayoutManager(new LinearLayoutManager(this));
        this.binding.moviesRv.setHasFixedSize(true);

        this.adapter = new MovieAdapter();
        this.adapter.setContext(this);
        this.binding.moviesRv.setAdapter(adapter);

        this.binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkConnectionAndRun();
                MainActivity.this.binding.swipeContainer.setRefreshing(false);
            }
        });
    }

    private void checkConnectionAndRun() {
        boolean isConnected = ApiConstants.isConnected(this);
        if(!isConnected) {
            Snackbar.make(this.binding.moviesRv, "Интернет не доступен", Snackbar.LENGTH_LONG).show();
        }
        else {
            search(this.query);
        }
    }

    private void loadAllMovies() {
        movieViewModel.getListLiveData().observe(this, this::submitMovies);
        movieViewModel.getInitLoadStatus().observe(this, this::setInitialState);
        movieViewModel.getProgressLoadStatus().observe(this, this::setAdapterState);

    }

    // If queryString not null it'll starts searching,
    // else it'll load all movies.
    private void search(String query) {
        if(query != null && !query.isEmpty()) {
            movieViewModel.getSearchedMoviesLiveData(query).observe(this, this::submitSearched);
            movieViewModel.getInitSearchStatus().observe(this, this::setInitialState);
            movieViewModel.getProgressSearchStatus().observe(this, this::setAdapterState);
        }
        else {
            loadAllMovies();
        }
    }

    /* todo можно было вывести фрагмент с drawable и textview для ошибок и с recyclerView для результата.*/
    // At the first loading.
    private void setInitialState(String state) {
        // The first page loading.
        if(state.equalsIgnoreCase(ApiConstants.INITIAL_LOADING)) {
            this.binding.progressBar.setIndeterminate(true);
            this.binding.progressBar.setVisibility(View.VISIBLE);
        }

        // Not found results for the first page.
        else if(state.equalsIgnoreCase(ApiConstants.NOT_FOUND)) {
            this.binding.progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(this.binding.moviesRv, "Ничего не найдено", Snackbar.LENGTH_LONG).show();
        }

        else if (state.equalsIgnoreCase(ApiConstants.ERROR)) {
            this.binding.progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(this.binding.moviesRv, "Нам не удалось обработать ваш запрос. Попробуйте позже.", Snackbar.LENGTH_LONG).show();
        }

        // ApiConstants.FIRST_LOADED
        else{
            this.binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void submitMovies(PagedList<Movie> movies) {
        adapter.submitList(movies);
    }

    private void submitSearched(PagedList<Movie> movies) {
        adapter.submitList(movies);
    }

    private void setAdapterState(String state) {
        adapter.setNetworkState(state);
    }
}
