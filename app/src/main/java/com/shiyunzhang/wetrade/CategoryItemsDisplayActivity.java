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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.DataClass.Product;

import java.util.ArrayList;

public class CategoryItemsDisplayActivity extends AppCompatActivity {
    private String TAG = "CategoryItemsDisplayActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference categoryItemsRef;
    private SearchAdapter adapter;
    private String category;
    private RecyclerView categoryRecyclerView;
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
        setContentView(R.layout.activity_category_item_display);
        Intent intent = this.getIntent();
        category = intent.getStringExtra("CATEGORY");
        categoryItemsRef = db.collection("Product");
        init();
        setUpActionBar();
    }

    private void init(){
        progressBar = findViewById(R.id.category_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getCategoryItems();
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        setTitle(category);
    }


    private void getCategoryItems(){
        categoryItemsRef.whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Product> productList  = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Product item = queryDocumentSnapshot.toObject(Product.class);
                        productList.add(item);
                    }
                    adapter = new SearchAdapter(CategoryItemsDisplayActivity.this, productList, v-> {
                        int position = (int) v.getTag();
//                        Intent intent = new Intent(this, RecentItemDetailActivity.class);
//                        intent.putExtra("ID", productList.get(position).getProductId());
//                        startActivity(intent);
                    });
                    categoryRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
