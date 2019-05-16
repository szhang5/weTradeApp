package com.shiyunzhang.wetrade.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.Adapter.BidPriceAdapter;
import com.shiyunzhang.wetrade.DataClass.Auction;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserBidPrice;
import com.shiyunzhang.wetrade.DataClass.UserFCMToken;
import com.shiyunzhang.wetrade.DataClass.UserInfo;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AuctionActivity extends AppCompatActivity {
    private final static String TAG = "AuctionActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference auctionRef;
    private CollectionReference inventoryCollection;
    private Inventory inventory;
    private Auction auction;
    private CircleImageView hostImageView;
    private ImageView auctionItemImageView;
    private TextView auctionItemName, auctionItemDesc, auctionItemCondition, auctionItemPrice, hostName, yourBidPrice, auctionStatus, auctionUpdatedPrice;
    private EditText bidPrice;
    private double auctionStartPrice;
    private UserInfo userPreference;
    private BidPriceAdapter adapter;
    private ArrayList<UserBidPrice> userBidPriceArrayList;
    private String selectedWinnerId;
    private int selectedPosition = -1;
    private Button endAuctionBtn, submitPriceBtn;

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
            LinearLayout hostView = findViewById(R.id.auction_host_view);
            hostView.setVisibility(View.VISIBLE);
        } else {
            LinearLayout bidView = findViewById(R.id.bid_view);
            bidView.setVisibility(View.VISIBLE);
        }
        auctionRef = db.collection("Auction").document(auctionId);
        inventoryCollection = db.collection("Inventory");
        auctionStatus = findViewById(R.id.auction_status);
        auctionItemImageView = findViewById(R.id.auction_image);
        auctionItemName = findViewById(R.id.auction_name);
        auctionItemDesc = findViewById(R.id.auction_desc);
        auctionItemCondition = findViewById(R.id.auction_condition);
        auctionItemPrice = findViewById(R.id.auction_price);
        auctionUpdatedPrice = findViewById(R.id.auction_updated_price);
        hostImageView = findViewById(R.id.host_profile_image);
        hostName = findViewById(R.id.host_name);
        bidPrice = findViewById(R.id.bid_price);
        endAuctionBtn = findViewById(R.id.end_auction_btn);
        submitPriceBtn = findViewById(R.id.submit_price_btn);
        yourBidPrice = findViewById(R.id.your_bid_price);
        userBidPriceArrayList = new ArrayList<>();
        RecyclerView mRecyclerView = findViewById(R.id.auction_price_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BidPriceAdapter(this, userBidPriceArrayList, v -> {
            int position = (int) v.getTag();
            adapter.setSelectedPosition(position);
            selectedPosition = position == selectedPosition ? -1 : position;
            selectedWinnerId = selectedPosition == -1 ? null : userBidPriceArrayList.get(position).getUserId();
        });
        mRecyclerView.setAdapter(adapter);
        getAuctionFromDatabase();
    }

    private void getUserInfoFromPreference() {
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userStr = sharedpreferences.getString("USER", "");
        Gson gson = new Gson();
        userPreference = gson.fromJson(userStr, UserInfo.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auctionRef.collection("BidPrice").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) return;
            userBidPriceArrayList.clear();
            if (!queryDocumentSnapshots.isEmpty()) {
                double maxPrice = Integer.MIN_VALUE;
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    UserBidPrice userBidPrice = queryDocumentSnapshot.toObject(UserBidPrice.class);
                    maxPrice = Math.max(maxPrice, userBidPrice.getPrice());
                    userBidPriceArrayList.add(userBidPrice);
                }
                auctionUpdatedPrice.setText("$ " + maxPrice);
                adapter.notifyDataSetChanged();
            }

        });

        auctionRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) return;
            Auction auctionChange = documentSnapshot.toObject(Auction.class);
            if (auctionChange.getWinnerId() != null && auctionChange.getWinnerId().equals(userPreference.getId())) {
                submitPriceBtn.setEnabled(false);
                bidPrice.setEnabled(false);
                setUpWinnerAlertDialog();
            } else if (auctionChange.getWinnerId() != null && auctionChange.getInventory().getUserID().equals(userPreference.getId())) {
                endAuctionBtn.setEnabled(false);
            } else if (auctionChange.getWinnerId() != null && !auctionChange.getWinnerId().equals(userPreference.getId())) {
                submitPriceBtn.setEnabled(false);
                bidPrice.setEnabled(false);
                setUpLoserAlertDialog();
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
                    if (auction.getAuctionStatus().equals("End")) {
                        auctionStatus.setText(auction.getAuctionStatus());
                        auctionStatus.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        auctionStatus.setText(auction.getAuctionStatus());
                        auctionStatus.setTextColor(getResources().getColor(R.color.green));
                    }
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
        if (selectedWinnerId == null) {
            Toast.makeText(this, "Please select a winner", Toast.LENGTH_SHORT).show();
            return;
        } else {
            deductInventoryFromAuctionHost();
            addUserTokenToAuctionDatabase();

        }

    }

    public void deductInventoryFromAuctionHost() {
        inventoryCollection.document(inventory.getItemID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Inventory item = documentSnapshot.toObject(Inventory.class);
                    ArrayList<ConditionAndQuantity> conditionAndQuantities = item.getConditionAndQuantities();
                    for (ConditionAndQuantity conditionAndQuantity : conditionAndQuantities) {
                        if (conditionAndQuantity.getCondition().equals(inventory.getConditionAndQuantities().get(0).getCondition())) {
                            conditionAndQuantity.setQuantity(conditionAndQuantity.getQuantity() - inventory.getConditionAndQuantities().get(0).getQuantity());
                            break;
                        }
                    }
                    item.setConditionAndQuantities(conditionAndQuantities);
                    inventoryCollection.document(inventory.getItemID()).set(item).addOnSuccessListener(documentSnapshot1 -> {
                        addInventoryToWinner();
                    });

                });
    }

    public void addInventoryToWinner(){
        inventoryCollection.whereEqualTo("userID", selectedWinnerId)
                .whereEqualTo("productID", inventory.getProductID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                            Inventory item = queryDocumentSnapshot.toObject(Inventory.class);
                            ArrayList<ConditionAndQuantity> conditionAndQuantities = item.getConditionAndQuantities();
                            for (ConditionAndQuantity conditionAndQuantity : conditionAndQuantities) {
                                if (conditionAndQuantity.getCondition().equals(inventory.getConditionAndQuantities().get(0).getCondition())) {
                                    conditionAndQuantity.setQuantity(conditionAndQuantity.getQuantity() + 1);
                                    break;
                                }
                            }
                            item.setConditionAndQuantities(conditionAndQuantities);
                            inventoryCollection.document(item.getItemID()).set(item).addOnSuccessListener(documentSnapshot1 -> {});
                        }
                    } else {
                        ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();
                        conditionAndQuantities.add(new ConditionAndQuantity(inventory.getConditionAndQuantities().get(0).getCondition(), 1));
                        Inventory item = new Inventory(inventoryCollection.document().getId(), inventory.getImageUrl(), inventory.getCategory(), inventory.getName(), inventory.getDescription(), conditionAndQuantities, selectedWinnerId, inventory.getProductID());
                        inventoryCollection.document(item.getItemID()).set(item).addOnSuccessListener(aVoid -> {});
                    }
                });
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

    private void setUpWinnerAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionActivity.this);
        builder.setTitle("Congratulation!");
        builder.setMessage("Item added to your inventory!");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void setUpLoserAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionActivity.this);
        builder.setTitle("Sorry, Auction End");
        builder.setMessage("Good luck next time!");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    public void addUserTokenToAuctionDatabase() {
        DocumentReference userTokenRef = db.collection("UserToken").document(selectedWinnerId);
        userTokenRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    UserFCMToken userFCMToken = documentSnapshot.toObject(UserFCMToken.class);
                    auction.setWinner(userFCMToken.getToken());
                    auction.setWinnerId(selectedWinnerId);
                    auction.setAuctionStatus("End");
                    auctionRef.set(auction, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    });

                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
