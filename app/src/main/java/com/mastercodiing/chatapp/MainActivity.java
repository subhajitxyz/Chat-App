package com.mastercodiing.chatapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mastercodiing.chatapp.utils.FirebaseUtil;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //profileFragment=new ProfileFragment();

        searchButton=findViewById(R.id.main_search_btn);
        bottomNavigationView= findViewById(R.id.bottom_navigation);


        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));

        });




        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.menu_chat){

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ChatFragment chatFragment=new ChatFragment();
                fragmentTransaction.replace(R.id.main_frame_layout, chatFragment);
                fragmentTransaction.commit();

            }
            if(item.getItemId()==R.id.menu_profile){

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfileFragment profileFragment=new ProfileFragment();
                fragmentTransaction.replace(R.id.main_frame_layout,profileFragment);
                fragmentTransaction.commit();
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();


    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token=task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken",token);
            }
        });
    }
}
