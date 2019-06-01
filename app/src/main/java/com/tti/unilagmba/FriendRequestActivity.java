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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.DataMessage;
import com.tti.unilagmba.Model.FriendRequest;
import com.tti.unilagmba.Model.MyResponse;
import com.tti.unilagmba.Remote.APIService;
import com.tti.unilagmba.ViewHolder.FriendRequestViewHolder;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

public class FriendRequestActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseDatabase db;
    private DatabaseReference friendRequestRef, user, friends, friendRequestAcceptorDecline, usersRef;
    private RecyclerView friendRequests;
    private LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder> adapter;
    private APIService mService;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mToolbar = (Toolbar)findViewById(R.id.friendsRequestAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Friend Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        mService = Common.getFCMService();

        db = FirebaseDatabase.getInstance();
        friendRequestRef = db.getReference().child("FriendRequest").child(Common.currentUser.getMatric());
        user = db.getReference().child("User");

        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);

        friendRequests = (RecyclerView)findViewById(R.id.friendRequestRecycler);

        friendRequests.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(FriendRequestActivity.this) {

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
        friendRequests.setLayoutManager(layoutManager);

        loadFriends();
    }

    private void loadFriends() {

        adapter = new FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder>(
                FriendRequest.class,
                R.layout.friend_request_layout,
                FriendRequestViewHolder.class,
                friendRequestRef) {
            @Override
            protected void populateViewHolder(final FriendRequestViewHolder viewHolder, FriendRequest model, int position) {

                final String friend_id = adapter.getRef(position).getKey();

                friendRequestRef.child(friend_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String reqType = dataSnapshot.child("requestType").getValue().toString();

                                if (reqType.equalsIgnoreCase("received")){
                                    user.child(friend_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String friend_username = dataSnapshot.child("userName").getValue().toString();
                                            final String friend_userImage = dataSnapshot.child("profilePictureThumb").getValue().toString();

                                            viewHolder.friendsName.setText(friend_username);

                                            if (friend_userImage.equalsIgnoreCase("")){



                                            } else {
                                                Picasso.with(getBaseContext()).load(friend_userImage)
                                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                                        .placeholder(R.drawable.profile)
                                                        .into(viewHolder.friendsPicture,
                                                                new Callback() {
                                                                    @Override
                                                                    public void onSuccess() {

                                                                    }

                                                                    @Override
                                                                    public void onError() {
                                                                        Picasso.with(getBaseContext()).load(friend_userImage)
                                                                                .placeholder(R.drawable.profile)
                                                                                .into(viewHolder.friendsPicture);
                                                                    }
                                                                });
                                            }

                                            viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    friendRequestAcceptorDecline = db.getReference("FriendRequest");
                                                    friends = db.getReference().child("Friends");
                                                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                                                    friends.child(Common.currentUser.getMatric()).child(friend_id).child("date").setValue(currentDate)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    friends.child(friend_id).child(Common.currentUser.getMatric()).child("date").setValue(currentDate)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    friendRequestAcceptorDecline.child(Common.currentUser.getMatric()).child(friend_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            friendRequestAcceptorDecline.child(friend_id).child(Common.currentUser.getMatric()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    Toast.makeText(FriendRequestActivity.this, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                                                                                                    sendNotification(friend_id);

                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });

                                                                }
                                                            });
                                                }
                                            });

                                            viewHolder.decline.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    friendRequestAcceptorDecline = db.getReference("FriendRequest");
                                                    friendRequestAcceptorDecline.child(Common.currentUser.getMatric()).child(friend_id).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    friendRequestAcceptorDecline.child(friend_id).child(Common.currentUser.getMatric()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(FriendRequestActivity.this, "Friend Request Declined", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    viewHolder.friendCard.setVisibility(View.GONE);
                                }

                                friendRequestRef.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent newFriRwq = new Intent(FriendRequestActivity.this, UsersProfile.class);
                        newFriRwq.putExtra("user_id", friend_id);
                        startActivity(newFriRwq);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        friendRequests.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sendNotification(String friend_id) {
        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "New Friend");
        dataSend.put("message", Common.currentUser.getUserName() + " Accepted Your Friend Request");
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(friend_id).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new retrofit2.Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(FriendRequestActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

            Intent help = new Intent(FriendRequestActivity.this, Help.class);
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
