package com.shiyunzhang.wetrade;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DataClass.UserInfo;
import com.shiyunzhang.wetrade.fragment.FavoriteFragment;
import com.shiyunzhang.wetrade.fragment.HomeFragment;
import com.shiyunzhang.wetrade.fragment.InventoryFragment;
import com.shiyunzhang.wetrade.fragment.ProfileFragment;
import com.shiyunzhang.wetrade.fragment.SearchFragment;

public class HomeActivity extends AppCompatActivity {
    private String TAG = "HomeActivity";
    private ActionBar actionBar;
    private BottomNavigationView navigation;
    private SharedPreferences sharedpreferences;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");
    private CollectionReference shoppingCartCollection = db.collection("ShoppingCart");
    private TextView shoppingCartCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        setUpActionBar();
        setUpBottomNavigation();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        getUserData();
        loadFragment(new HomeFragment());
        shoppingCartCount = findViewById(R.id.text_count);

    }

    public void setUpActionBar(){
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void setUpBottomNavigation(){
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShoppingCartCount();
    }

    public void getUserData() {
        userRef.whereEqualTo("id", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        UserInfo userInfo = queryDocumentSnapshot.toObject(UserInfo.class);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        Gson gson = new Gson();
                        String user = gson.toJson(userInfo);
                        editor.putString("USER", user).apply();
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
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

    public void goToShoppingCart(View view) {
        startActivity(new Intent(HomeActivity.this, ShoppingCartActivity.class));
    }

    private void getShoppingCartCount() {
        shoppingCartCollection.whereEqualTo("customerId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
                        shoppingCartCount.setText("" + queryDocumentSnapshots.size());
                        shoppingCartCount.setVisibility(View.VISIBLE);
                    } else {
                        shoppingCartCount.setVisibility(View.INVISIBLE);
                    }
                });
    }

}
