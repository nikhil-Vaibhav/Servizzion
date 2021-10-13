package com.main.chatFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.rpc.Help;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.ChatScreenActivity;
import com.main.chatapplication.HelperActivity;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jetbrains.annotations.NotNull;

/*
TODO: Lifecycle methods handling is required
 */

public class ViewProfileFragment extends Fragment {

    User active_user;

    private TextView userNameTv , userBioTv , userDescriptionTv , view_service ;
    private AppCompatImageView profileImageView;

    public ViewProfileFragment(User user) {
        this.active_user = user;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_profile_from_chat_layout , container , false);
        init(view);
        setListeners();
        return  view;
    }
    @Override
    public void onStart() {
        super.onStart();
        setData();
    }

    private void init(View view) {
        userNameTv = view.findViewById(R.id.profile_userName);
        userBioTv = view.findViewById(R.id.profile_userBio);
        userDescriptionTv = view.findViewById(R.id.profile_userDesc);
        profileImageView = view.findViewById(R.id.profile_userImage);
        view_service = view.findViewById(R.id.profile_service_view);
    }

    private void setListeners() {
        view_service.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity() , HelperActivity.class);
            intent.removeExtra(Store.EXTRA_SELECTED_TASK);
            intent.removeExtra(Store.KEY_CHAT_WITH_USER);
            intent.putExtra(Store.EXTRA_SELECTED_TASK , Store.TASK_THIS_USER_SERVICES);
            intent.putExtra(Store.KEY_CHAT_WITH_USER , active_user);
            startActivity(intent);
        });
    }
    private void setData() {
        userNameTv.setText(active_user.userName);
        userBioTv.setText(active_user.userBio);
        userDescriptionTv.setText(active_user.userDescription);

        Picasso.get()
                .load(active_user.profilePicturePath)
                .fit()
                .transform(new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build())
                .centerCrop()
                .into(profileImageView);

    }

}
