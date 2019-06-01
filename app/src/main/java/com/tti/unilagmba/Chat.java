package com.tti.unilagmba;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.tti.unilagmba.Model.Friend;
import com.tti.unilagmba.ViewHolder.FriendsViewHolder;

import java.util.Objects;

import io.paperdb.Paper;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {


    private Toolbar mToolbar;
    private FirebaseDatabase db;
    private DatabaseReference friendsRef, user;
    private RecyclerView friendsList;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Friend, FriendsViewHolder> adapter;
    String userSav = "";


    View v;

    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);
        v = inflater.inflate(R.layout.fragment_chat, container, false);

        db = FirebaseDatabase.getInstance();
        Paper.init(Objects.requireNonNull(getContext()));
        userSav = Paper.book().read(Common.USER_KEY);

        if (Common.currentUser != null){
            friendsRef = db.getReference("Friends").child(Common.currentUser.getMatric());
        } else {
            friendsRef = db.getReference("Friends").child(userSav);
        }

        user = db.getReference().child("User");

        friendsList = (RecyclerView)v.findViewById(R.id.friendsRecycler);

        friendsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext()){

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
        friendsList.setLayoutManager(layoutManager);

        loadFriends();

        return v;
    }

    private void loadFriends() {
        adapter = new FirebaseRecyclerAdapter<Friend, FriendsViewHolder>(
                Friend.class,
                R.layout.friends_item,
                FriendsViewHolder.class,
                friendsRef) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friend model, final int position) {

                String list_user_id = getRef(position).getKey();

                user.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String friendUserName = Objects.requireNonNull(dataSnapshot.child("userName").getValue()).toString();
                        String friendStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                        final String friendImage = Objects.requireNonNull(dataSnapshot.child("profilePictureThumb").getValue()).toString();

                        Boolean onlineStat = (Boolean) dataSnapshot.child("online").getValue();

                        if (dataSnapshot.hasChild("online")) {
                            if (onlineStat == true) {
                                viewHolder.onlineStatus.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.onlineStatus.setVisibility(View.INVISIBLE);
                            }
                        }

                        viewHolder.friendsName.setText(friendUserName);
                        viewHolder.friendsStatus.setText(friendStatus);

                        if (!friendImage.equalsIgnoreCase("")) {
                            Picasso.with(getContext()).load(friendImage).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.profile)
                                    .into(viewHolder.friendsPicture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getContext()).load(friendImage)
                                                    .placeholder(R.drawable.profile)
                                                    .into(viewHolder.friendsPicture);
                                        }
                                    });
                        } else {
                            viewHolder.friendsPicture.setImageResource(R.drawable.profile);
                        }

                        viewHolder.friendsPicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent friendsProfile = new Intent(getContext(), UsersProfile.class);
                                friendsProfile.putExtra("user_id", adapter.getRef(position).getKey());
                                startActivity(friendsProfile);
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });

                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent chatPage = new Intent(getContext(), ChatActivity.class);
                                chatPage.putExtra("user_id", adapter.getRef(position).getKey());
                                chatPage.putExtra("user_name", friendUserName);
                                startActivity(chatPage);
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        friendsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_add_friend) {
            Intent friendRequest = new Intent(getContext(), AddFriendActivity.class);
            startActivity(friendRequest);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;

        } else if (id == R.id.action_friend_requests){
            Intent friendRequest = new Intent(getContext(), FriendRequestActivity.class);
            startActivity(friendRequest);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;

        } else if (id == R.id.action_help){

            Intent help = new Intent(getContext(), Help.class);
            startActivity(help);
            return  true;

        }
        return super.onOptionsItemSelected(item);
    }

}
