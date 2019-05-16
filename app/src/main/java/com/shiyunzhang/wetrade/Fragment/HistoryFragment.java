package com.shiyunzhang.wetrade.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shiyunzhang.wetrade.Activity.AuctionActivity;
import com.shiyunzhang.wetrade.Adapter.AuctionAdapter;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.Auction;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.Adapter.TransactionAdapter;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {
    private ArrayList<Transaction> orderArrayList, sellArrayList;
    private ArrayList<Auction> auctionArrayList;
    private RecyclerView orderHistoryRecyclerView, sellHistoryRecyclerView, auctionHistoryRecyclerView;
    private TransactionAdapter orderAdapter, sellAdapter;
    private AuctionAdapter auctionAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderCollection, auctionCollection;
    private ProgressBar historyProgressBar;
    private LinearLayout orderSellHistoryLayout;
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
        auctionCollection = db.collection("Auction");
        init(view);
        return view;
    }

    private void init(View view){
        TabLayout tabLayout = view.findViewById(R.id.history_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Order&Sell History"));
        tabLayout.addTab(tabLayout.newTab().setText("Auction"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    orderSellHistoryLayout.setVisibility(View.VISIBLE);
                    auctionHistoryRecyclerView.setVisibility(View.GONE);
                } else {
                    orderSellHistoryLayout.setVisibility(View.GONE);
                    auctionHistoryRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        orderSellHistoryLayout = view.findViewById(R.id.order_sell_history_layout);
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

        auctionArrayList = new ArrayList<>();
        auctionHistoryRecyclerView = view.findViewById(R.id.auction_history_recyclerview);
        auctionHistoryRecyclerView.setHasFixedSize(true);
        auctionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auctionAdapter = new AuctionAdapter(getContext(), auctionArrayList, v -> {
            int position = (int) v.getTag();
            Auction auction = auctionArrayList.get(position);
                String auctionId = auction.getAuctionId();
                Intent intent = new Intent(getContext(), AuctionActivity.class);
                intent.putExtra("AUCTIONID", auctionId);
                if(auction.getInventory().getUserID().equals(uid)){
                    intent.putExtra("USERTYPE", "Host");
                } else {
                    intent.putExtra("USERTYPE", "Participant");
                }
                startActivity(intent);
            });
        auctionHistoryRecyclerView.setAdapter(auctionAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        orderArrayList.clear();
        sellArrayList.clear();
        auctionArrayList.clear();
        getOrderList();
        getSellList();
        getAuctionList();
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

    private void getAuctionList(){
        auctionCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                        Auction auction = queryDocumentSnapshot.toObject(Auction.class);
                        auctionArrayList.add(auction);
                        auctionAdapter.notifyDataSetChanged();
                    }
                });
    }
}