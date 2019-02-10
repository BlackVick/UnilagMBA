package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Scarecrow on 6/26/2018.
 */

public class FacultyAdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView adminName, adminPost;
    public CircleImageView adminImage;
    private ItemClickListener itemClickListener;

    public FacultyAdminViewHolder(View itemView) {
        super(itemView);
        adminImage = (CircleImageView)itemView.findViewById(R.id.facultyAdminImage);
        adminName = (TextView)itemView.findViewById(R.id.facultyAdminName);
        adminPost = (TextView)itemView.findViewById(R.id.facultyAdminPosition);
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
