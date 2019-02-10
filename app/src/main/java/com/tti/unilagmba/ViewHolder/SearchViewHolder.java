package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Scarecrow on 6/22/2018.
 */

public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public CircleImageView searchResultPicture;
    public TextView searchResultName;
    private ItemClickListener itemClickListener;


    public SearchViewHolder(View itemView) {
        super(itemView);

        searchResultPicture = (CircleImageView)itemView.findViewById(R.id.searchResultImage);
        searchResultName = (TextView)itemView.findViewById(R.id.searchResultName);

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
