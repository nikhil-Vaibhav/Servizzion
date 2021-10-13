package com.main.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatUtils.AccountHandler;
import com.main.chatUtils.Store;

import java.util.Objects;


public class SignInActivity extends AppCompatActivity  {

    public static final String TAG = "Sign IN";

    FirebaseAuth firebaseAuth;
    Handler handler;
    public TextView page_title;
    private Button create_account, sign_in_btn, createReq , signInReq;
    private TextInputEditText otpField, emailInput, passwordInput;
    public ProgressBar progressBar;
    private AccountHandler accountHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        setListeners();
    }

    private void init() {
        handler = new Handler();
        accountHandler = new AccountHandler(SignInActivity.this , new SignInActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        page_title = findViewById(R.id.signInTitle);
        create_account = findViewById(R.id.createAccountBtn);
        createReq = findViewById(R.id.createRequestBtn);
        signInReq = findViewById(R.id.signInRequestBtn);
        sign_in_btn = findViewById(R.id.signInBtn);
        otpField = findViewById(R.id.phone_number_new_account);
        emailInput = findViewById(R.id.user_email_input);
        passwordInput = findViewById(R.id.user_password_input);
        progressBar = findViewById(R.id.account_progress_bar);
    }

    private void setListeners() {


        create_account.setOnClickListener(v -> {
            if(create_account.getText().toString().equals(getString(R.string.verify_email))) {
                if (!isValidInput()) return;
                create_account.setText(getString(R.string.sending_otp));
                accountHandler.init(emailInput.getText().toString(), passwordInput.getText().toString());
                accountHandler.verify();
                create_account.setText(getString(R.string.create_account));
            } else {
                if(otpField.getText() == null) {
                    toast("Enter OTP");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                accountHandler.validateOTP(Objects.requireNonNull(otpField.getText()).toString());
            }
        });

        sign_in_btn.setOnClickListener(v -> {
            signIn();
        });

        signInReq.setOnClickListener(v -> {
            page_title.setText(R.string.sign_in);
            otpField.setVisibility(View.GONE);
            sign_in_btn.setVisibility(View.VISIBLE);
            createReq.setVisibility(View.VISIBLE);
            signInReq.setVisibility(View.GONE);
            create_account.setVisibility(View.GONE);
            findViewById(R.id.otp_ip_layout).setVisibility(View.GONE);
        });

        createReq.setOnClickListener(v -> {
            page_title.setText(R.string.create_account);
            otpField.setVisibility(View.VISIBLE);
            sign_in_btn.setVisibility(View.GONE);
            createReq.setVisibility(View.GONE);
            signInReq.setVisibility(View.VISIBLE);
            create_account.setVisibility(View.VISIBLE);
            findViewById(R.id.otp_ip_layout).setVisibility(View.VISIBLE);
        });
    }

    private boolean isValidInput() {
        if (Objects.requireNonNull(emailInput.getText()).toString().trim().isEmpty()) {
            toast("Enter EmailID");
            return false;
        }
        if (Objects.requireNonNull(passwordInput.getText()).toString().trim().isEmpty()) {
            toast("Enter Password");
            return false;
        }

        return true;
    }

    private void signIn() {
        if (!isValidInput()) return;

        progressBar.setVisibility(View.VISIBLE);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        signInWithEmailPassword(email , password);
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signInWithEmailPassword(String enteredEmail , String enteredPassword) {
        firebaseAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        String userId = user.getUid() + user.getEmail();

                        // [Getting User data from Database]
                        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER)
                                .document(userId)
                                .get()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful() && t.getResult() != null) {
                                        accountHandler.loadCurrentUserData(t.getResult());
                                    }
                                });
                    } else if (task.getException() != null) {
                        handler.post(() -> {
                            toast(task.getException().getMessage());
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                });
    }
}