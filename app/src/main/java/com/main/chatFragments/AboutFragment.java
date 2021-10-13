package com.main.chatFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.main.chatapplication.R;
import com.squareup.picasso.Picasso;

public class AboutFragment extends Fragment {

    AppCompatImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_screen_layout , container , false);
        imageView = view.findViewById(R.id.app_image);
        Picasso.get()
                .load(R.mipmap.ic_launcher_foreground)
                .fit()
                .centerCrop()
                .into(imageView);
        return view;
    }

}
