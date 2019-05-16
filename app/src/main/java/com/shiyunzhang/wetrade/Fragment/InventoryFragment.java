package com.shiyunzhang.wetrade.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.shiyunzhang.wetrade.Activity.DetailInventory;
import com.shiyunzhang.wetrade.Activity.DetailItemForSaleActivity;
import com.shiyunzhang.wetrade.Adapter.InventoryAdapter;
import com.shiyunzhang.wetrade.Adapter.ItemForSaleAdapter;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.Activity.SearchProductActivity;

import java.util.ArrayList;


public class InventoryFragment extends Fragment {
    private String TAG = "InventoryFragment";
    private Button addItemButton;
    private ArrayList<Inventory> inventoryArrayList;
    private ArrayList<ItemForSale> itemForSaleArrayList;
    private String uid;
    private InventoryAdapter adapter;
    private ItemForSaleAdapter itemForSaleAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryRef, itemForSaleRef;
    private ProgressBar recentProgressBar;
    private RecyclerView recyclerView, saleItemRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        inventoryRef = db.collection("Inventory");
        itemForSaleRef = db.collection("ItemForSale");
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addItemButton.setOnClickListener( v-> startActivity(new Intent(getContext(), SearchProductActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        inventoryArrayList.clear();
        getInventory();
        itemForSaleArrayList.clear();
        getItemForSale();
    }

    private void init(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Inventory"));
        tabLayout.addTab(tabLayout.newTab().setText("Item For Sale"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    saleItemRecyclerView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    saleItemRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        recentProgressBar = view.findViewById(R.id.inventory_progressbar);
        recentProgressBar.setVisibility(View.VISIBLE);
        inventoryArrayList = new ArrayList<>();
        addItemButton = view.findViewById(R.id.add_item_button);
        recyclerView = view.findViewById(R.id.inventory_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InventoryAdapter(getContext(), inventoryArrayList, v -> {
            int position = (int)v.getTag();
            Intent intent = new Intent(getActivity(), DetailInventory.class);
            intent.putExtra("UID", uid);
            intent.putExtra("ID", inventoryArrayList.get(position).getItemID());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        itemForSaleArrayList = new ArrayList<>();
        saleItemRecyclerView = view.findViewById(R.id.item_sale_recycle_view);
        saleItemRecyclerView.setHasFixedSize(true);
        saleItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemForSaleAdapter = new ItemForSaleAdapter(getContext(), itemForSaleArrayList, v-> {
            int position = (int) v.getTag();
            Intent intent = new Intent(getActivity(), DetailItemForSaleActivity.class);
            intent.putExtra("UID", uid);
            intent.putExtra("ID", itemForSaleArrayList.get(position).getItemID());
            startActivity(intent);
        });
        saleItemRecyclerView.setAdapter(itemForSaleAdapter);
    }

    private void getInventory(){
        inventoryRef.whereEqualTo("userID", uid).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Inventory inventory = queryDocumentSnapshot.toObject(Inventory.class);
                    ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
                    inventory.setItemID(queryDocumentSnapshot.getId());
                    inventoryArrayList.add(inventory);
                    adapter.notifyDataSetChanged();
                }
                recentProgressBar.setVisibility(View.GONE);
            });
    }

    private void getItemForSale(){
        itemForSaleRef.whereEqualTo("userID", uid).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    ItemForSale itemForSale = queryDocumentSnapshot.toObject(ItemForSale.class);
                    itemForSaleArrayList.add(itemForSale);
                    itemForSaleAdapter.notifyDataSetChanged();
                }

            });
    }
}
