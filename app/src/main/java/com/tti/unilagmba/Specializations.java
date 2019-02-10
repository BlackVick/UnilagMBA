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
import com.tti.unilagmba.Model.Specialization;
import com.tti.unilagmba.ViewHolder.FacultyViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class Specializations extends Fragment {

    private LinearLayoutManager manager;
    private FirebaseDatabase db;
    private DatabaseReference specializations;
    private FirebaseRecyclerAdapter<Specialization, FacultyViewHolder> adapter;
    private RecyclerView specializationsRecycler;


    public Specializations() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_specializations, container, false);

        db = FirebaseDatabase.getInstance();
        specializations = db.getReference("Specializations");
        specializations.keepSynced(true);

        specializationsRecycler = (RecyclerView)v.findViewById(R.id.specializationRecyclerView);
        specializationsRecycler.setHasFixedSize(true);
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
        specializationsRecycler.setLayoutManager(manager);

        adapter = new FirebaseRecyclerAdapter<Specialization, FacultyViewHolder>(Specialization.class, R.layout.faculty_list, FacultyViewHolder.class, specializations) {
            @Override
            protected void populateViewHolder(FacultyViewHolder viewHolder, final Specialization model, int position) {
                viewHolder.facultyName.setText(model.getName());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent coures = new Intent(getContext(), SpecializationCourses.class);
                        coures.putExtra("specializationId", adapter.getRef(position).getKey());
                        coures.putExtra("SpecializationName", model.getName());
                        startActivity(coures);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        specializationsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
