package com.shiyunzhang.wetrade.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.AddItemActivity;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DetailInventory;
import com.shiyunzhang.wetrade.EditProfileActivity;
import com.shiyunzhang.wetrade.InventoryAdapter;
import com.shiyunzhang.wetrade.R;

import java.io.Serializable;
import java.util.ArrayList;


public class InventoryFragment extends Fragment {
    private String TAG = "InventoryFragment";
    private Button addItemButton;
    private ArrayList<Inventory> inventoryArrayList;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private  InventoryAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        inventoryRef = db.collection("Inventory").document(uid).collection("Items");
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addItemButton.setOnClickListener( v-> startActivity(new Intent(getContext(), AddItemActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        inventoryArrayList.clear();
        adapter.notifyDataSetChanged();
        getInventory();
    }

    private void init(View view) {
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
    }

    private void getInventory(){
        inventoryRef.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Inventory inventory = queryDocumentSnapshot.toObject(Inventory.class);
                    inventory.setItemID(queryDocumentSnapshot.getId());
                    inventoryArrayList.add(inventory);
                    adapter.notifyDataSetChanged();
                }
            });
    }
}
