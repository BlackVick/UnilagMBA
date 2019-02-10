package com.tti.unilagmba;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.DataMessage;
import com.tti.unilagmba.Model.MyResponse;
import com.tti.unilagmba.Model.User;
import com.tti.unilagmba.Remote.APIService;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

public class UsersProfile extends AppCompatActivity {

    private ImageView mProfileImageView;
    private TextView mProfileNameText, mProfileStatusText;
    private Button mSendFriendRequestBtn, mDeclineFriendRequestBtn;
    private FirebaseDatabase db;
    private DatabaseReference users, friendRequest, friends, usersRef, chatmsgs, notifications;
    private android.app.AlertDialog mDialog;
    private String mCurrentState;
    private APIService mService;
    String user_name = "";
    String userSav = "";
    User currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        user_name = getIntent().getStringExtra("user_id");

        mService = Common.getFCMService();
        db = FirebaseDatabase.getInstance();
        users = db.getReference().child("User").child(user_name);

        friendRequest = db.getReference().child("FriendRequest");
        friends = db.getReference().child("Friends");
        notifications = db.getReference().child("Notifications");


        /*----------   KEEP USERS ONLINE   ----------*/
        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);


        mProfileImageView = (ImageView)findViewById(R.id.usersProfileImage);
        mProfileNameText = (TextView)findViewById(R.id.usersNameText);
        mProfileStatusText = (TextView)findViewById(R.id.usersStatusText);

        mSendFriendRequestBtn = (Button)findViewById(R.id.sendFriendRequestBtn);
        mDeclineFriendRequestBtn = (Button)findViewById(R.id.declineFriendRequestBtn);


        mCurrentState = "not_friends";


        mDialog = new SpotsDialog(UsersProfile.this, "Getting Details");
        mDialog.show();

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentProfile = dataSnapshot.getValue(User.class);

                mProfileNameText.setText(currentProfile.getUserName());
                mProfileStatusText.setText(currentProfile.getStatus());

                if (!currentProfile.getProfilePicture().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(currentProfile.getProfilePicture()).placeholder(R.drawable.profile).into(mProfileImageView);
                } else {
                    mProfileImageView.setImageResource(R.drawable.profile);
                }

                /*---------------   FRIENDS LIST / REQUEST HANDLER   --------------*/

                friendRequest.child(Common.currentUser.getMatric()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_name)){
                            String request_type = dataSnapshot.child(user_name).child("requestType").getValue().toString();

                            if (request_type.equalsIgnoreCase("received"))
                            {
                                mCurrentState = "request_received";
                                mSendFriendRequestBtn.setText("Accept Request");
                                mDeclineFriendRequestBtn.setVisibility(View.VISIBLE);
                                mDeclineFriendRequestBtn.setEnabled(true);

                            } else if (request_type.equalsIgnoreCase("sent"))
                            {
                                mCurrentState = "request_sent";
                                mSendFriendRequestBtn.setText("Cancel Request");

                                mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                mDeclineFriendRequestBtn.setEnabled(false);

                            }
                            mDialog.dismiss();
                        } else {

                            friends.child(Common.currentUser.getMatric()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_name)){
                                        mCurrentState = "friends";
                                        mSendFriendRequestBtn.setText("Unfriend User");

                                        mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                        mDeclineFriendRequestBtn.setEnabled(false);
                                    }
                                    mDialog.dismiss();
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mDialog.dismiss();
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mDialog.dismiss();
            }
        });

        mSendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                mSendFriendRequestBtn.setEnabled(false);

                /*---------------   NOT FRIENDS HANDLER   --------------*/

                if (mCurrentState.equalsIgnoreCase("not_friends"))
                {

                    friendRequest.child(Common.currentUser.getMatric()).child(user_name).child("requestType").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        friendRequest.child(user_name).child(Common.currentUser.getMatric()).child("requestType").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mCurrentState = "request_sent";
                                                        mSendFriendRequestBtn.setText("Cancel Request");

                                                        mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                                        mDeclineFriendRequestBtn.setEnabled(false);
                                                        sendNotification();



                                                        //Toast.makeText(UsersProfile.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else
                                    {
                                        Toast.makeText(UsersProfile.this, "Failed To Send Request", Toast.LENGTH_SHORT).show();
                                    }

                                    mSendFriendRequestBtn.setEnabled(true);
                                }
                            });
                }

                /*---------------   CANCEL REQUEST HANDLER   --------------*/

                if (mCurrentState.equalsIgnoreCase("request_sent"))
                {
                    friendRequest.child(Common.currentUser.getMatric()).child(user_name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendRequest.child(user_name).child(Common.currentUser.getMatric()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(UsersProfile.this, "Request Cancelled", Toast.LENGTH_SHORT).show();

                                    mSendFriendRequestBtn.setEnabled(true);
                                    mCurrentState = "not_friends";
                                    mSendFriendRequestBtn.setText("Send Friend Request");

                                    mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                    mDeclineFriendRequestBtn.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                /*---------------   REQUEST RECEIVED HANDLER   --------------*/


                if (mCurrentState.equalsIgnoreCase("request_received"))
                {
                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    friends.child(Common.currentUser.getMatric()).child(user_name).child("date").setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friends.child(user_name).child(Common.currentUser.getMatric()).child("date").setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendRequest.child(Common.currentUser.getMatric()).child(user_name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            friendRequest.child(user_name).child(Common.currentUser.getMatric()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //Toast.makeText(UsersProfile.this, "Request Cancelled", Toast.LENGTH_SHORT).show();

                                                                    mSendFriendRequestBtn.setEnabled(true);
                                                                    mCurrentState = "friends";
                                                                    mSendFriendRequestBtn.setText("Unfriend User");

                                                                    mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                                                    mDeclineFriendRequestBtn.setEnabled(false);
                                                                    sendNotificationAccept();

                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });

                                }
                            });
                }

                /*---------------   UNFRIEND USER HANDLER   --------------*/

                if (mCurrentState.equalsIgnoreCase("friends"))
                {
                    friends.child(Common.currentUser.getMatric()).child(user_name).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friends.child(user_name).child(Common.currentUser.getMatric()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mCurrentState = "not_friends";
                                                    mSendFriendRequestBtn.setText("Send Friend Request");

                                                    mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                                    mDeclineFriendRequestBtn.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }
            }
        });

        mDeclineFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequest.child(Common.currentUser.getMatric()).child(user_name).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendRequest.child(user_name).child(Common.currentUser.getMatric()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mCurrentState = "not_friends";
                                                mSendFriendRequestBtn.setText("Send Friend Request");

                                                mDeclineFriendRequestBtn.setVisibility(View.GONE);
                                                mDeclineFriendRequestBtn.setEnabled(false);
                                            }
                                        });
                            }
                        });
            }
        });

    }

    private void sendNotificationAccept() {

        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "New Friend");
        dataSend.put("message", Common.currentUser.getUserName() + " Accepted Your Friend Request");
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(user_name).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new retrofit2.Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(UsersProfile.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sendNotification() {
        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "Friend Request");
        dataSend.put("message", "You Have A New Friend Request From  "+ Common.currentUser.getUserName());
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(user_name).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new retrofit2.Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(UsersProfile.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*----------   KEEP USERS ONLINE   ----------*/
        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
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
