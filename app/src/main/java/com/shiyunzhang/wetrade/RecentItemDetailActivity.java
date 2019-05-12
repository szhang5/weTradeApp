package com.shiyunzhang.wetrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentItemDetailActivity extends AppCompatActivity {
    private String TAG = "RecentItemDetailActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shoppingCartCollection;
    private DocumentReference itemRef, userRef;
    private ImageView itemImage;
    private String sellerId, imageUrl, name, condition, category, description;
    private TextView itemName, itemDesc, itemPrice, itemCategory, itemQuantity, userName;
    private ArrayList<ConditionAndQuantity> conditionAndQuantityList;
    private AppCompatSpinner conditionSpinner;
    private CircleImageView userImage;
    private RelativeLayout sellerInfo;
    private String[] conditions;
    private ArrayList<Integer> quantities;
    private int quantity, selectedQuantity;
    private double price;
    private ArrayList<Double> priceList;
    private Inventory item;
    private UserInfo userInfo;
    private Transaction transaction;

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

    private void getUserInfoFromPreference(){
       SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
       String userStr = sharedpreferences.getString("USER", "");
       Gson gson = new Gson();
       userInfo = gson.fromJson(userStr, UserInfo.class);

    }

    private void init(){
        getUserInfoFromPreference();
        Intent intent = this.getIntent();
        String documentId = intent.getStringExtra("ID");
        itemRef = db.collection("ItemForSale").document(documentId);
        shoppingCartCollection = db.collection("ShoppingCart").document(userInfo.getId()).collection("ItemByUser");
        itemImage = findViewById(R.id.recent_item_detail_image);
        itemName = findViewById(R.id.recent_detail_name);
        itemCategory = findViewById(R.id.recent_detail_category);
        itemDesc = findViewById(R.id.recent_detail_description);
        itemQuantity = findViewById(R.id.recent_detail_quantity);
        userImage = findViewById(R.id.user_profile_image);
        userName = findViewById(R.id.user_name);
        sellerInfo = findViewById(R.id.seller_relative_layout);
        itemPrice = findViewById(R.id.recent_detail_price);
        conditionSpinner = findViewById(R.id.recent_detail_condition_spinner);
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void setUpClickListener(){
        sellerInfo.setOnClickListener(v -> {
            Intent intent = new Intent(RecentItemDetailActivity.this, SellerInfoActivity.class);
            intent.putExtra("SELLERID", sellerId);
            startActivity(intent);
        });

        findViewById(R.id.add_to_cart_button).setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.add_to_cart_popup, null);
            initDialogView(view);
            mBuilder.setTitle("Please Confirm: ");
            mBuilder.setPositiveButton("Add To Cart", (dialog, which) -> {
                saveAddToCartInfo(dialog);
            });
            mBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            mBuilder.setView(view);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
        });
    }

    public void initDialogView(View view){
        ImageView mImageView = view.findViewById(R.id.add_to_cart_image);
        TextView nameView = view.findViewById(R.id.add_to_cart_name);
        TextView conditionView = view.findViewById(R.id.add_to_cart_condition);
        TextView priceView = view.findViewById(R.id.add_to_cart_price);
        Glide.with(RecentItemDetailActivity.this).load(imageUrl).into(mImageView);
        nameView.setText(name);
        conditionView.setText("Condition: " + condition);
        priceView.setText("$ " + price);
        setUpSpinner(view);
    }

    public void setUpSpinner(View view) {
        AppCompatSpinner quantitySpinner = view.findViewById(R.id.add_to_cart_quantity);

        Integer[] quantities = new Integer[quantity];
        for (int i = 0; i < quantity; i++) {
            quantities[i] = i + 1;
        }
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, quantities);
        quantitySpinner.setAdapter(quantityAdapter);
        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedQuantity = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                quantitySpinner.setSelection(quantity);
            }
        });
    }

    private void saveAddToCartHelper(DocumentReference documentReference, DialogInterface dialog, String transactionId) {
        long timestamp = System.currentTimeMillis();
        Transaction addToCartItem = new Transaction(transactionId, name, imageUrl, category, description, item.getItemID(),
                item.getUserID(), userInfo.getId(), item.getProductID(), condition, price, selectedQuantity, timestamp);
        documentReference.set(addToCartItem, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
    }

    private void saveAddToCartInfo(DialogInterface dialog) {
        shoppingCartCollection.whereEqualTo("itemId", item.getItemID())
                .whereEqualTo("condition", condition)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        transaction = queryDocumentSnapshot.toObject(Transaction.class);
                    }
                    if (transaction != null) {
                        selectedQuantity = transaction.getQuantity() + selectedQuantity;
                        DocumentReference shoppingCartRef = shoppingCartCollection.document(transaction.getTransactionId());
                        saveAddToCartHelper(shoppingCartRef, dialog, transaction.getTransactionId());
                    } else {
                        DocumentReference shoppingCartRef = shoppingCartCollection.document();
                        String transactionId = shoppingCartRef.getId();
                        saveAddToCartHelper(shoppingCartRef, dialog, transactionId);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }

    public void getItemInfo(){
        itemRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    item = documentSnapshot.toObject(Inventory.class);
                    if(item.getImageUrl() != null){
                        imageUrl = item.getImageUrl();
                        Glide.with(RecentItemDetailActivity.this).load(imageUrl).into(itemImage);
                    }
                    if(item.getName() != null){
                        name = item.getName();
                        setTitle(name);
                        itemName.setText(name);
                    }
                    if(item.getDescription() != null){
                        description = item.getDescription();
                        itemDesc.setText(description);
                    }
                    if(item.getCategory() != null) {
                        category = item.getCategory();
                        itemCategory.setText("Category: " + category);
                    }
                    if(item.getConditionAndQuantities() != null){
                        conditionAndQuantityList = item.getConditionAndQuantities();
                        conditions = new String[conditionAndQuantityList.size()];
                        quantities = new ArrayList<>();
                        priceList = new ArrayList<>();
                        for(int i = 0; i < conditionAndQuantityList.size(); i++){
                            ConditionAndQuantity c = conditionAndQuantityList.get(i);
                            conditions[i] = c.getCondition();
                            quantities.add(c.getQuantity());
                            priceList.add(c.getPrice());
                        }

                        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, conditions);
                        conditionSpinner.setAdapter(conditionAdapter);
                        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                conditionSpinner.setSelection(position);
                                condition = conditions[position];
                                quantity = quantities.get(position);
                                price = priceList.get(position);
                                itemQuantity.setText("Quantity: " + quantity);
                                itemPrice.setText("$" + price);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                condition = conditions[0];
                                quantity = quantities.get(0);
                                price = priceList.get(0);
                                conditionSpinner.setSelection(0);
                                itemPrice.setText("$" + price);
                                itemQuantity.setText("Quantity: " + quantity);
                            }
                        });

                    }
                    if(item.getUserID() != null) {
                        sellerId = item.getUserID();
                        userRef = db.collection("User").document(sellerId);
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
