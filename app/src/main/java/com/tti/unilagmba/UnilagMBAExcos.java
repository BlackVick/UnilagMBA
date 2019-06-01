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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.FacultyAdminModel;
import com.tti.unilagmba.ViewHolder.FacultyAdminViewHolder;

import io.paperdb.Paper;

public class UnilagMBAExcos extends AppCompatActivity {

    private Toolbar mToolbar;
    TextView infoText;
    FirebaseDatabase db;
    DatabaseReference usersRef, mbaExcos;
    RecyclerView adminRecycler;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<FacultyAdminModel, FacultyAdminViewHolder> adapter;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unilag_mbaexcos);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);


        loadDepartmentAdmins();

        mToolbar = (Toolbar)findViewById(R.id.unilagExcosAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MBA Excos");
    }

    private void loadDepartmentAdmins() {
        db = FirebaseDatabase.getInstance();
        mbaExcos = db.getReference("UnilagMBAExcos");

        adminRecycler = (RecyclerView)findViewById(R.id.departmentAdminRecycler);
        adminRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(UnilagMBAExcos.this) {

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
        adminRecycler.setLayoutManager(layoutManager);

        adapter = new FirebaseRecyclerAdapter<FacultyAdminModel, FacultyAdminViewHolder>(
                FacultyAdminModel.class,
                R.layout.faculty_admin_item,
                FacultyAdminViewHolder.class,
                mbaExcos
        ) {
            @Override
            protected void populateViewHolder(final FacultyAdminViewHolder viewHolder, final FacultyAdminModel model, int position) {

                viewHolder.adminName.setText(model.getName());
                viewHolder.adminPost.setText(model.getPost());
                if (!model.getProfilePictureThumb().equalsIgnoreCase("")) {
                    Picasso.with(getBaseContext()).load(model.getProfilePictureThumb())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.adminImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getBaseContext()).load(model.getProfilePictureThumb())
                                            .placeholder(R.drawable.profile)
                                            .into(viewHolder.adminImage);
                                }
                            });
                }


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent adminDetail = new Intent(UnilagMBAExcos.this, UnilagMBAExcosDetail.class);
                        adminDetail.putExtra("AdminId", adapter.getRef(position).getKey());
                        adminDetail.putExtra("AdminName", model.getName());
                        startActivity(adminDetail);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });

            }
        };
        adminRecycler.setAdapter(adapter);
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

            Intent help = new Intent(UnilagMBAExcos.this, Help.class);
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
