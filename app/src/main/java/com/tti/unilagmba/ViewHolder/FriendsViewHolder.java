package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Scarecrow on 6/18/2018.
 */

public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public CircleImageView friendsPicture;
    public ImageView onlineStatus;
    public TextView friendsName, friendsStatus;
    private ItemClickListener itemClickListener;


    public FriendsViewHolder(View itemView) {
        super(itemView);

        friendsPicture = (CircleImageView)itemView.findViewById(R.id.friendsImage);
        friendsName = (TextView)itemView.findViewById(R.id.friendsName);
        friendsStatus = (TextView)itemView.findViewById(R.id.friendStatus);
        onlineStatus = (ImageView)itemView.findViewById(R.id.onlineIndicator);

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
