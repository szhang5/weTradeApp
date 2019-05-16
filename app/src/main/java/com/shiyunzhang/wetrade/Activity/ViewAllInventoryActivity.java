package com.shiyunzhang.wetrade.Activity;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.DataClass.ItemForSale;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.Adapter.SearchItemForSaleAdapter;

import java.util.ArrayList;

public class ViewAllInventoryActivity extends AppCompatActivity {
    private final static String TAG = "ViewAllInventory";
    private SearchItemForSaleAdapter adapter;
    private ArrayList<ItemForSale> itemForSales;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference allItemForSaleRef;
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
        setContentView(R.layout.activity_view_all);
        setUpActionBar();
        init();
    }

    private void init() {
        progressBar = findViewById(R.id.view_all_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        allItemForSaleRef = db.collection("ItemForSale");
        RecyclerView mRecyclerView = findViewById(R.id.view_all_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        itemForSales = new ArrayList<>();
        adapter = new SearchItemForSaleAdapter(this, itemForSales, v -> {
            int position = (int) v.getTag();
            Intent intent = new Intent(ViewAllInventoryActivity.this, RecentItemDetailActivity.class);
            intent.putExtra("ID", itemForSales.get(position).getItemID());
            startActivity(intent);
        });
        mRecyclerView.setAdapter(adapter);
        getAllItemForSaleFromFirebase();

    }

    private void setUpActionBar(){
        setTitle("View All Items");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void getAllItemForSaleFromFirebase(){
        allItemForSaleRef.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    ItemForSale itemForSale = queryDocumentSnapshot.toObject(ItemForSale.class);
                    itemForSales.add(itemForSale);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
