package com.main.chatAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatFragments.ImageFragment;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.sun.mail.util.LineInputStream;

import java.util.List;

public class ImagePagerAdapter extends FragmentStateAdapter {

    List<String> images;

    public ImagePagerAdapter(@NonNull Fragment fragment , List<String> imageList) {
        super(fragment);
        images = imageList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ImageFragment.getInstance(position , images);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
