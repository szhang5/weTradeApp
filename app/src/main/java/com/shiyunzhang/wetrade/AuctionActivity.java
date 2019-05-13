package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.Auction;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;


public class AuctionActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference auctionRef;
    private Inventory inventory;
    private Auction auction;
    private CircleImageView hostImageView;
    private ImageView auctionItemImageView;
    private LinearLayout hostView, bidView;
    private TextView auctionItemName, auctionItemDesc, auctionItemCondition, auctionItemPrice, hostName;

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

    private void init(){
        Intent intent = getIntent();
        String auctionId = intent.getStringExtra("AUCTIONID");
        auctionRef = db.collection("Auction").document(auctionId);
        auctionItemImageView = findViewById(R.id.auction_image);
        auctionItemName = findViewById(R.id.auction_name);
        auctionItemDesc = findViewById(R.id.auction_desc);
        auctionItemCondition = findViewById(R.id.auction_condition);
        auctionItemPrice = findViewById(R.id.auction_price);
        hostImageView = findViewById(R.id.host_profile_image);
        hostName = findViewById(R.id.host_name);
        hostView = findViewById(R.id.auction_host_view);
        bidView = findViewById(R.id.bid_view);
        getAuctionFromDatabase();
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void getAuctionFromDatabase(){
        auctionRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                auction = documentSnapshot.toObject(Auction.class);
                inventory = auction.getInventory();
                setTitle("Auction - " + inventory.getName());
                Glide.with(AuctionActivity.this).load(inventory.getImageUrl()).into(auctionItemImageView);
                auctionItemName.setText(inventory.getName());
                auctionItemDesc.setText(inventory.getDescription());
                auctionItemCondition.setText("Condition: " + inventory.getConditionAndQuantities().get(0).getCondition());
                auctionItemPrice.setText("$ " + auction.getPrice());
                getHostInfo(inventory.getUserID());
            })
            .addOnFailureListener(e -> {

            });
    }

    private void getHostInfo(String uid){
        db.collection("User").document(uid)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                if(userInfo.getImageUrl()!= null) {
                    Glide.with(AuctionActivity.this).load(userInfo.getImageUrl()).into(hostImageView);
                }
                if(userInfo.getFirstName() != null && userInfo.getLastName() != null){
                    hostName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                }
            })
            .addOnFailureListener(e -> {

            });
    }

    public void endAuction(View view) {
    }
}
