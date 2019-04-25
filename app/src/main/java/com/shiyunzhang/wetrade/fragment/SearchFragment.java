package com.shiyunzhang.wetrade.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.shiyunzhang.wetrade.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private SearchView searchView;
    private ListView searchResult;
    private Index index;

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

    private void init(View view){
        searchView = view.findViewById(R.id.search_bar);
        searchResult = view.findViewById(R.id.search_result_list_view);
        Client client = new Client("KBCNT58640", "7bf45a6696368d5ebc1ff04e57e4e1e0");
        index = client.getIndex("Inventory");
        setSearchListener();
    }

    private void setSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = new Query(newText)
                        .setAttributesToRetrieve("name")
                        .setHitsPerPage(50);
                index.searchAsync(query, (jsonObject, e) -> {
                    try {
                        JSONArray hits = jsonObject.getJSONArray("hits");
                        List<String> list = new ArrayList<>();
                        for(int i = 0; i < hits.length(); i++) {
                            JSONObject productObject = hits.getJSONObject(i);
                            String productName = productObject.getString("name");
                            list.add(productName);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
                        searchResult.setVisibility(View.VISIBLE);
                        searchResult.setAdapter(arrayAdapter);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                });
                return false;
            }
        });
    }

}
