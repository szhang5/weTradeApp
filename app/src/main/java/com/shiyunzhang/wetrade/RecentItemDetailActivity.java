package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.Inventory;

public class RecentItemDetailActivity extends AppCompatActivity {
    private String TAG = "RecentItemDetailActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    private ImageView itemImage;
    private TextView itemName, itemDesc, itemCondition, itemPrice, itemCategory, itemQuantity;

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
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
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
                })
                .addOnFailureListener(e -> Toast.makeText(RecentItemDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

}
