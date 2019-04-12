package com.shiyunzhang.wetrade;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;

import com.shiyunzhang.wetrade.fragment.FavoriteFragment;
import com.shiyunzhang.wetrade.fragment.HomeFragment;
import com.shiyunzhang.wetrade.fragment.InventoryFragment;
import com.shiyunzhang.wetrade.fragment.ProfileFragment;
import com.shiyunzhang.wetrade.fragment.SearchFragment;

public class HomeActivity extends AppCompatActivity {

    ActionBar actionBar;
    AppCompatTextView actionBarTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_style);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        actionBarTitle = findViewById(R.id.actionbar_title);
        actionBarTitle.setText(getString(R.string.app_name));
        loadFragment(new HomeFragment());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.show();
                    actionBarTitle.setText(getString(R.string.app_name));
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_search:
                    actionBar.show();
                    actionBarTitle.setText("Search");
                    fragment = new SearchFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_favorite:
                    actionBar.show();
                    actionBarTitle.setText("Favorite");
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_inventory:
                    actionBar.show();
                    actionBarTitle.setText("Inventory");
                    fragment = new InventoryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    actionBar.hide();
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
