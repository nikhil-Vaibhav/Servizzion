package com.main.chatFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.main.chatapplication.ViewServiceActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageFragment extends Fragment {

    public static final String COUNTER_VAR = "counter number";
    static Integer counter;
    static List<String> images;

    public ImageFragment() {
    }

    public static ImageFragment getInstance(Integer position , List<String> imageList) {
        images = imageList;
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(COUNTER_VAR , position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            counter = getArguments().getInt(COUNTER_VAR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_view , container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.single_image_item);
        setImage(imageView);
    }

    private void setImage(ImageView imageView ) {
        Picasso.get()
                .load(images.get(counter))
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
