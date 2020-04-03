package com.example.moviecatalog.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.moviecatalog.R;
import com.example.moviecatalog.api.ApiConstants;
import com.example.moviecatalog.databinding.FragmentMovieItemBinding;
import com.example.moviecatalog.databinding.ProgressItemBinding;
import com.example.moviecatalog.model.Movie;
import com.example.moviecatalog.utils.Preferences;

public class MovieAdapter extends PagedListAdapter<Movie, MovieAdapter.CustomViewHolder> {
    private String state;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;
    public MovieAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch (i) {
            case R.layout.progress_item:
                ProgressItemBinding progressItemBinding =
                        ProgressItemBinding.inflate(layoutInflater, viewGroup, false);
                Log.i("onCreateViewHolder ", "progress item view holder created");
                return new ProgressItemViewHolder(progressItemBinding);
            case R.layout.fragment_movie_item:
            default:
                FragmentMovieItemBinding movieItemBinding =
                        FragmentMovieItemBinding.inflate(layoutInflater, viewGroup, false);
                return new MovieItemViewHolder(movieItemBinding);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + getFooter();
    }



    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.progress_item:
                ProgressItemViewHolder loadingHolder = (ProgressItemViewHolder) holder;
                loadingHolder.progressItemBinding.progressItem.setIndeterminate(true);
                loadingHolder.progressItemBinding.progressItem.setVisibility(View.VISIBLE);
                break;
            case R.layout.fragment_movie_item:
            default:
                Movie movie = getItem(position);
                MovieItemViewHolder movieItemViewHolder = (MovieItemViewHolder) holder;
                movieItemViewHolder.binding.setModel(movie);

                if(Preferences.isFavourite(context, movie.getId())) {
                    movieItemViewHolder.binding.isFavourite.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favour));
                }
                else {
                    movieItemViewHolder.binding.isFavourite.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.ic_not_favour));
                }
                break;
        }
    }

    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {

        @Override
        public boolean areItemsTheSame(@NonNull Movie movie, @NonNull Movie t1) {
            return movie.getId().equals(t1.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movie movie, @NonNull Movie t1) {
            return movie.equals(t1);
        }
    };

    @Override
    public int getItemViewType(int position) {
        // Reached at the end
        if (hasFooter() && position == getItemCount() - 1) {
            if (this.state.equalsIgnoreCase(ApiConstants.LOADING_MORE)) {
                return R.layout.progress_item;
            } else {
                throw new IllegalStateException("Illegal state exception");
            }
        } else {
            return R.layout.fragment_movie_item;
        }
    }

    public void setNetworkState(String newState) {
        String previousState = this.state;
        boolean hadFooter = hasFooter();
        this.state = newState;
        boolean hasFooter = hasFooter();
        if (hadFooter != hasFooter) {
            if (hadFooter) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasFooter && !previousState.equals(newState)) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private int getFooter() {
        return hasFooter() ? 1 : 0;
    }

    private boolean hasFooter() {
        return state != null && state.equalsIgnoreCase(ApiConstants.LOADING_MORE);
    }

    public class MovieItemViewHolder extends CustomViewHolder {
        private FragmentMovieItemBinding binding = null;

        private MovieItemViewHolder(@NonNull FragmentMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, binding.getModel().getTitle(), Toast.LENGTH_LONG).show();
                }
            });

            this.binding.isFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        binding.isFavourite.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favour));
                        Preferences.setFavourite(context, binding.getModel().getId(), true);
                    }
                    else {
                        binding.isFavourite.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.ic_not_favour));
                        Preferences.setFavourite(context, binding.getModel().getId(), false);
                    }
                }
            });
        }
    }

    public class ProgressItemViewHolder  extends CustomViewHolder {
        private ProgressItemBinding progressItemBinding = null;

        public ProgressItemViewHolder(@NonNull ProgressItemBinding progressItemBinding) {
            super(progressItemBinding.getRoot());
            this.progressItemBinding = progressItemBinding;
        }
    }


    public class CustomViewHolder  extends RecyclerView.ViewHolder {
        public CustomViewHolder(@NonNull View view) {
            super(view);
        }
    }

}