package com.shiyunzhang.wetrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.Auction;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserBidPrice;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AuctionActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference auctionRef;
    private Inventory inventory;
    private Auction auction;
    private CircleImageView hostImageView;
    private ImageView auctionItemImageView;
    private LinearLayout hostView, bidView;
    private TextView auctionItemName, auctionItemDesc, auctionItemCondition, auctionItemPrice, hostName, yourBidPrice;
    private EditText bidPrice;
    private double auctionStartPrice;
    private UserInfo userPreference;
    private RecyclerView mRecyclerView;
    private BidPriceAdapter adapter;
    private ArrayList<UserBidPrice> userBidPriceArrayList;

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
        setContentView(R.layout.activity_auction);
        setUpActionBar();
        init();
    }

    private void init() {
        getUserInfoFromPreference();
        Intent intent = getIntent();
        String auctionId = intent.getStringExtra("AUCTIONID");
        String userType = intent.getStringExtra("USERTYPE");
        if (userType.equals("Host")) {
            hostView = findViewById(R.id.auction_host_view);
            hostView.setVisibility(View.VISIBLE);
        } else {
            bidView = findViewById(R.id.bid_view);
            bidView.setVisibility(View.VISIBLE);
        }
        auctionRef = db.collection("Auction").document(auctionId);
        auctionItemImageView = findViewById(R.id.auction_image);
        auctionItemName = findViewById(R.id.auction_name);
        auctionItemDesc = findViewById(R.id.auction_desc);
        auctionItemCondition = findViewById(R.id.auction_condition);
        auctionItemPrice = findViewById(R.id.auction_price);
        hostImageView = findViewById(R.id.host_profile_image);
        hostName = findViewById(R.id.host_name);
        bidPrice = findViewById(R.id.bid_price);
        yourBidPrice = findViewById(R.id.your_bid_price);
        userBidPriceArrayList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.auction_price_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BidPriceAdapter(this, userBidPriceArrayList);
        mRecyclerView.setAdapter(adapter);
        getAuctionFromDatabase();
    }

    private void getUserInfoFromPreference(){
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userStr = sharedpreferences.getString("USER", "");
        Gson gson = new Gson();
        userPreference = gson.fromJson(userStr, UserInfo.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auctionRef.collection("BidPrice").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if(e != null) return;
                userBidPriceArrayList.clear();
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    UserBidPrice userBidPrice = queryDocumentSnapshot.toObject(UserBidPrice.class);
                    userBidPriceArrayList.add(userBidPrice);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void getAuctionFromDatabase() {
        auctionRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    auction = documentSnapshot.toObject(Auction.class);
                    inventory = auction.getInventory();
                    setTitle("Auction - " + inventory.getName());
                    Glide.with(AuctionActivity.this).load(inventory.getImageUrl()).into(auctionItemImageView);
                    auctionItemName.setText(inventory.getName());
                    auctionItemDesc.setText(inventory.getDescription());
                    auctionItemCondition.setText("Condition: " + inventory.getConditionAndQuantities().get(0).getCondition());
                    auctionStartPrice = auction.getPrice();
                    auctionItemPrice.setText("$ " + auctionStartPrice);
                    getHostInfo(inventory.getUserID());
                })
                .addOnFailureListener(e -> {

                });
    }

    private void getHostInfo(String uid) {
        db.collection("User").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                    if (userInfo.getImageUrl() != null) {
                        Glide.with(AuctionActivity.this).load(userInfo.getImageUrl()).into(hostImageView);
                    }
                    if (userInfo.getFirstName() != null && userInfo.getLastName() != null) {
                        hostName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    public void endAuction(View view) {
    }

    public void SubmitPrice(View view) {
        if (bidPrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Price", Toast.LENGTH_SHORT).show();
        } else {
            double price = Double.parseDouble(bidPrice.getText().toString());
            if (price < auctionStartPrice) {
                Toast.makeText(this, "Please enter price greater than the start price", Toast.LENGTH_SHORT).show();
            } else {
                yourBidPrice.setText("Your Bid Price : $ " + price);
                String name = userPreference.getFirstName() + " " + userPreference.getLastName();
                UserBidPrice userBidPrice = new UserBidPrice(userPreference.getId(), name, price, userPreference.getImageUrl());
                auctionRef.collection("BidPrice").document(userPreference.getId())
                    .set(userBidPrice)
                    .addOnSuccessListener(aVoid -> bidPrice.setText(""))
                    .addOnFailureListener(e -> {

                    });
            }
        }
    }
}
