package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

/**
 * Created by Scarecrow on 3/10/2018.
 */

public class NewsFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView newsPicture;
    public TextView newsTitle, newsDetail, newsTime;
    private ItemClickListener itemClickListener;


    public NewsFeedViewHolder(View itemView) {
        super(itemView);

        newsPicture = (ImageView)itemView.findViewById(R.id.news_image);
        newsTitle = (TextView)itemView.findViewById(R.id.news_head);
        newsTime = (TextView)itemView.findViewById(R.id.news_time);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
