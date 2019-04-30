package com.shiyunzhang.wetrade.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.AddItemActivity;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DetailInventory;
import com.shiyunzhang.wetrade.InventoryAdapter;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.SearchProductActivity;

import java.util.ArrayList;


public class InventoryFragment extends Fragment {
    private String TAG = "InventoryFragment";
    private Button addItemButton;
    private ArrayList<Inventory> inventoryArrayList;
    private String uid;
    private InventoryAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryRef;
    private ProgressBar recentProgressBar;
    private TextView unsoldItemCount;
    private int totalQuantity;
    private TabLayout tabLayout;
    private RecyclerView recyclerView, saleItemRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
//        inventoryRef = db.collection("Inventory").document(uid).collection("Items");
        inventoryRef = db.collection("Inventory");
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
        adapter.notifyDataSetChanged();
        getInventory();
    }

    private void init(View view) {
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Inventory"));
        tabLayout.addTab(tabLayout.newTab().setText("Item For Sale"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    saleItemRecyclerView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Inventory shows", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    saleItemRecyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Item for sales shows", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        unsoldItemCount = view.findViewById(R.id.unsold_item);
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

        saleItemRecyclerView = view.findViewById(R.id.item_sale_recycle_view);
        saleItemRecyclerView.setHasFixedSize(true);
        saleItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getInventory(){
        totalQuantity = 0;
        inventoryRef.whereEqualTo("userID", uid).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Inventory inventory = queryDocumentSnapshot.toObject(Inventory.class);
                    ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
                    if (conditionAndQuantities != null) {
                        for(int i = 0; i < conditionAndQuantities.size(); i++){
                            totalQuantity += conditionAndQuantities.get(i).getQuantity();
                        }
                    }
                    inventory.setItemID(queryDocumentSnapshot.getId());
                    inventoryArrayList.add(inventory);
                    adapter.notifyDataSetChanged();
                }
                recentProgressBar.setVisibility(View.GONE);
                unsoldItemCount.setText(""+totalQuantity);
            });
    }
}
