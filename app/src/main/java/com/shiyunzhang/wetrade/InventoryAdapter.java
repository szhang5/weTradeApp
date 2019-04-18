package com.shiyunzhang.wetrade;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.Inventory;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>{
    private ArrayList<Inventory> inventoryList;
    private Context context;

    public InventoryAdapter(Context context, ArrayList<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.inventory_recycleview_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Inventory inventory = getItem(position);
        holder.inventoryName.setText(inventory.getName());
        Glide.with(holder.itemView).load(inventory.getImageUrl()).into(holder.inventoryPic);
        holder.inventoryPrice.setText("$" + inventory.getPrice());
        holder.inventoryCondition.setText(inventory.getCondition());
        holder.inventoryDesciption.setText(inventory.getDescription());
        holder.inventoryCategory.setText("Category: " + inventory.getCategory());
        holder.inventoryQuantity.setText("Quantities: " + inventory.getQuantity());
    }

    public Inventory getItem(int position){
        return inventoryList.get(position);
    }

    public void setInventoryList(ArrayList<Inventory> inventoryList){
        this.inventoryList = inventoryList;
        notifyDataSetChanged();
    }

    public void addInventory(Inventory inventory){
        inventoryList.add(inventory);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView inventoryPic;
        public TextView inventoryName;
        public TextView inventoryDesciption;
        public TextView inventoryPrice;
        public TextView inventoryCondition;
        public TextView inventoryCategory;
        public TextView inventoryQuantity;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.inventoryPic = itemView.findViewById(R.id.inventory_image);
            this.inventoryName = itemView.findViewById(R.id.inventory_name);
            this.inventoryDesciption = itemView.findViewById(R.id.inventory_description);
            this.inventoryPrice = itemView.findViewById(R.id.inventory_price);
            this.inventoryCondition = itemView.findViewById(R.id.inventory_condition);
            this.inventoryCategory = itemView.findViewById(R.id.inventory_category);
            this.inventoryQuantity = itemView.findViewById(R.id.inventory_quantity);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}