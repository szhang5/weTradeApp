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
import com.shiyunzhang.wetrade.DataClass.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    private ArrayList<Transaction> transactionList;
    private Context context;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.inventory_recycleview_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Transaction inventory = getItem(position);
        holder.inventoryName.setText(inventory.getName());
        Glide.with(holder.itemView).load(inventory.getImageUrl()).into(holder.inventoryPic);
        holder.inventoryDesciption.setText(inventory.getDescription());
        holder.inventoryQuantity.setText("Quantities: " + inventory.getQuantity());
        holder.itemView.setTag(position);
    }

    public Transaction getItem(int position){
        return transactionList.get(position);
    }


    @Override
    public int getItemCount() {
        return transactionList.size();
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