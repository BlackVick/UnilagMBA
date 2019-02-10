package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleImageView;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.R;

/**
 * Created by Scarecrow on 6/23/2018.
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

    public ImageView sentTick;
    public TextView messageTextSender, messageTextYou, sentAbbvr;
    public BubbleImageView imageSender, imageYou, documentSender, documentYou;
    public LinearLayout yourWholeMessageLayout;

    public ChatMessageViewHolder(View itemView) {
        super(itemView);

        sentTick = (ImageView)itemView.findViewById(R.id.messageSentTick);
        sentAbbvr = (TextView)itemView.findViewById(R.id.messageSentAbbvr);
        messageTextSender = (TextView)itemView.findViewById(R.id.chatMessageTextSender);
        messageTextYou = (TextView)itemView.findViewById(R.id.chatMessageTextYou);
        imageSender = (BubbleImageView)itemView.findViewById(R.id.chatImageSender);
        imageYou = (BubbleImageView)itemView.findViewById(R.id.chatImageYou);
        documentYou = (BubbleImageView)itemView.findViewById(R.id.chatDocumentYou);
        documentSender = (BubbleImageView)itemView.findViewById(R.id.chatDocumentSender);
        yourWholeMessageLayout = (LinearLayout)itemView.findViewById(R.id.yourMessageLayoutForReadReceipt);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select An Option");
        menu.add(0,0,getAdapterPosition(), Common.DELETE_SINGLE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE_BOTH);
    }
}
