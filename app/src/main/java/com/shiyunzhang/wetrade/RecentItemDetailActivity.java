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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentItemDetailActivity extends AppCompatActivity {
    private String TAG = "RecentItemDetailActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    private DocumentReference userRef;
    private ImageView itemImage;
    private String uid;
    private TextView itemName, itemDesc, itemCondition, itemPrice, itemCategory, itemQuantity, userName;
    private CircleImageView userImage;
    private RelativeLayout sellerInfo;

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
        setContentView(R.layout.activity_recent_item_detail_inventory);
        init();
        setUpActionBar();
        setUpClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getItemInfo();
    }

    private void init(){
        Intent intent = this.getIntent();
        String documentId = intent.getStringExtra("ID");
        itemRef = db.collection("Inventory").document(documentId);
        itemImage = findViewById(R.id.recent_item_detail_image);
        itemName = findViewById(R.id.recent_detail_name);
        itemCategory = findViewById(R.id.recent_detail_category);
        itemCondition = findViewById(R.id.recent_detail_condition);
        itemDesc = findViewById(R.id.recent_detail_description);
        itemQuantity = findViewById(R.id.recent_detail_quantity);
        itemPrice = findViewById(R.id.recent_detail_price);
        userImage = findViewById(R.id.user_profile_image);
        userName = findViewById(R.id.user_name);
        sellerInfo = findViewById(R.id.seller_relative_layout);
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void setUpClickListener(){
        sellerInfo.setOnClickListener(v -> {
            Intent intent = new Intent(RecentItemDetailActivity.this, SellerInfoActivity.class);
            intent.putExtra("UID", uid);
            startActivity(intent);
        });
    }

    public void getItemInfo(){
        itemRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    Inventory item = documentSnapshot.toObject(Inventory.class);
                    if(item.getImageUrl() != null){
                        Glide.with(RecentItemDetailActivity.this).load(item.getImageUrl()).into(itemImage);
                    }
                    if(item.getName() != null){
                        setTitle(item.getName());
                        itemName.setText(item.getName());
                    }
                    if(item.getDescription() != null){
                        itemDesc.setText(item.getDescription());
                    }
                    if(item.getCondition() != null) {
                        itemCondition.setText("Condition: " + item.getCondition());
                    }
                    if(item.getCategory() != null) {
                        itemCategory.setText("Category: " + item.getCategory());
                    }
                    if(item.getPrice() != 0) {
                        itemPrice.setText("$" + item.getPrice());
                    }
                    if(item.getQuantity() != 0) {
                        itemQuantity.setText("Quantities: " + item.getQuantity());
                    }
                    if(item.getUserID() != null) {
                        uid = item.getUserID();
                        userRef = db.collection("User").document(uid);
                    }
                    getUserInfo();
                })
                .addOnFailureListener(e -> Toast.makeText(RecentItemDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    public void getUserInfo(){
        userRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                if(userInfo.getImageUrl() != null) {
                    Glide.with(RecentItemDetailActivity.this).load(userInfo.getImageUrl()).into(userImage);
                }
                if(userInfo.getFirstName() != null && userInfo.getLastName() != null) {
                    userName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                }
            })
            .addOnFailureListener(e -> Toast.makeText(RecentItemDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

}
