package com.shiyunzhang.wetrade.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

public class CheckoutItemAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Transaction> checkoutList;

    public CheckoutItemAdapter(Context context, ArrayList<Transaction> checkoutList) {
        this.context = context;
        this.checkoutList = checkoutList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.checkout_item, null);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Transaction transaction = getItem(i);
        CheckoutItemViewHolder holder = (CheckoutItemViewHolder) viewHolder;
        Glide.with(holder.itemView).load(transaction.getImageUrl()).into(holder.imageView);
        holder.quantityView.setText("Quantity: " + transaction.getQuantity());
        holder.priceView.setText("$ " + transaction.getPrice());
        holder.conditionView.setText("Condition: " + transaction.getCondition());
    }

    @Override
    public int getItemCount() {
        return checkoutList.size();
    }

    public Transaction getItem(int position){
        return checkoutList.get(position);
    }

    public class CheckoutItemViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView priceView;
        TextView conditionView;
        TextView quantityView;

        public CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.conditionView = itemView.findViewById(R.id.checkout_condition);
            this.imageView = itemView.findViewById(R.id.checkout_image);
            this.priceView = itemView.findViewById(R.id.checkout_price);
            this.quantityView = itemView.findViewById(R.id.checkout_quantity);
        }
    }
}
