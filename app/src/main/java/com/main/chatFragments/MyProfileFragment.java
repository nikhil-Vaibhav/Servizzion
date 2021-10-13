package com.main.chatFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatModel.ProfileItem;
import com.main.chatModel.User;
import com.main.chatUtils.AccountHandler;
import com.main.chatUtils.Store;
import com.main.chatUtils.Upload;
import com.main.chatapplication.MainActivity;
import com.main.chatapplication.R;
import com.main.chatapplication.HelperActivity;
import com.main.chatapplication.UserSectionActivity;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MyProfileFragment extends Fragment  {

    ImageView profile_image_view;
    TextView profileUserName, profileUserBio ;
    Button actionBtn1 , actionBtn2 , actionBtn3 , actionBtn4 , actionBtn5 , actionBtn6 , actionBtn7 , actionBtn8 , actionBtn9;
    ArrayList<ProfileItem> serviceList;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile_layout, container, false);
        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserData();
        setListeners();
    }

    private void setUserData() {

        Picasso.get()
                .load(Store.CURRENT_USER.profilePicturePath)
                .fit()
                .transform(new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .cornerRadiusDp(40)
                        .oval(false)
                        .build())
                .centerCrop()
                .into(profile_image_view);

        profileUserName.setText(Store.CURRENT_USER.userName);
        profileUserBio.setText(Store.CURRENT_USER.userBio);
    }

    private void init(View view) {

        profile_image_view = view.findViewById(R.id.my_profile_image);
        profileUserName = view.findViewById(R.id.my_profile_userName);
        profileUserBio = view.findViewById(R.id.my_profile_userBio);
        actionBtn1 = view.findViewById(R.id.action_button1);
        actionBtn2 = view.findViewById(R.id.action_button2);
        actionBtn3 = view.findViewById(R.id.action_button3);
        actionBtn4 = view.findViewById(R.id.action_button4);
        actionBtn5 = view.findViewById(R.id.action_button5);
        actionBtn6 = view.findViewById(R.id.action_button6);
        actionBtn7 = view.findViewById(R.id.action_button7);
        actionBtn8 = view.findViewById(R.id.action_button8);
        actionBtn9 = view.findViewById(R.id.action_button9);
        serviceList = new ArrayList<>();
    }

    private void setListeners() {

        actionBtn1.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HelperActivity.class);
            intent.removeExtra(Store.EXTRA_SELECTED_TASK);
            intent.removeExtra(Store.KEY_CHAT_WITH_USER);
            intent.putExtra(Store.EXTRA_SELECTED_TASK, Store.TASK_MY_SERVICES);
            intent.putExtra(Store.KEY_CHAT_WITH_USER, Store.CURRENT_USER);
            startActivity(intent);
        });
        actionBtn2.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HelperActivity.class);
            intent.removeExtra(Store.EXTRA_SELECTED_TASK);
            intent.removeExtra(Store.KEY_CHAT_WITH_USER);
            intent.putExtra(Store.EXTRA_SELECTED_TASK, Store.TASK_MY_ORDERS);
            intent.putExtra(Store.KEY_CHAT_WITH_USER, Store.CURRENT_USER);
            startActivity(intent);
        });
        actionBtn3.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HelperActivity.class);
            intent.removeExtra(Store.EXTRA_SELECTED_TASK);
            intent.removeExtra(Store.KEY_CHAT_WITH_USER);
            intent.putExtra(Store.EXTRA_SELECTED_TASK, Store.TASK_ORDERS_PENDING);
            intent.putExtra(Store.KEY_CHAT_WITH_USER, Store.CURRENT_USER);
            startActivity(intent);
        });
        actionBtn4.setOnClickListener(v -> {
            AccountHandler accountHandler = new AccountHandler(requireActivity() , null);
            accountHandler.signOut();
        });
        actionBtn5.setOnClickListener(v -> {
            /*
            TODO: Share function
             */
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT , "Servizzion");
            intent.putExtra(Intent.EXTRA_TEXT , "I am using Servizzion. This is a cool platform where you can connect with sellers for your business needs.");
            startActivity(Intent.createChooser(intent , "Share with"));
        });
        actionBtn6.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HelperActivity.class);
            intent.removeExtra(Store.EXTRA_SELECTED_TASK);
            intent.removeExtra(Store.KEY_CHAT_WITH_USER);
            intent.putExtra(Store.EXTRA_SELECTED_TASK, Store.TASK_DELIVERED_ORDERS);
            intent.putExtra(Store.KEY_CHAT_WITH_USER, Store.CURRENT_USER);
            startActivity(intent);
        });
        actionBtn7.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HelperActivity.class);
            intent.removeExtra(Store.EXTRA_SELECTED_TASK);
            intent.putExtra(Store.EXTRA_SELECTED_TASK, Store.TASK_ACCOUNT_UPDATE);
            startActivity(intent);
        });
        actionBtn8.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UserSectionActivity.class);
            requireActivity().getSharedPreferences(Store.KEY_SHARED_PREFS , MODE_PRIVATE).edit().putString(Store.REMOTE_MESSAGE_TYPE , "suggestion").apply();
            startActivity(intent);
        });
        actionBtn9.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UserSectionActivity.class);
            requireActivity().getSharedPreferences(Store.KEY_SHARED_PREFS , MODE_PRIVATE).edit().putString(Store.REMOTE_MESSAGE_TYPE , "help").apply();
            startActivity(intent);
        });

    }
}
