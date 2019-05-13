package com.shiyunzhang.wetrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.ItemForSale;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DetailInventory;
import com.shiyunzhang.wetrade.DetailItemForSaleActivity;
import com.shiyunzhang.wetrade.InventoryAdapter;
import com.shiyunzhang.wetrade.ItemForSaleAdapter;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.TransactionAdapter;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {
    private ArrayList<Transaction> orderArrayList, sellArrayList;
    private RecyclerView orderHistoryRecyclerView, sellHistoryRecyclerView;
    private TransactionAdapter orderAdapter, sellAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderCollection;
    private ProgressBar historyProgressBar;
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        orderCollection = db.collection("Order").document(uid).collection("UserOrder");
        init(view);
        getOrderList();
        getSellList();
        return view;
    }

    private void init(View view){
        TabLayout tabLayout = view.findViewById(R.id.history_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Order History"));
        tabLayout.addTab(tabLayout.newTab().setText("Sell History"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    sellHistoryRecyclerView.setVisibility(View.GONE);
                    orderHistoryRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    orderHistoryRecyclerView.setVisibility(View.GONE);
                    sellHistoryRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        historyProgressBar = view.findViewById(R.id.history_tabs_progressbar);
        historyProgressBar.setVisibility(View.VISIBLE);
        orderArrayList = new ArrayList<>();
        orderHistoryRecyclerView = view.findViewById(R.id.order_history_recyclerview);
        orderHistoryRecyclerView.setHasFixedSize(true);
        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new TransactionAdapter(getContext(), orderArrayList);
        orderHistoryRecyclerView.setAdapter(orderAdapter);

        sellArrayList = new ArrayList<>();
        sellHistoryRecyclerView = view.findViewById(R.id.sell_history_recyclerview);
        sellHistoryRecyclerView.setHasFixedSize(true);
        sellHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sellAdapter = new TransactionAdapter(getContext(), sellArrayList);
        sellHistoryRecyclerView.setAdapter(sellAdapter);
    }

    private void getOrderList(){
        orderCollection.whereEqualTo("customerId", uid).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Transaction transaction = queryDocumentSnapshot.toObject(Transaction.class);
                        orderArrayList.add(transaction);
                        orderAdapter.notifyDataSetChanged();
                    }
                    historyProgressBar.setVisibility(View.GONE);
                });
    }

    private void getSellList(){
        orderCollection.whereEqualTo("sellerId", uid).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Transaction transaction = queryDocumentSnapshot.toObject(Transaction.class);
                        sellArrayList.add(transaction);
                        sellAdapter.notifyDataSetChanged();
                    }

                });
    }
}