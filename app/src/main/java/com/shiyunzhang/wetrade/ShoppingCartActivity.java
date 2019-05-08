package com.shiyunzhang.wetrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {
    private final static String TAG = "ShoppingCartActivity";
    private RecyclerView mRecyclerView;
    private ShoppingCartItemAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shoppingCartCollection = db.collection("ShoppingCart");
    private UserInfo userInfo;
    private ArrayList<Transaction> shoppingCartList;
    private ProgressBar progressBar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        setUpActionBar();
        init();
    }

    private void setUpActionBar(){
        setTitle("Shopping Cart");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void init(){
        getUserInfoFromPreference();
        progressBar = findViewById(R.id.shopping_cart_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.shopping_cart_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayout.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        shoppingCartList = new ArrayList<>();
        adapter = new ShoppingCartItemAdapter(this, shoppingCartList);
        mRecyclerView.setAdapter(adapter);
        getShoppingCartItem();
    }

    private void getUserInfoFromPreference(){
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userStr = sharedpreferences.getString("USER", "");
        Gson gson = new Gson();
        userInfo = gson.fromJson(userStr, UserInfo.class);

    }


    private void getShoppingCartItem(){
        shoppingCartCollection.whereEqualTo("customerId", userInfo.getId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Transaction transaction = queryDocumentSnapshot.toObject(Transaction.class);
                    shoppingCartList.add(transaction);
                    adapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    public void checkout(View view) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }
}
