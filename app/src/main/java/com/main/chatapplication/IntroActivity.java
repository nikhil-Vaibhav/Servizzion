package com.main.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.main.chatUtils.Store;
import com.squareup.picasso.Picasso;

public class IntroActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imageView = findViewById(R.id.app_image);
        Picasso.get()
                .load(R.mipmap.ic_launcher_foreground)
                .fit()
                .centerCrop()
                .into(imageView);

        Store.FIRST_VISIT = false;
        Button continueBtn = findViewById(R.id.continue_button);
        continueBtn.setOnClickListener(v -> {
            startActivity(new Intent(this , SignInActivity.class));
        });
    }
}