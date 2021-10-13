package com.main.chatFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatUtils.Store;
import com.main.chatUtils.Upload;
import com.main.chatapplication.AccountSetupActivity;
import com.main.chatapplication.MainActivity;
import com.main.chatapplication.R;

import java.util.HashMap;
import java.util.Map;

public class AccountUpdateFragment extends Fragment {

    private AppCompatImageView profileImageView;
    private EditText userNameTv , userBioTv , userDescTv , userPhoneTv;
    private Button uploadImageBtn , updateBtn;
    private boolean imageUploaded;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_account_setup , container , false);

        init(view);
        setUpListener();
        loadData();
        return view;
    }

    private void loadData() {
        userNameTv.setText(Store.CURRENT_USER.userName);
        userBioTv.setText(Store.CURRENT_USER.userBio);
        userDescTv.setText(Store.CURRENT_USER.userDescription);
        userPhoneTv.setText(Store.CURRENT_USER.phone_number);
    }

    private void init(View view) {
        profileImageView = view.findViewById(R.id.setup_image_view);
        userNameTv = view.findViewById(R.id.setup_userName);
        userBioTv = view.findViewById(R.id.setup_userBio);
        userDescTv = view.findViewById(R.id.setup_userDesc);
        userPhoneTv = view.findViewById(R.id.setup_userPhone);
        uploadImageBtn = view.findViewById(R.id.uploadImageBtn);
        updateBtn = view.findViewById(R.id.setup_accountBtn);
        imageUploaded = false;
    }


    private void setUpListener() {
        uploadImageBtn.setOnClickListener(v -> {
            uploadProfileImage();
        });
        updateBtn.setOnClickListener(view -> {
            saveUserDataAndUpdateAccount();
        });
    }

    private void saveUserDataAndUpdateAccount() {
        if(!validInput()) return;
        Map<String , Object> data = new HashMap<>();
        data.put(Store.KEY_USERNAME , userNameTv.getText().toString());
        data.put(Store.KEY_USERBIO , userBioTv.getText().toString());
        data.put(Store.KEY_USER_DESCRIPTION , userDescTv.getText().toString());
        data.put(Store.KEY_PHONE_NUMBER , userPhoneTv.getText().toString());

        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER)
                .document(Store.CURRENT_USER_ID)
                .update(data)
                .addOnSuccessListener(unused -> {
                    toast("Updated");
                })
                .addOnFailureListener(e -> {
                    toast("Some error occurred : " + e);
                });
        updateCurrentUser();
        getParentFragmentManager().popBackStackImmediate(null , FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void updateCurrentUser() {
        Store.CURRENT_USER.userName = userNameTv.getText().toString();
        Store.CURRENT_USER.userBio = userBioTv.getText().toString();
        Store.CURRENT_USER.userDescription = userDescTv.getText().toString();
        Store.CURRENT_USER.phone_number = userPhoneTv.getText().toString();
    }

    boolean validInput() {
        if(userNameTv.getText().toString().trim().equals("")) {
            toast("Enter User Name");
            return false;
        }
        if(userDescTv.getText().toString().trim().equals("")) {
            toast("Enter some description");
            return false;
        }
        if(userBioTv.getText().toString().trim().equals("")) {
            userBioTv.setText(R.string.default_bio);
            return true;
        }
        return true;
    }

    private void toast(String message) {
        Toast.makeText(requireActivity() , message, Toast.LENGTH_SHORT).show();
    }
    private void uploadProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        profileImageLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> profileImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    imageUploaded = true;
                    Glide.with(this)
                            .load(result.getData().getData())
                            .fitCenter()
                            .circleCrop()
                            .placeholder(R.drawable.buy_services)
                            .into(profileImageView);
                    Upload upload = new Upload( requireActivity());
                    upload.uploadProfileImage(result.getData().getData());
                }
            });
}
