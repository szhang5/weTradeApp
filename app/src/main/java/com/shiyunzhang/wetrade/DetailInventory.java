package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.shiyunzhang.wetrade.DataClass.Inventory;


public class DetailInventory extends AppCompatActivity {
    private String TAG = "DetailInventory";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    private ImageView itemImage, itemImageEdit;
    private TextView itemName, itemDesc, itemCondition, itemPrice, itemCategory, itemQuantity;
    private EditText itemNameEdit, itemDescEdit, itemPriceEdit, itemCategoryEdit, itemQuantityEdit;
    private AppCompatSpinner itemConditionEdit;
    private LinearLayout itemDisplay, itemEdit, buttonGroup;
    private String name, desc, condition, category, imageUrl;
    private double price;
    private int quantity;
    private Button editButton;
    private ArrayAdapter<String> conditionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventory);
        init();
        setUpButtonListener();
        setUpConditionSpinner();
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
        itemDisplay = findViewById(R.id.item_display_linear_layout);
        itemImageEdit = findViewById(R.id.item_detail_image_edit);
        itemEdit = findViewById(R.id.item_edit_linear_layout);
        itemNameEdit = findViewById(R.id.item_detail_name_edit);
        itemDescEdit = findViewById(R.id.item_detail_description_edit);
        itemConditionEdit = findViewById(R.id.item_detail_condition_edit);
        itemPriceEdit = findViewById(R.id.item_detail_price_edit);
        itemCategoryEdit = findViewById(R.id.item_detail_category_edit);
        itemQuantityEdit = findViewById(R.id.item_detail_quantity_edit);
        buttonGroup = findViewById(R.id.save_and_cancel_button);
        editButton = findViewById(R.id.edit_item);
    }

    public void setUpButtonListener(){
        editButton.setOnClickListener(v -> {
            itemDisplay.setVisibility(View.GONE);
            itemEdit.setVisibility(View.VISIBLE);
            buttonGroup.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.save_button).setOnClickListener(v -> {
            updateItem();
            itemDisplay.setVisibility(View.VISIBLE);
            itemEdit.setVisibility(View.GONE);
            buttonGroup.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            itemDisplay.setVisibility(View.VISIBLE);
            itemEdit.setVisibility(View.GONE);
            buttonGroup.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });
    }

    private void setUpConditionSpinner(){
        String[] conditions = getResources().getStringArray(R.array.itemCondition);
        conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, conditions);
        itemConditionEdit.setAdapter(conditionAdapter);
        itemConditionEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                condition = conditions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                condition = conditions[0];
            }
        });
    }

    public void getItemInfo(){
        itemRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                Inventory item = documentSnapshot.toObject(Inventory.class);
                if(item.getImageUrl() != null){
                    imageUrl = item.getImageUrl();
                    Glide.with(DetailInventory.this).load(imageUrl).into(itemImage);
                    Glide.with(DetailInventory.this).load(imageUrl).into(itemImageEdit);
                }
                if(item.getName() != null){
                    setTitle(item.getName());
                    name = item.getName();
                    itemName.setText(name);
                    itemNameEdit.setText(name);
                }
                if(item.getDescription() != null){
                    desc = item.getDescription();
                    itemDesc.setText(desc);
                    itemDescEdit.setText(desc);
                }
                if(item.getCondition() != null) {
                    condition = item.getCondition();
                    itemCondition.setText("Condition: " + condition);
                    itemConditionEdit.setSelection(conditionAdapter.getPosition(condition));
            }
                if(item.getCategory() != null) {
                    category = item.getCategory();
                    itemCategory.setText("Category: " + category);
                    itemCategoryEdit.setText(category);
                }
                if(item.getPrice() != 0) {
                    price = item.getPrice();
                    itemPrice.setText("$" + price);
                    itemPriceEdit.setText("" + price);
                }
                if(item.getQuantity() != 0) {
                    quantity = item.getQuantity();
                    itemQuantity.setText("Quantities: " + quantity);
                    itemQuantityEdit.setText("" + quantity);
                }
            })
            .addOnFailureListener(e -> Toast.makeText(DetailInventory.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    public void updateItem(){
        if(isValidInput()){
            name = itemNameEdit.getText().toString();
            desc = itemDescEdit.getText().toString();
            price = Double.parseDouble(itemPriceEdit.getText().toString());
            category = itemCategoryEdit.getText().toString();
            quantity = Integer.parseInt(itemQuantityEdit.getText().toString());
        }

        long current = System.currentTimeMillis();
        Inventory inventory = new Inventory(imageUrl, category, name, desc, price, quantity, condition, current);

        itemRef.set(inventory, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(DetailInventory.this, "Update Item Info Successfully!", Toast.LENGTH_SHORT).show();
                getItemInfo();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(DetailInventory.this, "Error!", Toast.LENGTH_LONG).show();
                Log.d(TAG, e.toString());
            });

    }

    public boolean isValidInput(){
        if(itemCategoryEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item category", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemNameEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemDescEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item description", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemPriceEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item price", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemQuantityEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item quantities", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
