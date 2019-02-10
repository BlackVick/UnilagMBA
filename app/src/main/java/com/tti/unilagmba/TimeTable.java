package com.tti.unilagmba;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTable extends Fragment {


    RelativeLayout timeTable;
    RelativeLayout monday, tuesday, wednesday, thursday, friday, saturday;

    public TimeTable() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_time_table, container, false);

        timeTable = (RelativeLayout)v.findViewById(R.id.timeTable);
        monday = (RelativeLayout)v.findViewById(R.id.monday);
        tuesday = (RelativeLayout)v.findViewById(R.id.tuesday);
        wednesday = (RelativeLayout)v.findViewById(R.id.wednesday);
        thursday = (RelativeLayout)v.findViewById(R.id.thursday);
        friday = (RelativeLayout)v.findViewById(R.id.friday);
        saturday = (RelativeLayout)v.findViewById(R.id.saturday);


        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DayDetail.class);
                i.putExtra("Day", "Monday");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DayDetail.class);
                i.putExtra("Day", "Tuesday");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DayDetail.class);
                i.putExtra("Day", "Wednesday");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DayDetail.class);
                i.putExtra("Day", "Thursday");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DayDetail.class);
                i.putExtra("Day", "Friday");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DayDetail.class);
                i.putExtra("Day", "Saturday");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.common_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if  (id == R.id.action_help){
            Intent help = new Intent(getContext(), Help.class);
            startActivity(help);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
