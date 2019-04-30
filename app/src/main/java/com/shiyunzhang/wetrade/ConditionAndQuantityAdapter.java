package com.shiyunzhang.wetrade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;

import java.util.ArrayList;

public class ConditionAndQuantityAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ConditionAndQuantity> conditionAndQuantities;

    public ConditionAndQuantityAdapter(Context context, ArrayList<ConditionAndQuantity> conditionAndQuantities){
        this.context = context;
        this.conditionAndQuantities = conditionAndQuantities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.condition_and_quantities_item, null);
        return new ConditionQuantityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ConditionAndQuantity item = getItem(i);
        ConditionQuantityViewHolder holder = (ConditionQuantityViewHolder) viewHolder;
        holder.itemView.setTag(i);
        holder.condition.setText(item.getCondition());
        holder.quantity.setText(item.getQuantity() + "");
        holder.sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on sell button", Toast.LENGTH_SHORT).show();
            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on add button", Toast.LENGTH_SHORT).show();
            }
        });
        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on remove button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return conditionAndQuantities.size();
    }

    public ConditionAndQuantity getItem(int position){
        return conditionAndQuantities.get(position);
    }

    public class ConditionQuantityViewHolder extends RecyclerView.ViewHolder{

        TextView condition;
        TextView quantity;
        Button sellButton;
        ImageView add;
        ImageView subtract;

        public ConditionQuantityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.condition = itemView.findViewById(R.id.condition);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.sellButton = itemView.findViewById(R.id.sell_inventory);
            this.add = itemView.findViewById(R.id.plus);
            this.subtract = itemView.findViewById(R.id.remove);
        }
    }
}
