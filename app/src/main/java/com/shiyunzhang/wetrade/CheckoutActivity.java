package com.shiyunzhang.wetrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private final static String TAG = "CheckoutActivity";
    private TextView shippingAddress, itemPrice, totalBeforeTax, taxCollect, totalPrice;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shoppingCartCollection = db.collection("ShoppingCart");
    private ArrayList<Transaction> shoppingCartList;
    private RecyclerView mRecyclerView;
    private CheckoutItemAdapter adapter;
    private UserInfo userInfo;
    private double amount, taxAmount, afterTaxTotal;

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
        setContentView(R.layout.activity_checkout);
        setUpActionBar();
        init();
    }

    private void setUpActionBar(){
        setTitle("Checkout");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void getUserInfoFromPreference(){
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userStr = sharedpreferences.getString("USER", "");
        Gson gson = new Gson();
        userInfo = gson.fromJson(userStr, UserInfo.class);

    }

    private void init(){
        getUserInfoFromPreference();
        shoppingCartList = new ArrayList<>();
        shippingAddress = findViewById(R.id.shipping_address);
        shippingAddress.setText("Shipping Address: \n" + userInfo.getAddress() + ", " + userInfo.getCity()
                + ", " + userInfo.getState() + ", " + userInfo.getZipCode());
        itemPrice = findViewById(R.id.item_price);
        totalBeforeTax = findViewById(R.id.total_before_tax);
        taxCollect = findViewById(R.id.tax_collect);
        totalPrice = findViewById(R.id.total_price);
        mRecyclerView = findViewById(R.id.checkout_item_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckoutItemAdapter(this, shoppingCartList);
        mRecyclerView.setAdapter(adapter);
        getShoppingCartList();
    }

    private void getShoppingCartList(){
        amount = 0;
        shoppingCartCollection.whereEqualTo("customerId", userInfo.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Transaction transaction = queryDocumentSnapshot.toObject(Transaction.class);
                        amount += transaction.getPrice()*transaction.getQuantity();
                        shoppingCartList.add(transaction);
                        adapter.notifyDataSetChanged();
                    }
                    taxAmount = 0.0875 * amount;
                    afterTaxTotal = amount + taxAmount;
                    itemPrice.setText("$ " + String.format("%.2f", amount));
                    totalBeforeTax.setText("$ " + String.format("%.2f", amount));
                    taxCollect.setText("$ " + String.format("%.2f", taxAmount));
                    totalPrice.setText("$ " + String.format("%.2f", afterTaxTotal));

                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
