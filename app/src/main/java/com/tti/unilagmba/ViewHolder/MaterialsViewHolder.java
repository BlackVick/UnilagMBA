package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

/**
 * Created by Scarecrow on 3/10/2018.
 */

public class MaterialsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView materialName;
    public TextView downloadLink;
    public TextView docType;
    public TextView courseName;
    public TextView materialInfo;

    private ItemClickListener itemClickListener;


    public MaterialsViewHolder(View itemView) {
        super(itemView);

        courseName = (TextView)itemView.findViewById(R.id.courseName);
        materialName = (TextView)itemView.findViewById(R.id.materialNameTxt);
        downloadLink = (TextView) itemView.findViewById(R.id.downloadLink);
        docType = (TextView)itemView.findViewById(R.id.docType);
        materialInfo = (TextView)itemView.findViewById(R.id.materialInfoTxt);

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
