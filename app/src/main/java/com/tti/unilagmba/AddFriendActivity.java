package com.tti.unilagmba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.User;
import com.tti.unilagmba.ViewHolder.SearchViewHolder;

import io.paperdb.Paper;

public class AddFriendActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseDatabase db;
    private DatabaseReference users, usersRef;
    private RecyclerView searchList;
    private LinearLayoutManager layoutManager;
    private EditText searchBox;
    private ImageView searchButton;
    private FirebaseRecyclerAdapter<User, SearchViewHolder> adapter;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mToolbar = (Toolbar)findViewById(R.id.addFriendAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        /*----------   KEEP USERS ONLINE   ----------*/
        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        usersRef.child("online").setValue(true);

        searchBox = (EditText)findViewById(R.id.searchBox);
        searchButton = (ImageView)findViewById(R.id.searchButton);

        searchList = (RecyclerView)findViewById(R.id.searchRecycler);
        searchList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(AddFriendActivity.this) {

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
        searchList.setLayoutManager(layoutManager);

        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchTxt = searchBox.getText().toString();

                    if (!searchTxt.equalsIgnoreCase("")){

                        searchForUser(searchTxt);
                        return true;

                    } else {
                        Toast.makeText(AddFriendActivity.this, "Cant Process Empty Query", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchBox.getText().toString().isEmpty()){
                    Toast.makeText(AddFriendActivity.this, "Cant Process An Empty Query", Toast.LENGTH_SHORT).show();
                } else {

                    String searchTxt = searchBox.getText().toString();
                    searchForUser(searchTxt);
                }
            }
        });

    }

    private void searchForUser(String searchTxt) {

        Query firebaseSearchQuery = users.orderByChild("userName").startAt(searchTxt).endAt(searchTxt + "\uf8ff");

        adapter = new FirebaseRecyclerAdapter<User, SearchViewHolder>(User.class, R.layout.search_list_layout, SearchViewHolder.class, firebaseSearchQuery) {
            @Override
            protected void populateViewHolder(SearchViewHolder viewHolder, User model, int position) {

                viewHolder.searchResultName.setText(model.getUserName());

                if (model.getProfilePictureThumb().equalsIgnoreCase("")){



                } else {
                    Picasso.with(getBaseContext()).load(model.getProfilePictureThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.searchResultPicture);

                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if (adapter.getRef(position).getKey().equalsIgnoreCase(Common.currentUser.getMatric())){
                            Intent friendsProfile = new Intent(AddFriendActivity.this, ProfileSetting.class);
                            friendsProfile.putExtra("user_id", adapter.getRef(position).getKey());
                            startActivity(friendsProfile);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Intent friendsProfile = new Intent(AddFriendActivity.this, UsersProfile.class);
                            friendsProfile.putExtra("user_id", adapter.getRef(position).getKey());
                            startActivity(friendsProfile);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }
                });

            }
        };
        searchList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help){

            Intent help = new Intent(AddFriendActivity.this, Help.class);
            startActivity(help);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
