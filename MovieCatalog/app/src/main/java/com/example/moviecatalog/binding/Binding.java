package com.example.moviecatalog.binding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviecatalog.api.ApiConstants;

public class Binding {
    @BindingAdapter({"poster"})
    public static void LoadPoster(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .asBitmap()
                .load(ApiConstants.IMAGES_PATH + imageUrl)
                .apply(new RequestOptions().fitCenter())
                .into(view);
    }
}