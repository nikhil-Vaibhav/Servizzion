package com.main.chatFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.main.chatAdapters.ImagePagerAdapter;
import com.main.chatapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderFragment extends Fragment {


    ViewPager2 imageSlider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment_layout , container , false);

        List<String> images = new ArrayList<>();
        if(getArguments() != null)
            images = getArguments().getStringArrayList("imagelist");
        imageSlider = view.findViewById(R.id.image_slider_pager);
        imageSlider.setAdapter(new ImagePagerAdapter(this , images ));

        return view;
    }

}
