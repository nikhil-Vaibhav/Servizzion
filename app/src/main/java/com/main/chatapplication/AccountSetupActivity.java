package com.main.chatapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatModel.User;
import com.main.chatUtils.AccountHandler;
import com.main.chatUtils.Store;
import com.main.chatUtils.Upload;

import java.util.HashMap;
import java.util.Map;

public class AccountSetupActivity extends AppCompatActivity {

    private AppCompatImageView profileImageView;
    private EditText userNameTv , userBioTv , userDescTv , userPhoneTv;
    private Button uploadImageBtn , setupBtn;
    private boolean imageUploaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        init();
        setUpListener();
    }

    private void init() {
        profileImageView = findViewById(R.id.setup_image_view);
        userNameTv = findViewById(R.id.setup_userName);
        userBioTv = findViewById(R.id.setup_userBio);
        userDescTv = findViewById(R.id.setup_userDesc);
        userPhoneTv = findViewById(R.id.setup_userPhone);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        setupBtn = findViewById(R.id.setup_accountBtn);
        Store.CURRENT_USER = new User();
        imageUploaded = false;
    }

    private void updateCurrentUser() {
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER)
                .document(Store.CURRENT_USER_ID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    new AccountHandler(AccountSetupActivity.this , null).loadCurrentUserData(documentSnapshot);
                });
    }

    private void setUpListener() {
        uploadImageBtn.setOnClickListener(v -> {
            uploadProfileImage();
        });
        setupBtn.setOnClickListener(view -> {
            saveUserDataAndSetupAccount();
        });
    }

    private void saveUserDataAndSetupAccount() {
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
                    toast("Setup Successful");
                })
                .addOnFailureListener(e -> {
                    toast("Some error occurred : " + e);
                });
        updateCurrentUser();
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
        if(!imageUploaded) {
            toast("Upload an Image");
            return false;
        }
        return true;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                    Glide.with(AccountSetupActivity.this)
                            .load(result.getData().getData())
                            .fitCenter()
                            .centerCrop()
                            .placeholder(R.drawable.buy_services)
                            .into(profileImageView);
                    Upload upload = new Upload( AccountSetupActivity.this);
                    upload.uploadProfileImage(result.getData().getData());
                }
            });
}