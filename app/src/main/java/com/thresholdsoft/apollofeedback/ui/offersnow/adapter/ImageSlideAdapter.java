package com.thresholdsoft.apollofeedback.ui.offersnow.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.databinding.LayoutImageSliderBinding;
import com.thresholdsoft.apollofeedback.utils.AppConstants;

import java.util.List;

public class ImageSlideAdapter extends RecyclerView.Adapter<ImageSlideAdapter.ViewHolder> {
    private Context context;
    private List<String> images;

    public ImageSlideAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutImageSliderBinding imageSliderBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_image_slider, parent, false);
        return new ViewHolder(imageSliderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + images.get(position))).into(holder.imageSliderBinding.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutImageSliderBinding imageSliderBinding;

        public ViewHolder(@NonNull LayoutImageSliderBinding imageSliderBinding) {
            super(imageSliderBinding.getRoot());
            this.imageSliderBinding = imageSliderBinding;
        }
    }
}
