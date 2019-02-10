package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

/**
 * Created by Scarecrow on 3/15/2018.
 */

public class FacultyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView facultyName;
    private ItemClickListener itemClickListener;

    public FacultyViewHolder(View itemView) {
        super(itemView);

        facultyName = (TextView)itemView.findViewById(R.id.facultyNameTxt);
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
