package com.shiyunzhang.wetrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private final static String TAG = "CheckoutActivity";
    private TextView shippingAddress, itemPrice, totalBeforeTax, taxCollect, totalPrice;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shoppingCartCollection;
    private CollectionReference inventoryCollection = db.collection("Inventory");
    private ArrayList<Transaction> shoppingCartList;
    private RecyclerView mRecyclerView;
    private CheckoutItemAdapter adapter;
    private UserInfo userInfo;
    private double amount, taxAmount, afterTaxTotal;

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
        setContentView(R.layout.activity_checkout);
        setUpActionBar();
        init();
    }

    private void setUpActionBar(){
        setTitle("Checkout");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void getUserInfoFromPreference(){
        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userStr = sharedpreferences.getString("USER", "");
        Gson gson = new Gson();
        userInfo = gson.fromJson(userStr, UserInfo.class);

    }

    private void init(){
        getUserInfoFromPreference();
        shoppingCartCollection = db.collection("ShoppingCart").document(userInfo.getId()).collection("ItemByUser");
        shoppingCartList = new ArrayList<>();
        shippingAddress = findViewById(R.id.shipping_address);
        shippingAddress.setText("Shipping Address: \n" + userInfo.getAddress() + ", " + userInfo.getCity()
                + ", " + userInfo.getState() + ", " + userInfo.getZipCode());
        itemPrice = findViewById(R.id.item_price);
        totalBeforeTax = findViewById(R.id.total_before_tax);
        taxCollect = findViewById(R.id.tax_collect);
        totalPrice = findViewById(R.id.total_price);
        mRecyclerView = findViewById(R.id.checkout_item_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckoutItemAdapter(this, shoppingCartList);
        mRecyclerView.setAdapter(adapter);
        getShoppingCartList();
    }

    private void getShoppingCartList(){
        amount = 0;
        shoppingCartCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Transaction transaction = queryDocumentSnapshot.toObject(Transaction.class);
                        amount += transaction.getPrice()*transaction.getQuantity();
                        shoppingCartList.add(transaction);
                        adapter.notifyDataSetChanged();
                    }
                    taxAmount = 0.0875 * amount;
                    afterTaxTotal = amount + taxAmount;
                    itemPrice.setText("$ " + String.format("%.2f", amount));
                    totalBeforeTax.setText("$ " + String.format("%.2f", amount));
                    taxCollect.setText("$ " + String.format("%.2f", taxAmount));
                    totalPrice.setText("$ " + String.format("%.2f", afterTaxTotal));

                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    public void placeOrder(View view) {
        FrameLayout frameLayout = findViewById(R.id.progressbar_layout);
        frameLayout.setVisibility(View.VISIBLE);
        ArrayList<Task<QuerySnapshot>> customerTasks = new ArrayList<>();
        ArrayList<Task<QuerySnapshot>> sellerTasks = new ArrayList<>();
        Map<String, List<ConditionAndQuantity>> itemInfo = new HashMap<>();
        Map<String, Inventory> productInfo = new HashMap<>();
//        HashSet<String> productsInCart = new HashSet<>();
        for(Transaction transaction : shoppingCartList) {
            String customerId = transaction.getCustomerId();
            String productId = transaction.getProductId();
            String itemId = transaction.getItemId();
            if (itemInfo.containsKey(itemId)) {
                itemInfo.get(itemId).add(new ConditionAndQuantity(transaction.getCondition(), transaction.getQuantity()));
            } else {
                ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();
                conditionAndQuantities.add(new ConditionAndQuantity(transaction.getCondition(), transaction.getQuantity()));
                itemInfo.put(itemId, conditionAndQuantities);
                sellerTasks.add(inventoryCollection.whereEqualTo("itemID", itemId).get());
                customerTasks.add(inventoryCollection.whereEqualTo("productID", productId).whereEqualTo("userID", customerId).get());
            }

            if (productInfo.containsKey(productId)) {
                boolean productExists = false;
                Inventory inventory = productInfo.get(productId);
                ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
                for (ConditionAndQuantity conditionAndQuantity : conditionAndQuantities){
                    if (conditionAndQuantity.getCondition().equals(transaction.getCondition())) {
                        conditionAndQuantity.setQuantity(conditionAndQuantity.getQuantity() + transaction.getQuantity());
                        productExists = true;
                    }
                }
                if (!productExists) {
                    conditionAndQuantities.add(new ConditionAndQuantity(transaction.getCondition(), transaction.getQuantity()));
                    inventory.setConditionAndQuantities(conditionAndQuantities);
                    productInfo.put(productId, inventory);
                }
            } else {
                ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();
                conditionAndQuantities.add(new ConditionAndQuantity(transaction.getCondition(), transaction.getQuantity()));

                Inventory inventory = new Inventory(inventoryCollection.document().getId(), transaction.getImageUrl(), transaction.getCategory(), transaction.getName(), transaction.getDescription(), conditionAndQuantities, customerId, productId);
                productInfo.put(productId, inventory);
            }

        }
        Task<List<QuerySnapshot>> allCustomerTasks = Tasks.whenAllSuccess(customerTasks);
        Task<List<QuerySnapshot>> allSellerTasks = Tasks.whenAllSuccess(sellerTasks);
        allCustomerTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                ArrayList<Task<Void>> updateCustomerInventoryTasks = new ArrayList<>();
                HashSet<String> existProduct = new HashSet<>();
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        continue;
                    }
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Inventory inventory = queryDocumentSnapshot.toObject(Inventory.class);
                        String itemID = inventory.getItemID();
                        String productID = inventory.getProductID();
                        existProduct.add(inventory.getProductID());
                        ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
                        Inventory inventory1 =  productInfo.get(productID);
                        for(ConditionAndQuantity conditionAndQuantity1 : inventory1.getConditionAndQuantities()) {
                            boolean conditionExist = false;
                            for(ConditionAndQuantity conditionAndQuantity : conditionAndQuantities) {
                                if (conditionAndQuantity.getCondition().equals(conditionAndQuantity1.getCondition())) {
                                    conditionAndQuantity.setQuantity(conditionAndQuantity.getQuantity() + conditionAndQuantity1.getQuantity());
                                    conditionExist = true;
                                }
                            }
                            if (!conditionExist) {
                                conditionAndQuantities.add(new ConditionAndQuantity(conditionAndQuantity1.getCondition(), conditionAndQuantity1.getQuantity()));
                            }

                        }
                        inventory.setConditionAndQuantities(conditionAndQuantities);
                        updateCustomerInventoryTasks.add(inventoryCollection.document(itemID).set(inventory, SetOptions.merge()));
                    }
                }
                for (Map.Entry<String, Inventory> entry : productInfo.entrySet()) {
                    if (!existProduct.contains(entry.getKey())) {
                        Inventory inventory = entry.getValue();
                        updateCustomerInventoryTasks.add(inventoryCollection.document(inventory.getItemID()).set(inventory));
                    }
                }
                Task<List<QuerySnapshot>> allUpdateCustomerInventoryTasks = Tasks.whenAllSuccess(updateCustomerInventoryTasks);
                allUpdateCustomerInventoryTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> querySnapshots) {
                        frameLayout.setVisibility(View.GONE);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        allSellerTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                ArrayList<Task<Void>> updateSellerInventoryTasks = new ArrayList<>();
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Inventory inventory = queryDocumentSnapshot.toObject(Inventory.class);
                        String itemID = inventory.getItemID();
                        ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
                        for(ConditionAndQuantity conditionAndQuantity : conditionAndQuantities) {
                            for(ConditionAndQuantity conditionAndQuantity1 : itemInfo.get(itemID)) {
                                if (conditionAndQuantity.getCondition().equals(conditionAndQuantity1.getCondition())) {
                                    conditionAndQuantity.setQuantity(conditionAndQuantity.getQuantity() - conditionAndQuantity1.getQuantity());
                                }
                            }
                        }
                        inventory.setConditionAndQuantities(conditionAndQuantities);
                        updateSellerInventoryTasks.add(inventoryCollection.document(itemID).set(inventory, SetOptions.merge()));
                    }
                }
                Task<List<QuerySnapshot>> allUpdateSellerInventoryTasks = Tasks.whenAllSuccess(updateSellerInventoryTasks);
                allUpdateSellerInventoryTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> querySnapshots) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
