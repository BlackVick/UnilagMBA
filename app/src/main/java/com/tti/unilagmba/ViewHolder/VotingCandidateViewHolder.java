package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Scarecrow on 7/14/2018.
 */

public class VotingCandidateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public CircleImageView candidateImage;
    public TextView candidateName;
    private ItemClickListener itemClickListener;

    public VotingCandidateViewHolder(View itemView) {
        super(itemView);

        candidateImage = (CircleImageView)itemView.findViewById(R.id.candidatePicture);
        candidateName = (TextView)itemView.findViewById(R.id.candidateName);
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
