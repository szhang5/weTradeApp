package com.shiyunzhang.wetrade;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;

import java.lang.reflect.Array;
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
//        holder.inventoryPrice.setText("$" + inventory.getPrice());
//        holder.inventoryDesciption.setText(inventory.getDescription());
//        holder.inventoryCategory.setText(inventory.getCategory());
        int totalQuantity = 0;
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<ConditionAndQuantity> conditionAndQuantities = inventory.getConditionAndQuantities();
        if (conditionAndQuantities != null) {
            for(int i = 0; i < conditionAndQuantities.size(); i++){
                totalQuantity += conditionAndQuantities.get(i).getQuantity();
                conditions.add(conditionAndQuantities.get(i).getCondition());
            }
        }
        holder.inventoryCondition.setText("Condition: " + TextUtils.join("/ ", conditions));
        holder.inventoryQuantity.setText("Quantities: " + totalQuantity);
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
//        public TextView inventoryDesciption;
//        public TextView inventoryPrice;
        public TextView inventoryCondition;
//        public TextView inventoryCategory;
        public TextView inventoryQuantity;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.inventoryPic = itemView.findViewById(R.id.inventory_image);
            this.inventoryName = itemView.findViewById(R.id.inventory_name);
//            this.inventoryDesciption = itemView.findViewById(R.id.inventory_description);
//            this.inventoryPrice = itemView.findViewById(R.id.inventory_price);
            this.inventoryCondition = itemView.findViewById(R.id.inventory_condition);
//            this.inventoryCategory = itemView.findViewById(R.id.inventory_category);
            this.inventoryQuantity = itemView.findViewById(R.id.inventory_quantity);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}