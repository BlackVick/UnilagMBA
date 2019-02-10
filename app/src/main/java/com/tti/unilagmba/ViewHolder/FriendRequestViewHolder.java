package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Scarecrow on 6/22/2018.
 */

public class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public CircleImageView friendsPicture;
    public TextView friendsName;
    public Button accept, decline;
    private ItemClickListener itemClickListener;
    public CardView friendCard;


    public FriendRequestViewHolder(View itemView) {
        super(itemView);

        friendsPicture = (CircleImageView)itemView.findViewById(R.id.friendsImage);
        friendsName = (TextView)itemView.findViewById(R.id.friendsName);
        accept = (Button)itemView.findViewById(R.id.acceptRequestBtn);
        decline = (Button)itemView.findViewById(R.id.declineRequestBtn);
        friendCard = (CardView)itemView.findViewById(R.id.friendRequestCard);

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
