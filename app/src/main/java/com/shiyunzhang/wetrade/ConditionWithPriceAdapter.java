package com.shiyunzhang.wetrade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;

import java.util.ArrayList;

public class ConditionWithPriceAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<ConditionAndQuantity> conditionAndQuantities;

    public ConditionWithPriceAdapter(Context context, ArrayList<ConditionAndQuantity> conditionAndQuantities){
        this.context = context;
        this.conditionAndQuantities = conditionAndQuantities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.condition_and_quantities_with_price, null);
        return new ConditionWithPriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ConditionAndQuantity item = getItem(i);
        ConditionWithPriceViewHolder holder = (ConditionWithPriceViewHolder) viewHolder;
        holder.condition.setText(item.getCondition());
        holder.quantity.setText(item.getQuantity() + "");
        holder.price.setText("$" + item.getPrice());
    }

    @Override
    public int getItemCount() {
        return conditionAndQuantities.size();
    }

    public ConditionAndQuantity getItem(int position){
        return conditionAndQuantities.get(position);
    }

    public class ConditionWithPriceViewHolder extends RecyclerView.ViewHolder{

        TextView condition;
        TextView quantity;
        TextView price;
        ImageView add;
        ImageView subtract;

        public ConditionWithPriceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.condition = itemView.findViewById(R.id.condition);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.price = itemView.findViewById(R.id.sell_price);
            this.add = itemView.findViewById(R.id.plus);
            this.subtract = itemView.findViewById(R.id.remove);
        }
    }
}
