package com.tti.unilagmba;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.Faculty;
import com.tti.unilagmba.ViewHolder.FacultyViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class Materials extends Fragment {


    LinearLayoutManager manager;
    FirebaseDatabase db;
    DatabaseReference faculty;
    FirebaseRecyclerAdapter<Faculty, FacultyViewHolder> adapter;
    private RecyclerView facultyRecycler;

    public Materials() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_materials, container, false);


        db = FirebaseDatabase.getInstance();
        faculty = db.getReference("Courses");
        faculty.keepSynced(true);

        facultyRecycler = (RecyclerView)v.findViewById(R.id.facultyRecyclerView);
        facultyRecycler.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext()){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {

                    private static final float SPEED = 300f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }

                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };
        facultyRecycler.setLayoutManager(manager);

        adapter = new FirebaseRecyclerAdapter<Faculty, FacultyViewHolder>(Faculty.class, R.layout.faculty_list, FacultyViewHolder.class, faculty) {
            @Override
            protected void populateViewHolder(FacultyViewHolder viewHolder, final Faculty model, int position) {
                viewHolder.facultyName.setText(model.getName());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent dept = new Intent(getContext(), Documents.class);
                        dept.putExtra("CourseId", adapter.getRef(position).getKey());
                        dept.putExtra("CourseName", model.getName());
                        startActivity(dept);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        facultyRecycler.setAdapter(adapter);
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
