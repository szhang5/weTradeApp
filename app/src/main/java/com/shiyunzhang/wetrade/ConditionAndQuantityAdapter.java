package com.shiyunzhang.wetrade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;

import java.util.ArrayList;

public class ConditionAndQuantityAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ConditionAndQuantity> conditionAndQuantities;
    Inventory inventory;

    public ConditionAndQuantityAdapter(Context context, ArrayList<ConditionAndQuantity> conditionAndQuantities){
        this.context = context;
        this.conditionAndQuantities = conditionAndQuantities;
    }

    public void setInventory(Inventory inventory){
        this.inventory = inventory;
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
                Log.d("jdkfjalfjalkfj", inventory.toString());
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View mView = inflater.inflate(R.layout.sell_window_popup, null);
                ImageView image = mView.findViewById(R.id.sell_item_image);
                Glide.with(context).load(inventory.getImageUrl()).into(image);
                TextView name = mView.findViewById(R.id.sell_item_name);
                name.setText(inventory.getName());
                TextView desc = mView.findViewById(R.id.sell_item_desc);
                desc.setText(inventory.getDescription());
                TextView condition = mView.findViewById(R.id.sell_item_condition);
                condition.setText("Condition: " + item.getCondition());
                EditText quantity = mView.findViewById(R.id.sell_item_quantity);
                EditText price = mView.findViewById(R.id.sell_item_price);

                mBuilder.setTitle("Please Enter Selling Item Info:");

                mBuilder.setPositiveButton("Confirm", (dialog, which) -> dialog.dismiss());
                mBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
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
