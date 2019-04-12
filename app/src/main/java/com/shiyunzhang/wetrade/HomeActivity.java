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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_style);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        actionBar.hide();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_search:
                        fragment = new SearchFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_favorite:
                        fragment = new FavoriteFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_inventory:
                        fragment = new InventoryFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        return true;
                }

                return false;
            };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
