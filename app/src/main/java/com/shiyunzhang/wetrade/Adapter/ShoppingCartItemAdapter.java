package com.shiyunzhang.wetrade.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.Transaction;
import com.shiyunzhang.wetrade.DataClass.UserInfo;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

public class ShoppingCartItemAdapter extends RecyclerView.Adapter {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference shoppingCartRef;
    private Context context;

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private UserInfo userInfo;
    private ArrayList<Transaction> shoppingCartItems;

    public ShoppingCartItemAdapter(Context context, ArrayList<Transaction> shoppingCartItems){
        this.context = context;
        this.shoppingCartItems = shoppingCartItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.shopping_cart_item, null);
        return new ShoppingCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Transaction transaction = getItem(i);
        ShoppingCartViewHolder holder = (ShoppingCartViewHolder) viewHolder;
        Glide.with(holder.itemView).load(transaction.getImageUrl()).into(holder.shoppingCartImg);
        holder.shoppingCartName.setText(transaction.getName());
        holder.shoppingCartCondition.setText("Condition: " + transaction.getCondition());
        holder.shoppingCartQuantity.setText("" + transaction.getQuantity());
        holder.shoppingCartPrice.setText("$" + transaction.getPrice());
        holder.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Delete item - " + transaction.getName());
            builder.setTitle("Please Confirm?");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                deleteShoppingCartItem(transaction, dialog, i);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();

        });
    }

    @Override
    public int getItemCount() {
        return shoppingCartItems.size();
    }

    public Transaction getItem(int position){
        return shoppingCartItems.get(position);
    }

    public class ShoppingCartViewHolder extends RecyclerView.ViewHolder {

        ImageView shoppingCartImg;
        TextView shoppingCartName;
        TextView shoppingCartCondition;
        TextView shoppingCartPrice;
        TextView shoppingCartQuantity;
        ImageView addBtn;
        ImageView removeBtn;
        Button deleteBtn;

        public ShoppingCartViewHolder(@NonNull View itemView) {
            super(itemView);
            this.shoppingCartImg = itemView.findViewById(R.id.shopping_cart_image);
            this.shoppingCartName = itemView.findViewById(R.id.add_to_cart_name);
            this.shoppingCartCondition = itemView.findViewById(R.id.add_to_cart_condition);
            this.shoppingCartPrice = itemView.findViewById(R.id.add_to_cart_price);
            this.shoppingCartQuantity = itemView.findViewById(R.id.quantity);
            this.addBtn = itemView.findViewById(R.id.plus);
            this.removeBtn = itemView.findViewById(R.id.remove);
            this.deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }

    public void deleteShoppingCartItem(Transaction transaction, DialogInterface dialog, int position){
        shoppingCartRef = db.collection("ShoppingCart").document(userInfo.getId()).collection("ItemByUser").document(transaction.getTransactionId());
        shoppingCartRef.delete().addOnSuccessListener(aVoid -> dialog.dismiss());
        shoppingCartItems.remove(position);
        notifyDataSetChanged();
    }

}
