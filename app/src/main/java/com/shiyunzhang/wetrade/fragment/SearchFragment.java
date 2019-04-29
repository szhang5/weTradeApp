package com.shiyunzhang.wetrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.CategoryItemsDisplayActivity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.RecentItemDetailActivity;
import com.shiyunzhang.wetrade.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView searchResult;
    private SearchAdapter adapter;
    private Index index;
    private GridView categoryGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(),R.layout.search_category_item, getResources().getStringArray(R.array.itemCategory)){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                if(position == 0)
                        view.setBackgroundColor(getResources().getColor(R.color.green));
                if(position == 1)
                    view.setBackground(getResources().getDrawable(R.drawable.makeup));
                if(position == 2)
                    view.setBackground(getResources().getDrawable(R.drawable.books));
                if(position == 4)
                    view.setBackgroundColor(getResources().getColor(R.color.yellow));
                if(position == 5)
                    view.setBackgroundColor(getResources().getColor(R.color.blue));
                if(position == 6)
                    view.setBackground(getResources().getDrawable(R.drawable.fashion));
                if(position == 7)
                    view.setBackground(getResources().getDrawable(R.drawable.electronis));
                return view;
            }
        };
        categoryGridView.setAdapter(categoryAdapter);
        categoryGridView.setOnItemClickListener((parent, view, position, id) -> {
            String category = getResources().getStringArray(R.array.itemCategory)[position];
            Intent intent = new Intent(getActivity(), CategoryItemsDisplayActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        });
    }

    private void init(View view) {
        categoryGridView = view.findViewById(R.id.category_grid_view);
        searchView = view.findViewById(R.id.search_bar);
        searchResult = view.findViewById(R.id.search_result_list_view);
        searchResult.setHasFixedSize(true);
        searchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        Client client = new Client("KBCNT58640", "7bf45a6696368d5ebc1ff04e57e4e1e0");
        index = client.getIndex("Inventory");
        setSearchListener();
    }

    private void setSearchListener() {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (query.length() < 2)
//                    return false;
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.length() < 2) {
//                    searchResult.setVisibility(View.GONE);
//                    return false;
//                }
//                else {
//                    Query query = new Query(newText)
//                            .setAttributesToRetrieve("name", "category", "imageUrl", "itemID")
//                            .setHitsPerPage(50);
//                    index.searchAsync(query, (jsonObject, e) -> {
//                        try {
//                            JSONArray hits = jsonObject.getJSONArray("hits");
//                            List<Inventory> list = new ArrayList<>();
//                            for (int i = 0; i < hits.length(); i++) {
//                                JSONObject productObject = hits.getJSONObject(i);
//                                Gson gson = new Gson();
//                                Inventory product = gson.fromJson(productObject.toString(), Inventory.class);
//                                list.add(product);
//                            }
//                            adapter = new SearchAdapter(getActivity(), list, v -> {
//                                int position = (int) v.getTag();
//                                Intent intent = new Intent(getActivity(), RecentItemDetailActivity.class);
//                                intent.putExtra("ID", list.get(position).getItemID());
//                                startActivity(intent);
//                            });
//                            searchResult.setAdapter(adapter);
//                            searchResult.setVisibility(View.VISIBLE);
//                            adapter.notifyDataSetChanged();
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    });
//                    return true;
//                }
//
//            }
//        });
    }

}
