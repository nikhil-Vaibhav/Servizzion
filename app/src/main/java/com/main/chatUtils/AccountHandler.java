package com.main.chatUtils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatModel.User;
import com.main.chatServices.MailSender;
import com.main.chatapplication.AccountSetupActivity;
import com.main.chatapplication.MainActivity;
import com.main.chatapplication.R;
import com.main.chatapplication.SignInActivity;

import java.util.Random;

public class AccountHandler {

    private String enteredEmail, enteredPassword;
    private final Context context;
    private String emailSubject, emailContent, recipientEmail, OTP;
    private Handler handler;
    private FirebaseAuth firebaseAuth;

    public AccountHandler(Context ctx , SignInActivity signInActivity) {
        this.context = ctx;
        handler = new Handler();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void init(String email, String pwd) {
        enteredEmail = email;
        enteredPassword = pwd;
    }

    /*
    TODO: Add some validations on email and password complexities
     */

    public void verify() {
        if (enteredEmail.trim().isEmpty()) {
            toast("Enter EmailID");
            return;
        }
        if (enteredPassword.trim().isEmpty()) {
            toast("Enter Password");
            return;
        }
        createOTP();
        setEmailCreds();
        sendMail.start();
    }

    private void setEmailCreds() {
        emailSubject = "Email Verification Required";
        emailContent = "To verify your email , please enter this OTP in the required field.\n\n" + OTP;
        recipientEmail = enteredEmail;
    }

    private void createOTP() {
        long otp = 0;
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            otp = otp * 10 + random.nextInt(9);
        }
        OTP = String.valueOf(otp);
    }

    public void validateOTP(String otpEntered) {
        if (otpEntered.equals(OTP)) {
            createAccountThread.start();
        } else {
            toast("Incorrect OTP");
        }
    }

    private void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void addUserToDatabase() {

        User user = new User();
        user.userEmail = enteredEmail;
        user.userPassword = enteredPassword;
        user.userID = FirebaseAuth.getInstance().getCurrentUser().getUid() + FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER)
                .document(user.userID)
                .set(user)
                .addOnSuccessListener(voids -> {
                    Store.CURRENT_USER_ID = user.userID;
                    context.startActivity(new Intent(context, AccountSetupActivity.class));
                });
    }

    public void signOut() {
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER)
                .document(Store.CURRENT_USER_ID)
                .update(Store.KEY_FCM_TOKEN, FieldValue.delete());

        FirebaseAuth.getInstance().signOut();
        Store.CURRENT_USER = new User();
        context.startActivity(new Intent(context, SignInActivity.class));
    }

    public void loadCurrentUserData(DocumentSnapshot documentSnapshot) {
        Store.CURRENT_USER_ID = documentSnapshot.getId();
        Store.CURRENT_USER.userID = Store.CURRENT_USER_ID;
        Store.CURRENT_USER.userName = (String) documentSnapshot.get(Store.KEY_USERNAME);
        Store.CURRENT_USER.userBio = (String) documentSnapshot.get(Store.KEY_USERBIO);
        Store.CURRENT_USER.phone_number = (String) documentSnapshot.get(Store.KEY_PHONE_NUMBER);
        Store.CURRENT_USER.userDescription = (String) documentSnapshot.get(Store.KEY_USER_DESCRIPTION);
        Store.CURRENT_USER.userEmail = (String) documentSnapshot.get(Store.KEY_EMAIL);
        Store.CURRENT_USER.userPassword = (String) documentSnapshot.get(Store.KEY_PASSWORD);
        Store.CURRENT_USER.profilePicturePath = String.valueOf(documentSnapshot.get(Store.KEY_USER_PROFILE_PICTURE_PATH));
        Store.CURRENT_USER_FOLDER_REFERENCE = Store.CURRENT_USER_ID;
        handler.post(() -> {
            toast("Sign in Successful");
            context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
        });
    }

    Thread sendMail = new Thread(() -> {
        MailSender sender = new MailSender(
                Store.OWNER_EMAIL, Store.OWNER_PASSWORD
        );
        boolean res = sender.sendMail(emailSubject,
                emailContent,
                Store.OWNER_EMAIL,
                recipientEmail);

        handler.post(() -> {
            if (res) {
                toast("OTP Sent Successfully");
            }
        });
    });

    Thread createAccountThread = new Thread(() -> {
        firebaseAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handler.post(() -> {
                            toast("Account created");
                        });
                        addUserToDatabase();
                    } else {
                        handler.post(() -> {
                            toast("Error" + task.getException());
                        });
                    }
                });
    });
}
