package com.shiyunzhang.wetrade;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>{
    private ArrayList<Inventory> inventoryList;

    public InventoryAdapter(ArrayList<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.inventory_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata[position];
        holder.inventoryName.setText(listdata[position].getName());
        holder.inventoryPic.setImageResource(listdata[position].getPicId());
        holder.inventoryPrice.setText(listdata[position].getPrice());
        holder.inventoryCategory.setText(listdata[position].getCategory());
        holder.inventoryCondition.setText(listdata[position].getCondition());
        holder.inventoryQuantity.setText(listdata[position].getQuantity());
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView inventoryPic;
        public TextView inventoryName;
        public TextView inventoryPrice;
        public TextView inventoryCategory;
        public TextView inventoryCondition;
        public TextView inventoryQuantity;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.inventoryPic = (ImageView) itemView.findViewById(R.id.inventory_image);
            this.inventoryName = (TextView) itemView.findViewById(R.id.inventory_name);
            this.inventoryPrice = (TextView) itemView.findViewById(R.id.inventory_price);
            this.inventoryCategory = (TextView) itemView.findViewById(R.id.inventory_category);
            this.inventoryCondition = (TextView) itemView.findViewById(R.id.inventory_condition);
            this.inventoryQuantity = (TextView) itemView.findViewById(R.id.inventory_quantity);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}