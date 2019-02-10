package com.tti.unilagmba.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.DayDetail;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.Todo;
import com.tti.unilagmba.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scarecrow on 4/13/2018.
 */

class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView course, venue, duration;

    private ItemClickListener itemClickListener;

    public void setTxt_todo_name(TextView course) {
        this.course = course;
    }

    public TodoViewHolder(View itemView) {
        super(itemView);
        course = (TextView)itemView.findViewById(R.id.courseTxt);
        venue = (TextView)itemView.findViewById(R.id.venueTxt);
        duration = (TextView)itemView.findViewById(R.id.duration);


        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0,0, getAdapterPosition(), Common.DELETE);
    }
}

public class TodoAdapter extends  RecyclerView.Adapter<TodoViewHolder>{

    private List<Todo> listData = new ArrayList<>();
    private DayDetail dayDetail;

    public TodoAdapter(List<Todo> listData, DayDetail dayDetail) {
        this.listData = listData;
        this.dayDetail = dayDetail;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(dayDetail);
        View itemView = inflater.inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, final int position) {

        holder.course.setText(listData.get(position).getCourseCode());
        holder.venue.setText(listData.get(position).getVenue());
        holder.duration.setText(listData.get(position).getStart() + " - " + listData.get(position).getStop());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
