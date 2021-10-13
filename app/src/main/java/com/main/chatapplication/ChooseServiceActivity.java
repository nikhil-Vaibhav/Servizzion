package com.main.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.main.chatFragments.AboutFragment;
import com.main.chatFragments.DeliveryRequestFragment;
import com.main.chatFragments.MyProfileFragment;
import com.main.chatFragments.RecentConversationsFragment;
import com.main.chatUtils.Store;

public class ChooseServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment;
        int id = getIntent().getIntExtra(Store.KEY_INTENT_FRAGMENT , -1);

        String pageTitle = "";
        if(id == R.id.active_chat_option) {
            fragment = new RecentConversationsFragment();
            pageTitle = "Recent";
        } else if(id == R.id.my_profile_option) {
            fragment = new MyProfileFragment();
            pageTitle = "Account";
        } else if(id == R.id.about_app_option) {
            fragment = new AboutFragment();
            pageTitle = "About Servizzion";
        } else {
            fragment = new DeliveryRequestFragment();
            pageTitle = "Delivery Requests";
        }

        setTitle(pageTitle);
        getSupportFragmentManager().beginTransaction().replace(R.id.user_list_frame , fragment).commit();
    }
}