package com.shiyunzhang.wetrade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.Inventory;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter {
    Context context;
    List<Inventory> productList;
    View.OnClickListener clickListener;

    public SearchAdapter(Context context, List<Inventory> productList, View.OnClickListener clickListener){
        this.context = context;
        this.productList = productList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.search_item_list, null);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Inventory product = getItem(i);
        SearchViewHolder holder = (SearchViewHolder) viewHolder;
        Glide.with(holder.itemView).load(product.getImageUrl()).into(holder.imageView);
        holder.itemName.setText(product.getName());
        holder.itemCategory.setText("Category: " + product.getCategory());
        holder.itemView.setOnClickListener(clickListener);
        holder.itemView.setTag(i);
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public Inventory getItem(int position){
        return productList.get(position);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView itemName;
        TextView itemCategory;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.search_result_item_img);
            itemName = itemView.findViewById(R.id.search_result_item_name);
            itemCategory = itemView.findViewById(R.id.recent_item_category);
        }
    }
}
