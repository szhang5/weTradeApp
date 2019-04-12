package com.shiyunzhang.wetrade.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shiyunzhang.wetrade.AddItemActivity;
import com.shiyunzhang.wetrade.R;


public class InventoryFragment extends Fragment {
    Button addItemButton;

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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addItemButton.setOnClickListener( v-> {
            startActivity(new Intent(getContext(), AddItemActivity.class));
        });
    }

    private void init(View view){
        addItemButton = view.findViewById(R.id.add_item_button);
    }

}
