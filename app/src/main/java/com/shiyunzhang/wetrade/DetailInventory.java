package com.shiyunzhang.wetrade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.Auction;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;


public class DetailInventory extends AppCompatActivity {
    private final static String TAG = "DetailInventory";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    private DocumentReference auctionRef;
    private ImageView itemImage;
    private TextView itemName, itemDesc, itemCategory, itemQuantity;
    private String name, desc, category, imageUrl, auctionConditionSelected, auctionId;
    private int quantity;
    private Inventory item;
    private UserInfo userInfo;
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
        getUserInfoFromPreference();
        Intent intent = this.getIntent();
        String documentId = intent.getStringExtra("ID");
        itemRef = db.collection("Inventory").document(documentId);
        auctionRef = db.collection("Auction").document();
        auctionId = auctionRef.getId();
        itemImage = findViewById(R.id.item_detail_image);
        itemName = findViewById(R.id.item_detail_name);
        itemDesc = findViewById(R.id.item_detail_description);
        itemCategory = findViewById(R.id.item_detail_category);
        itemQuantity = findViewById(R.id.item_detail_quantity);
    }

    private void getUserInfoFromPreference(){
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userStr = sharedpreferences.getString("USER", "");
        Gson gson = new Gson();
        userInfo = gson.fromJson(userStr, UserInfo.class);

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
                item = documentSnapshot.toObject(Inventory.class);
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.start_auction_popup, null);
        EditText priceView = v.findViewById(R.id.auction_price);
        setUpConditionSpinner(v);
        mBuilder.setTitle("Enter Auction Info Below: ");
        mBuilder.setPositiveButton("Start Auction", (dialog, which) -> {
            if(priceView.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter price before starting an auction", Toast.LENGTH_SHORT).show();
            } else {
                double price = Double.parseDouble(priceView.getText().toString());
                ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();
                conditionAndQuantities.add(new ConditionAndQuantity(auctionConditionSelected, 1));
                item.setConditionAndQuantities(conditionAndQuantities);
                Auction auction = new Auction(auctionId, item, price);
                saveAuctionToDatabase(auction, dialog);
            }
        });
        mBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void setUpConditionSpinner(View view){
        AppCompatSpinner conditionSpinner = view.findViewById(R.id.auction__condition_spinner);
        String[] conditions = new String[conditionAndQuantities.size()];
        for(int i = 0; i < conditionAndQuantities.size(); i++){
            conditions[i] = conditionAndQuantities.get(i).getCondition();
        }
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, conditions);
        conditionSpinner.setAdapter(conditionAdapter);
        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                auctionConditionSelected = conditions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                auctionConditionSelected = conditions[0];
            }
        });
    }

    private void saveAuctionToDatabase(Auction auction, DialogInterface dialog){
        auctionRef.set(auction)
            .addOnSuccessListener(aVoid -> {
                Intent intent = new Intent(DetailInventory.this, AuctionActivity.class);
                intent.putExtra("AUCTIONID", auctionId);
                intent.putExtra("USERTYPE", "Host");
                startActivity(intent);
                dialog.dismiss();
                finish();
            })
            .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
