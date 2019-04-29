package com.shiyunzhang.wetrade;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchProductActivity extends AppCompatActivity {
    private static final String COLLECTION = "Product";
    private Index index;
    private SearchView productSearchView;
    private RecyclerView productResultView;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        setUpActionBar();
        init();
    }

    private void init(){
        Client client = new Client("KBCNT58640", "7bf45a6696368d5ebc1ff04e57e4e1e0");
        index = client.getIndex(COLLECTION);
        productSearchView = findViewById(R.id.search_bar);
        productResultView = findViewById(R.id.search_result_list_view);
        productResultView.setHasFixedSize(true);
        productResultView.setLayoutManager(new LinearLayoutManager(this));
        setSearchListener();
    }
    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
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

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }


    public void addNewProduct(View view) {
        startActivity(new Intent(this, AddProductActivity.class));
        finish();
    }

    private void setSearchListener() {
        productSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() < 2)
                    return false;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 2) {
                    productResultView.setVisibility(View.GONE);
                    return false;
                }
                else {
                    Query query = new Query(newText)
                            .setAttributesToRetrieve("name", "category", "image", "productId", "description")
                            .setHitsPerPage(50);
                    index.searchAsync(query, (jsonObject, e) -> {
                        try {
                            JSONArray hits = jsonObject.getJSONArray("hits");
                            List<Product> list = new ArrayList<>();
                            for (int i = 0; i < hits.length(); i++) {
                                JSONObject productObject = hits.getJSONObject(i);
                                Gson gson = new Gson();
                                Product product = gson.fromJson(productObject.toString(), Product.class);
                                list.add(product);
                            }
                            adapter = new SearchAdapter(SearchProductActivity.this, list, v -> {
                                int position = (int) v.getTag();
                                Product product = list.get(position);
                                Intent intent = new Intent(SearchProductActivity.this, AddItemActivity.class);
                                intent.putExtra("imageUrl", product.getImage());
                                intent.putExtra("productName", product.getName());
                                intent.putExtra("productDescription", product.getDescription());
                                intent.putExtra("productId", product.getProductId());
                                intent.putExtra("category", product.getCategory());
                                startActivity(intent);
                                finish();
                            });
                            productResultView.setAdapter(adapter);
                            productResultView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    });
                    return true;
                }

            }
        });
    }
}
