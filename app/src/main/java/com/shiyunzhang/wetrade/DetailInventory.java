package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.Inventory;


public class DetailInventory extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    ImageView itemImage;
    TextView itemName, itemDesc, itemCondition, itemPrice, itemCategory, itemQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventory);
        init();
        setUpActionBar();
        getItemInfo();

    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    public void init(){
        Intent intent = this.getIntent();
        String uid = intent.getStringExtra("UID");
        String documentId = intent.getStringExtra("ID");
        itemRef = db.collection("Inventory").document(uid).collection("Items").document(documentId);
        itemImage = findViewById(R.id.item_detail_image);
        itemName = findViewById(R.id.item_detail_name);
        itemDesc = findViewById(R.id.item_detail_description);
        itemCondition = findViewById(R.id.item_detail_condition);
        itemPrice = findViewById(R.id.item_detail_price);
        itemCategory = findViewById(R.id.item_detail_category);
        itemQuantity = findViewById(R.id.item_detail_quantity);
    }


    public void getItemInfo(){
        itemRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                Inventory item = documentSnapshot.toObject(Inventory.class);
                if(item.getImageUrl() != null) Glide.with(DetailInventory.this).load(item.getImageUrl()).into(itemImage);
                if(item.getName() != null) itemName.setText(item.getName());
                if(item.getDescription() != null) itemDesc.setText(item.getDescription());
                if(item.getCondition() != null) itemCondition.setText("Condition: " + item.getCondition());
                if(item.getCategory() != null) itemCategory.setText("Category: " + item.getCategory());
                if(item.getPrice() != 0) itemPrice.setText("$" + item.getPrice());
                if(item.getQuantity() != 0) itemQuantity.setText("Quantities: " + item.getQuantity());
            })
            .addOnFailureListener(e -> Toast.makeText(DetailInventory.this, e.toString(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
