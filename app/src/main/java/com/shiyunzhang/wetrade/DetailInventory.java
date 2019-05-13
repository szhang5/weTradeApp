package com.shiyunzhang.wetrade;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;

import java.util.ArrayList;


public class DetailInventory extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    private ImageView itemImage;
    private TextView itemName, itemDesc, itemCategory, itemQuantity;
    private String name, desc, category, imageUrl;
    private int quantity;
    private ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventory);
        init();
        setUpButtonListener();
        setUpActionBar();
        getItemInfo();

    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    public void init(){
        Intent intent = this.getIntent();
        String documentId = intent.getStringExtra("ID");
        itemRef = db.collection("Inventory").document(documentId);
        itemImage = findViewById(R.id.item_detail_image);
        itemName = findViewById(R.id.item_detail_name);
        itemDesc = findViewById(R.id.item_detail_description);
        itemCategory = findViewById(R.id.item_detail_category);
        itemQuantity = findViewById(R.id.item_detail_quantity);
    }

    public void setUpButtonListener(){
        findViewById(R.id.delete_item).setOnClickListener(v -> setUpAlertDialog());
    }

    private void setUpAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInventory.this);
        builder.setMessage("Delete item - " + name);
        builder.setTitle("Please Confirm?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            deleteItem();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteItem(){
        itemRef.delete()
            .addOnSuccessListener(aVoid -> finish());
    }

    public void getItemInfo(){
        itemRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                Inventory item = documentSnapshot.toObject(Inventory.class);
                if(item.getImageUrl() != null){
                    imageUrl = item.getImageUrl();
                    Glide.with(DetailInventory.this).load(imageUrl).into(itemImage);
                }
                if(item.getName() != null){
                    setTitle(item.getName());
                    name = item.getName();
                    itemName.setText(name);
                }
                if(item.getDescription() != null){
                    desc = item.getDescription();
                    itemDesc.setText(desc);
                }
                if(item.getConditionAndQuantities() != null) {
                    quantity = 0;
                    conditionAndQuantities = item.getConditionAndQuantities();
                    for(ConditionAndQuantity conditionAndQuantity : conditionAndQuantities){
                        quantity += conditionAndQuantity.getQuantity();
                    }
                    if (quantity == 0) {
                        itemQuantity.setText("Out of Stock");
                    } else {
                        itemQuantity.setText("Total quantities: " + quantity);
                    }
                    RecyclerView conditionQuantityView = findViewById(R.id.condition_quantity_recycler_view);
                    conditionQuantityView.setHasFixedSize(true);
                    conditionQuantityView.setLayoutManager(new LinearLayoutManager(this));
                    ConditionAndQuantityAdapter adapter = new ConditionAndQuantityAdapter(this, conditionAndQuantities);
                    adapter.setInventory(item);
                    conditionQuantityView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                if(item.getCategory() != null) {
                    category = item.getCategory();
                    itemCategory.setText("Category: " + category);
                }
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

    public void createAuction(View view) {
//        Intent intent = new Intent(this, AuctionActivity.class);
//        startActivity(intent);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.start_auction_popup, null);

        mBuilder.setTitle("Please add a base price for this auction: ");
        mBuilder.setPositiveButton("Start Auction", (dialog, which) -> {
            dialog.dismiss();
        });
        mBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
}
