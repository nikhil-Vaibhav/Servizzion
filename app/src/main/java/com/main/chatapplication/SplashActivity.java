package com.main.chatapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.chatModel.User;
import com.main.chatUtils.AccountHandler;
import com.main.chatUtils.Store;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (Store.FIRST_VISIT)
                startActivity(new Intent(this, IntroActivity.class));
            else
                startActivity(new Intent(this, SignInActivity.class));
        } else {
            loadCurrentUserData();
        }
    }

    private void loadCurrentUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER)
                .document(user.getUid() + user.getEmail())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    new AccountHandler(SplashActivity.this , null).loadCurrentUserData(documentSnapshot);
                });

    }

}
