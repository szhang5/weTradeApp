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
import android.widget.Button;

import com.shiyunzhang.wetrade.AddItemActivity;
import com.shiyunzhang.wetrade.Inventory;
import com.shiyunzhang.wetrade.InventoryAdapter;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;


public class InventoryFragment extends Fragment {
    Button addItemButton;
    ArrayList<Inventory> inventoryArrayList;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);
        init(view);
        getInventory();
        InventoryAdapter adapter = new InventoryAdapter(getContext(), inventoryArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addItemButton.setOnClickListener( v-> {
            startActivity(new Intent(getContext(), AddItemActivity.class));
        });
    }

    private void init(View view) {
        addItemButton = view.findViewById(R.id.add_item_button);
        recyclerView = view.findViewById(R.id.inventory_recycle_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void getInventory(){
        inventoryArrayList = new ArrayList<>();
        inventoryArrayList.add(new Inventory("Concert tickets",android.R.drawable.ic_dialog_email, 60, "Tickets", "new", 2  ));
        inventoryArrayList.add(new Inventory("Calculus Textbook",android.R.drawable.ic_dialog_email, 100, "Books", "used", 1 ));
        inventoryArrayList.add(new Inventory("Organic Chemistry Notes",android.R.drawable.ic_dialog_email, 25, "Class Notes", "used", 1 ));
    }

}
