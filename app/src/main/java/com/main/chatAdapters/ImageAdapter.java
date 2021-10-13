package com.main.chatAdapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatModel.ActiveUser;
import com.main.chatUtils.Upload;
import com.main.chatapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    NewImageListener listener;
    Context context;
    static List<Uri> imageList;

    public ImageAdapter(NewImageListener listener, Context context, List<Uri> images) {
        this.listener = listener;
        this.context = context;
        imageList = images;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_image_item , parent , false);
        return new ViewHolder(view , listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageAdapter.ViewHolder holder, int position) {
        holder.setData(imageList.get(position) , context);
    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        AppCompatButton addImageBtn;
        AppCompatImageView newImageView;
        Button deleteThisImageBtn;

        public ViewHolder(@NonNull @NotNull View itemView , NewImageListener listener) {
            super(itemView);

            addImageBtn = itemView.findViewById(R.id.add_image_button);
            newImageView = itemView.findViewById(R.id.new_uploaded_image);
            deleteThisImageBtn = itemView.findViewById(R.id.deleteThisImageBtn);

            addImageBtn.setOnClickListener(v -> {
                listener.onNewImage();
            });
            deleteThisImageBtn.setOnClickListener(v -> {
                listener.onDeleteImage(getBindingAdapterPosition());
            });
        }

        private void setData(Uri uri , Context context) {
            if(uri == null) return;
            addImageBtn.setVisibility(View.GONE);
            newImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(uri)
                    .fitCenter()
                    .centerCrop()
                    .into(newImageView);

        }
    }

    public interface NewImageListener {
        void onNewImage();
        void onDeleteImage(int position);
    }
}
