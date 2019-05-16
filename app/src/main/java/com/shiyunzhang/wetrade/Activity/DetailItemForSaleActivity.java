package com.shiyunzhang.wetrade.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.Adapter.ConditionWithPriceAdapter;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.ItemForSale;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

public class DetailItemForSaleActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemforSaleRef;
    private ImageView itemSaleImage;
    private TextView itemSaleName, itemSaleDesc, itemSaleCategory, itemSaleQuantity;
    private String name, desc, category, imageUrl;
    private ArrayList<ConditionAndQuantity> conditionAndQuantities;
    private int quantity;

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
        setContentView(R.layout.activity_detail_item_for_sale);
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
        String documentId = intent.getStringExtra("ID");
        itemforSaleRef = db.collection("ItemForSale").document(documentId);
        itemSaleImage = findViewById(R.id.item_for_sale_detail_image);
        itemSaleName = findViewById(R.id.item_for_sale_detail_name);
        itemSaleDesc = findViewById(R.id.item_for_sale_detail_description);
        itemSaleCategory = findViewById(R.id.item_for_sale_detail_category);
        itemSaleQuantity = findViewById(R.id.item_for_sale_detail_quantity);

    }

    public void getItemInfo(){
        itemforSaleRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    ItemForSale item = documentSnapshot.toObject(ItemForSale.class);
                    if(item.getImageUrl() != null){
                        imageUrl = item.getImageUrl();
                        Glide.with(DetailItemForSaleActivity.this).load(imageUrl).into(itemSaleImage);
                    }
                    if(item.getName() != null){
                        setTitle(item.getName());
                        name = item.getName();
                        itemSaleName.setText(name);
                    }
                    if(item.getDescription() != null){
                        desc = item.getDescription();
                        itemSaleDesc.setText(desc);
                    }
                    if(item.getConditionAndQuantities() != null) {
                        quantity = 0;
                        conditionAndQuantities = item.getConditionAndQuantities();
                        for(ConditionAndQuantity conditionAndQuantity : conditionAndQuantities){
                            quantity += conditionAndQuantity.getQuantity();
                        }
                        if (quantity == 0) {
                            itemSaleQuantity.setText("Out of Stock");
                        } else {
                            itemSaleQuantity.setText("Total quantities: " + quantity);
                        }
                        RecyclerView conditionQuantityView = findViewById(R.id.condition_quantity_price_recycler_view);
                        conditionQuantityView.setHasFixedSize(true);
                        conditionQuantityView.setLayoutManager(new LinearLayoutManager(this));
                        ConditionWithPriceAdapter adapter = new ConditionWithPriceAdapter(this, conditionAndQuantities);
                        conditionQuantityView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    if(item.getCategory() != null) {
                        category = item.getCategory();
                        itemSaleCategory.setText("Category: " + category);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(DetailItemForSaleActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void setUpAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailItemForSaleActivity.this);
        builder.setMessage("Delete item - " + name);
        builder.setTitle("Please Confirm?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            itemforSaleRef.delete();
            finish();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public void deleteItemForSale(View view) {
        setUpAlertDialog();
    }
}
