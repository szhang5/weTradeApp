package com.shiyunzhang.wetrade.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>{
    private ArrayList<Inventory> inventoryList;
    private Context context;
    private View.OnClickListener clickListener;

    public InventoryAdapter(Context context, ArrayList<Inventory> inventoryList, View.OnClickListener clickListener) {
        this.inventoryList = inventoryList;
        this.context = context;
        this.clickListener = clickListener;
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
        holder.inventoryDesciption.setText(inventory.getDescription());
        int totalQuantity = 0;
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
        if (conditionAndQuantities != null) {
            for(int i = 0; i < conditionAndQuantities.size(); i++){
                totalQuantity += conditionAndQuantities.get(i).getQuantity();
                conditions.add(conditionAndQuantities.get(i).getCondition());
            }
        }
        if (totalQuantity == 0) {
            holder.inventoryQuantity.setText("Out of Stock");
        } else {
            holder.inventoryQuantity.setText("Quantities: " + totalQuantity);
        }
        holder.itemView.setOnClickListener(clickListener);
        holder.itemView.setTag(position);
    }

    public Inventory getItem(int position){
        return inventoryList.get(position);
    }


    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView inventoryPic;
        public TextView inventoryName;
        public TextView inventoryDesciption;
        public TextView inventoryQuantity;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.inventoryPic = itemView.findViewById(R.id.inventory_image);
            this.inventoryName = itemView.findViewById(R.id.inventory_name);
            this.inventoryDesciption = itemView.findViewById(R.id.inventory_description);
            this.inventoryQuantity = itemView.findViewById(R.id.inventory_quantity);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}