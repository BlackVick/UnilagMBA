package com.tti.unilagmba;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Database.Database;
import com.tti.unilagmba.Model.Todo;
import com.tti.unilagmba.ViewHolder.TodoAdapter;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class DayDetail extends AppCompatActivity {

    MaterialEditText courseCode, venue, start, end;
    String day = "";
    List<Todo> todo = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    TodoAdapter adapter;
    DatabaseReference usersRef;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        recyclerView = (RecyclerView)findViewById(R.id.dayScheduleList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(DayDetail.this) {

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
        recyclerView.setLayoutManager(layoutManager);

        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);

        if (getIntent() != null) {
            day = getIntent().getStringExtra("Day");
            getSupportActionBar().setTitle(day);
        }
        if (!day.isEmpty()) {
            loadTodoList();
        } else {
            Toast.makeText(this, "Happy Day", Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTodoBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAddPlanDialog();

            }
        });
    }

    private void loadTodoList() {
        todo = new Database(this).getTodo(day);
        adapter = new TodoAdapter(todo, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

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

    private void openAddPlanDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayDetail.this);
        alertDialog.setTitle("Add New Task");
        alertDialog.setMessage("Fill All Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_lecture_layout = inflater.inflate(R.layout.add_new_lecture,null);

        courseCode = (MaterialEditText)add_lecture_layout.findViewById(R.id.todoCourseCodeEdt);
        venue = (MaterialEditText)add_lecture_layout.findViewById(R.id.todoLectureVenueEdt);
        start = (MaterialEditText)add_lecture_layout.findViewById(R.id.todoStartTimeEdt);
        end = (MaterialEditText)add_lecture_layout.findViewById(R.id.todoStopTimeEdt);

        alertDialog.setView(add_lecture_layout);
        alertDialog.setIcon(R.drawable.ic_school_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (courseCode.getText().toString().isEmpty() || venue.getText().toString().isEmpty() || start.getText().toString().isEmpty() || end.getText().toString().isEmpty()){
                    Toast.makeText(DayDetail.this, "Please Enter your Full Schedule", Toast.LENGTH_SHORT).show();
                } else {
                    new Database(getBaseContext()).addToTodo(new Todo(
                            day,
                            courseCode.getText().toString(),
                            venue.getText().toString(),
                            start.getText().toString(),
                            end.getText().toString()
                    ));
                    loadTodoList();
                    Toast.makeText(DayDetail.this, "New Todo Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteTodo(item.getOrder());
        return true;
    }

    private void deleteTodo(int position) {
        //remove item by position
        todo.remove(position);
        //remove previous data from sqlite
        new Database(this).cleanTodo(day);
        //update new data list
        for (Todo item:todo)
            new Database(this).addToTodo(item);
        //refresh food list
        loadTodoList();
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

            Intent help = new Intent(DayDetail.this, Help.class);
            startActivity(help);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
