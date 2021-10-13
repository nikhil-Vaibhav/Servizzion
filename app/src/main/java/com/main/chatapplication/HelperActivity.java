package com.main.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;

import com.main.chatFragments.AccountUpdateFragment;
import com.main.chatFragments.DeliveredOrdersFragment;
import com.main.chatFragments.MyOrdersFragment;
import com.main.chatFragments.MyServicesFragment;
import com.main.chatFragments.OrderPendingFragment;
import com.main.chatFragments.ViewProfileFragment;
import com.main.chatFragments.ViewThisUserServicesFragment;
import com.main.chatModel.User;
import com.main.chatUtils.Store;

public class HelperActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        context = getApplicationContext();

        int task = getIntent().getIntExtra(Store.EXTRA_SELECTED_TASK , 0);
        User user = (User) getIntent().getSerializableExtra(Store.KEY_CHAT_WITH_USER);
        Fragment fragment = new MyOrdersFragment();
        String title = null;

        if(task == Store.TASK_MY_SERVICES) {
            fragment = new MyServicesFragment();
            title = "My Services";
        }
        else if(task == Store.TASK_MY_ORDERS) {
            fragment = new MyOrdersFragment();
            title = "My Orders";
        }
        else if(task == Store.TASK_DELIVERED_ORDERS) {
            fragment = new DeliveredOrdersFragment();
            title = "Orders Delivered";
        }
        else if(task == Store.TASK_ORDERS_PENDING) {
            fragment = new OrderPendingFragment();
            title = "Orders Pending";
        }
        else if(task == Store.TASK_ACCOUNT_UPDATE) {
            fragment = new AccountUpdateFragment();
            title = "Update Account";
        }
        else if(task == Store.TASK_THIS_USER_SERVICES) {
            fragment = new ViewThisUserServicesFragment(user);
            title = user.userName + "'s Services";
        }
        else if (task == R.id.item_view_profile || task == Store.TASK_VIEW_PROFILE) {
            fragment = new ViewProfileFragment(user);
            title = user.userName + "'s Profile";
        }

        setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_frame , fragment).commit();
    }
}