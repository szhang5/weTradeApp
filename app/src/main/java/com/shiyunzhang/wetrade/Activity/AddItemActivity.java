package com.shiyunzhang.wetrade.Activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;


public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItemActivity";
    private static final String COLLECTION = "Inventory";

    private AppCompatSpinner itemCondition;
    private EditText itemQuantity;
    private Button saveButton;
    private ImageView productImage;
    private TextView productName, productDescription, productCategory;
    private String category, name, description, condition, imageUrl, productId;
    private int quantity;
    private FirebaseAuth firebaseAuth;
    private String uid, itemID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryCollect = db.collection(COLLECTION);
    private Inventory inventory;
    private ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(AddItemActivity.this, LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        setUpActionBar();
        init();
    }

    private void init() {
        itemQuantity = findViewById(R.id.item_quantity);
        itemCondition = findViewById(R.id.item_condition);
        saveButton = findViewById(R.id.save_button);
        productImage = findViewById(R.id.product_detail_image);
        productName = findViewById(R.id.product_detail_name);
        productDescription = findViewById(R.id.product_detail_description);
        productCategory = findViewById(R.id.product_detail_category);
        setUpConditionSpinner();
        setUpButtonListener();
        getProductInfoFromIntent();
    }

    private void getProductInfoFromIntent(){
        Intent intent = this.getIntent();
        imageUrl = intent.getStringExtra("imageUrl");
        name = intent.getStringExtra("productName");
        description = intent.getStringExtra("productDescription");
        productId = intent.getStringExtra("productId");
        category = intent.getStringExtra("category");
        productName.setText(name);
        productDescription.setText(description);
        productCategory.setText("Category: " + category);
        Glide.with(this).load(imageUrl).into(productImage);
    }

    private boolean isValidInput() {
        if(itemQuantity.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item quantities", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setUpButtonListener(){
        saveButton.setOnClickListener(v->{
            saveInventoryInfo();
        });
    }

    private void setUpActionBar(){
        setTitle("Add an item");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void setUpConditionSpinner(){
        String[] conditions = getResources().getStringArray(R.array.itemCondition);
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, conditions);
        itemCondition.setAdapter(conditionAdapter);
        itemCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveInventoryHelper(DocumentReference inventoryRef){
        Inventory inventoryInfo = new Inventory(itemID, imageUrl, category, name, description, conditionAndQuantities, uid, productId);
        inventoryRef.set(inventoryInfo)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(AddItemActivity.this, "Item Information Saved", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(AddItemActivity.this, "Error!", Toast.LENGTH_LONG).show();
                Log.d(TAG, e.toString());
            });
    }

    private void saveInventoryInfo(){
        if(isValidInput()){
            quantity = Integer.parseInt(itemQuantity.getText().toString());
        }
        inventoryCollect.whereEqualTo("productID", productId)
            .whereEqualTo("userID", uid)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    inventory = queryDocumentSnapshot.toObject(Inventory.class);
                }
                if(inventory != null) {
                    itemID = inventory.getItemID();
                    productId = inventory.getProductID();
                    ArrayList<ConditionAndQuantity> existConditionAndQuantities = inventory.getConditionAndQuantities();
                    boolean exist = false;
                    if(existConditionAndQuantities != null) {
                        conditionAndQuantities = existConditionAndQuantities;
                    }
                    for(int i = 0; i < conditionAndQuantities.size(); i++) {
                        if(condition.equals(conditionAndQuantities.get(i).getCondition())){
                            exist = true;
                            conditionAndQuantities.get(i).setQuantity(conditionAndQuantities.get(i).getQuantity() + quantity);
                        }
                    }
                    if(!exist) conditionAndQuantities.add(new ConditionAndQuantity(condition, quantity));
                    DocumentReference inventoryRef = inventoryCollect.document(itemID);
                    saveInventoryHelper(inventoryRef);
                } else {
                    DocumentReference inventoryRef = inventoryCollect.document();
                    itemID = inventoryRef.getId();
                    conditionAndQuantities.add(new ConditionAndQuantity(condition, quantity));
                    saveInventoryHelper(inventoryRef);
                }
            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }
}
