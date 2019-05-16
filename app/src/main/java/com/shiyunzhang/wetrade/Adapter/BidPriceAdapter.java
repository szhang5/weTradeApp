package com.shiyunzhang.wetrade.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shiyunzhang.wetrade.DataClass.UserBidPrice;
import com.shiyunzhang.wetrade.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BidPriceAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<UserBidPrice> userBidPriceArrayList;
    private View.OnClickListener clickListener;
    private int selectedPosition = Adapter.NO_SELECTION;

    public BidPriceAdapter(Context context, ArrayList<UserBidPrice> userBidPriceArrayList, View.OnClickListener clickListener) {
        this.context = context;
        this.userBidPriceArrayList = userBidPriceArrayList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.bid_price_item, null);
        return new BidPriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        UserBidPrice userBidPrice = getItem(i);
        BidPriceViewHolder holder = (BidPriceViewHolder) viewHolder;
        Glide.with(holder.itemView).load(userBidPrice.getImageUrl()).into(holder.bidProfileImg);
        holder.bidName.setText(userBidPrice.getName());
        holder.bidPrice.setText("$ " + userBidPrice.getPrice());
        holder.itemView.setTag(i);
        holder.itemView.setOnClickListener(clickListener);
        if(selectedPosition == i){
//            holder.selectedTag.setVisibility(View.VISIBLE);
            holder.selectedTag.setColorFilter(context.getResources().getColor(R.color.yellow));
        } else {
//            holder.selectedTag.setVisibility(View.GONE);
            holder.selectedTag.setColorFilter(context.getResources().getColor(R.color.whiteGray));
        }
    }

    public void setSelectedPosition(int position){
        selectedPosition =  position ==  selectedPosition ? Adapter.NO_SELECTION : position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userBidPriceArrayList.size();
    }

    public UserBidPrice getItem(int position){
        return userBidPriceArrayList.get(position);
    }

    public class BidPriceViewHolder extends RecyclerView.ViewHolder{

        CircleImageView bidProfileImg;
        TextView bidName;
        TextView bidPrice;
        ImageView selectedTag;

        public BidPriceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.bidProfileImg = itemView.findViewById(R.id.bid_profile_image);
            this.bidName = itemView.findViewById(R.id.bid_name);
            this.bidPrice = itemView.findViewById(R.id.bid_price);
            this.selectedTag = itemView.findViewById(R.id.selected_tag);
        }
    }
}
