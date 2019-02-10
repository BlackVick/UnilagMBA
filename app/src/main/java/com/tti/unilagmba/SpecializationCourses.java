package com.tti.unilagmba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.Specialization;
import com.tti.unilagmba.Model.SpecializationCourse;
import com.tti.unilagmba.ViewHolder.FacultyViewHolder;

import io.paperdb.Paper;

public class SpecializationCourses extends AppCompatActivity {

    private LinearLayoutManager manager;
    private FirebaseDatabase db;
    private DatabaseReference specializations, usersRef;
    private FirebaseRecyclerAdapter<SpecializationCourse, FacultyViewHolder> adapter;
    private RecyclerView specializationCourseRecycler;
    String specializationId = "";
    String specializationName = "";
    private Toolbar mToolbar;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialization_courses);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        db = FirebaseDatabase.getInstance();
        specializations = db.getReference("SpecializationCourses");
        specializations.keepSynced(true);
        specializationId = getIntent().getStringExtra("specializationId");
        specializationName = getIntent().getStringExtra("SpecializationName");

        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);

        mToolbar = (Toolbar)findViewById(R.id.specialCoursesAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(specializationName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        specializationCourseRecycler = (RecyclerView)findViewById(R.id.specializationCoursesRecycler);
        specializationCourseRecycler.setHasFixedSize(true);
        manager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(SpecializationCourses.this) {

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
        specializationCourseRecycler.setLayoutManager(manager);

        adapter = new FirebaseRecyclerAdapter<SpecializationCourse, FacultyViewHolder>(
                SpecializationCourse.class,
                R.layout.faculty_list,
                FacultyViewHolder.class,
                specializations.orderByChild("specializationId").equalTo(specializationId)
        ) {
            @Override
            protected void populateViewHolder(FacultyViewHolder viewHolder, final SpecializationCourse model, int position) {
                viewHolder.facultyName.setText(model.getName());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent dept = new Intent(SpecializationCourses.this, Documents.class);
                        dept.putExtra("CourseId", model.getCourseId());
                        dept.putExtra("CourseName", model.getName());
                        startActivity(dept);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        specializationCourseRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help){

            Intent help = new Intent(SpecializationCourses.this, Help.class);
            startActivity(help);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.currentUser.getMatric() != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
            usersRef.child("online").setValue(false);
        }
    }
}
