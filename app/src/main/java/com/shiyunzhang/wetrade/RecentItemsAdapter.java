package com.shiyunzhang.wetrade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.Inventory;

import java.util.ArrayList;

public class RecentItemsAdapter extends RecyclerView.Adapter<RecentItemsAdapter.ViewHolder> {

    private ArrayList<Inventory> recentItemsList;
    private Context context;
    private View.OnClickListener clickListener;


    public RecentItemsAdapter(Context context, ArrayList<Inventory> recentItemsList, View.OnClickListener clickListener){
        this.context = context;
        this.recentItemsList = recentItemsList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecentItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.home_recently_post_recyclerview_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentItemsAdapter.ViewHolder viewHolder, int position) {
        final Inventory inventory = getItem(position);
        Glide.with(viewHolder.itemView).load(inventory.getImageUrl()).dontAnimate().into(viewHolder.recentItemImage);
        viewHolder.recentItemName.setText(inventory.getName());
        viewHolder.recentItemPrice.setText("$" + inventory.getPrice());
        viewHolder.itemView.setOnClickListener(clickListener);
        viewHolder.itemView.setTag(position);
    }

    public Inventory getItem(int position){
        return recentItemsList.get(position);
    }

    @Override
    public int getItemCount() {
        return recentItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView recentItemImage;
        public TextView recentItemName;
        public TextView recentItemPrice;


        public ViewHolder(View itemView) {
            super(itemView);
            this.recentItemImage = itemView.findViewById(R.id.recent_item_img);
            this.recentItemName = itemView.findViewById(R.id.recent_item_name);
            this.recentItemPrice = itemView.findViewById(R.id.recent_item_price);
        }
    }
}
