package com.shiyunzhang.wetrade.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.ItemForSale;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

public class ItemForSaleAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ItemForSale> itemForSales;
    View.OnClickListener clickListener;

    public ItemForSaleAdapter(Context context, ArrayList<ItemForSale> itemForSales, View.OnClickListener clickListener){
        this.context = context;
        this.itemForSales = itemForSales;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.item_for_sale_item, null);
        return new ItemForSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemForSale itemForSale = getItem(i);
        ItemForSaleViewHolder holder = (ItemForSaleViewHolder) viewHolder;
        Glide.with(context).load(itemForSale.getImageUrl()).into(holder.itemImage);
        holder.itemName.setText(itemForSale.getName());
        holder.itemCategory.setText("Category: " + itemForSale.getCategory());
        int quantities = 0;
        for(ConditionAndQuantity conditionAndQuantity : itemForSale.getConditionAndQuantities()){
            quantities += conditionAndQuantity.getQuantity();
        }
        if (quantities == 0) {
            holder.itemQuantity.setText("Out of Stock");
        } else {
            holder.itemQuantity.setText("Total Quantities: " + quantities);
        }
        holder.itemView.setTag(i);
        holder.itemView.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return itemForSales.size();
    }

    public ItemForSale getItem(int position){
        return itemForSales.get(position);
    }

    public class ItemForSaleViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemName;
        TextView itemCategory;
        TextView itemQuantity;

        public ItemForSaleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemImage = itemView.findViewById(R.id.item_for_sale_img);
            this.itemName = itemView.findViewById(R.id.item_for_sale_name);
            this.itemCategory = itemView.findViewById(R.id.item_for_sale_category);
            this.itemQuantity = itemView.findViewById(R.id.item_for_sale_quantity);
        }
    }
}
