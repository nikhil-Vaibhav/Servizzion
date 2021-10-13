package com.main.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatUtils.Store;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserSectionActivity extends AppCompatActivity {

    TextInputEditText editText;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_section);

        editText = findViewById(R.id.input_user_message);
        submitBtn = findViewById(R.id.submit_feedback_button);

        submitBtn.setOnClickListener(v -> saveFeedback());
    }

    private void saveFeedback() {

        String type = getSharedPreferences(Store.KEY_SHARED_PREFS , MODE_PRIVATE).getString(Store.REMOTE_MESSAGE_TYPE , "");
        String msg = Objects.requireNonNull(editText.getText()).toString();

        Map<String , String> message = new HashMap<>();
        message.put(Store.KEY_MESSAGE , msg);
        if(type.equals("help")) {
            FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_HELP).add(message);
        } else {
            FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_SUGGESTIONS).add(message);
        }
    }
}