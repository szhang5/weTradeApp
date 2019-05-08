package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.DataClass.ItemForSale;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerInfoActivity extends AppCompatActivity {
    private final static String TAG = "SellerInfoActivity";
    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private CollectionReference itemForSaleRef;
    private CircleImageView sellerImage;
    private TextView sellerName, sellerCollege;
    private ArrayList<ItemForSale> sellerItems;
    private SearchItemForSaleAdapter adapter;
    private ProgressBar progressBar;

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
        setContentView(R.layout.activity_seller_info);
        setUpActionBar();
        init();
        getSellerInfo();
        getSellerOnSaleItems();
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void init(){
        Intent intent = getIntent();
        uid = intent.getStringExtra("SELLERID");
        userRef = db.collection("User").document(uid);
        itemForSaleRef = db.collection("ItemForSale");
        sellerImage = findViewById(R.id.seller_image);
        sellerName = findViewById(R.id.seller_name);
        sellerCollege = findViewById(R.id.seller_college);
        sellerItems = new ArrayList<>();
        progressBar = findViewById(R.id.seller_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = findViewById(R.id.seller_item_for_sale_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SearchItemForSaleAdapter(this, sellerItems, v -> {
            int position = (int) v.getTag();
            Intent newIntent = new Intent(this, RecentItemDetailActivity.class);
            newIntent.putExtra("ID", sellerItems.get(position).getItemID());
            startActivity(newIntent);
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getSellerInfo(){
        userRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                if(userInfo.getImageUrl() != null) {
                    Glide.with(SellerInfoActivity.this).load(userInfo.getImageUrl()).into(sellerImage);
                }
                if(userInfo.getFirstName() != null && userInfo.getLastName() != null) {
                    String name = userInfo.getFirstName() + " " + userInfo.getLastName();
                    setTitle(name);
                    sellerName.setText(name);
                }
                if(userInfo.getCollege() != null){
                    sellerCollege.setText(userInfo.getCollege());
                }
            })
            .addOnFailureListener(e -> Toast.makeText(SellerInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void getSellerOnSaleItems(){
        itemForSaleRef
                .whereEqualTo("userID", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        ItemForSale item = queryDocumentSnapshot.toObject(ItemForSale.class);
                        sellerItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
