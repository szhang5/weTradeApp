package com.shiyunzhang.wetrade.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.Auction;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

public class AuctionAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Auction> auctionArrayList;
    private View.OnClickListener clickListener;

    public AuctionAdapter(Context context, ArrayList<Auction> auctionArrayList, View.OnClickListener clickListener) {
        this.context = context;
        this.auctionArrayList = auctionArrayList;
        this.clickListener = clickListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.auction_item, null);
        return new AuctionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Auction auction = getItem(i);
        AuctionViewHolder holder = (AuctionViewHolder) viewHolder;
        Glide.with(holder.itemView).load(auction.getInventory().getImageUrl()).into(holder.auctionItemImg);
        holder.auctionItemName.setText(auction.getInventory().getName());
        holder.auctionStartPrice.setText("Start Price: $ " + auction.getPrice());
        if(auction.getAuctionStatus() != null){
            String status = auction.getAuctionStatus();
            holder.auctionItemStatus.setText(status);
            if(status.equals("In Progress")){
                holder.auctionItemStatus.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                holder.auctionItemStatus.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
        holder.itemView.setTag(i);
        holder.itemView.setOnClickListener(clickListener);

    }

    @Override
    public int getItemCount() {
        return auctionArrayList.size();
    }

    public Auction getItem(int position){
        return auctionArrayList.get(position);
    }

    public class AuctionViewHolder extends RecyclerView.ViewHolder{

        private ImageView auctionItemImg;
        private TextView auctionItemName;
        private TextView auctionStartPrice;
        private TextView auctionItemStatus;

        public AuctionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.auctionItemImg = itemView.findViewById(R.id.auction_item_img);
            this.auctionItemName = itemView.findViewById(R.id.auction_item_name);
            this.auctionStartPrice = itemView.findViewById(R.id.auction_start_price);
            this.auctionItemStatus = itemView.findViewById(R.id.auction_item_status);
        }
    }
}
