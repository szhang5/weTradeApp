package com.shiyunzhang.wetrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.RecentItemDetailActivity;
import com.shiyunzhang.wetrade.RecentItemsAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private String TAG = "HomeFragment";
    private ArrayList<Inventory> recentItemsList;
    private ArrayList<Inventory> homeDecorList;
    private ArrayList<Inventory> kitchenList;
    private RecentItemsAdapter adapter;
    private RecentItemsAdapter homeDecorAdapter;
    private RecentItemsAdapter kitchenAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recentItemsRef;
    private ProgressBar recentProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        recentItemsRef = db.collection("Inventory");
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recentItemsList.clear();
        homeDecorList.clear();
        kitchenList.clear();
        adapter.notifyDataSetChanged();
        homeDecorAdapter.notifyDataSetChanged();
        kitchenAdapter.notifyDataSetChanged();
        getRecentItems();
        getHomeDecorItems();
        getKitchenItems();
    }

    private void init(View view){
        recentProgressBar = view.findViewById(R.id.recent_progressbar);
        recentProgressBar.setVisibility(View.VISIBLE);
        recentItemsList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recent_items_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecentItemsAdapter(getContext(), recentItemsList, v -> {
            int position = (int) v.getTag();
            Intent intent = new Intent(getActivity(), RecentItemDetailActivity.class);
            intent.putExtra("ID", recentItemsList.get(position).getItemID());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        homeDecorList = new ArrayList<>();
        RecyclerView homeDecorView = view.findViewById(R.id.category_home_decor_recycle_view);
        homeDecorView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeDecorAdapter = new RecentItemsAdapter(getContext(), homeDecorList, v->{
            int position = (int) v.getTag();
            Intent intent = new Intent(getActivity(), RecentItemDetailActivity.class);
            intent.putExtra("ID", homeDecorList.get(position).getItemID());
            startActivity(intent);
        });
        homeDecorView.setAdapter(homeDecorAdapter);
        homeDecorAdapter.notifyDataSetChanged();

        kitchenList = new ArrayList<>();
        RecyclerView kitchenView = view.findViewById(R.id.category_kitchen_recycle_view);
        kitchenView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        kitchenAdapter = new RecentItemsAdapter(getContext(), kitchenList, v-> {
            int position = (int) v.getTag();
            Intent intent = new Intent(getActivity(), RecentItemDetailActivity.class);
            intent.putExtra("ID", kitchenList.get(position).getItemID());
            startActivity(intent);
        });
        kitchenView.setAdapter(kitchenAdapter);
        kitchenAdapter.notifyDataSetChanged();
    }

    private void getRecentItems(){
        recentItemsRef
                .limit(10)
                .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Inventory item = queryDocumentSnapshot.toObject(Inventory.class);
                    recentItemsList.add(item);
                    adapter.notifyDataSetChanged();
                }
                recentProgressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    private void getHomeDecorItems(){
        recentItemsRef.whereEqualTo("category", "Apps and Games")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Inventory item = queryDocumentSnapshot.toObject(Inventory.class);
                        homeDecorList.add(item);
                        homeDecorAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    private void getKitchenItems(){
        recentItemsRef.whereEqualTo("category", "Electronics")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Inventory item = queryDocumentSnapshot.toObject(Inventory.class);
                        kitchenList.add(item);
                        kitchenAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
