package com.tti.unilagmba;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.CommentMessage;
import com.tti.unilagmba.Model.Events;
import com.tti.unilagmba.Model.MissingItems;
import com.tti.unilagmba.Model.NewsFeeds;
import com.tti.unilagmba.ViewHolder.CommentViewHolder;

import java.text.SimpleDateFormat;

import io.paperdb.Paper;

public class NewsDetails extends AppCompatActivity {

    TextView newsTitle, newsDetails, newsTime;
    ImageView newsImage;
    EditText commentBox;
    String newsId = "";
    NewsFeeds currentNews;
    CommentMessage newComment;
    FirebaseDatabase db;
    DatabaseReference newsRef, usersRef;
    FirebaseRecyclerAdapter<CommentMessage, CommentViewHolder> adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        ImageView fab = (ImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });

        newsTitle = (TextView) findViewById(R.id.titleOfNews);
        newsDetails = (TextView) findViewById(R.id.newsDetailsFull);
        newsTime = (TextView) findViewById(R.id.timeOfNews);
        newsImage = (ImageView) findViewById(R.id.imageOfNews);

        recyclerView = (RecyclerView)findViewById(R.id.commentList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(NewsDetails.this) {

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
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseDatabase.getInstance();
        newsRef = db.getReference("NewsFeeds");

        /*----------   KEEP USERS ONLINE   ----------*/
        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        usersRef.child("online").setValue(true);

        if (getIntent() != null){
            newsId = getIntent().getStringExtra("NewsId");
        }

        if (!newsId.isEmpty()) {
            getNewsDetails(newsId);
            loadComments(newsId);

        }
    }

    private void sendComment() {
        final DatabaseReference commentz = db.getReference("CommentMessage");
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm");
        final String dateString = sdf.format(date);

        commentBox = (EditText) findViewById(R.id.enterCommentTxt);
        newComment = new CommentMessage();
        newComment.setUser(Common.currentUser.getUserName());
        newComment.setUserMatric(Common.currentUser.getMatric());
        newComment.setCommentTime(dateString);
        newComment.setComment(commentBox.getText().toString());
        newComment.setNewsId(newsId);
        if (commentBox.getText().toString().isEmpty()){
            Toast.makeText(this, "Sorry, Empty Comment Cant be Sent \ud83d\ude01", Toast.LENGTH_SHORT).show();
        } else if (newComment != null){
            commentz.push().setValue(newComment);
            commentBox.setText(null);
        }
    }

    private void loadComments(String newsId) {
        final DatabaseReference commentz = db.getReference("CommentMessage");

        adapter = new FirebaseRecyclerAdapter<CommentMessage, CommentViewHolder>(
                CommentMessage.class,
                R.layout.comment_item,
                CommentViewHolder.class,
                commentz.orderByChild("newsId").equalTo(newsId)
        ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder viewHolder, final CommentMessage model, int position) {
                viewHolder.username.setText(model.getUser());
                viewHolder.time.setText(model.getCommentTime());
                viewHolder.comment.setText(model.getComment());

                viewHolder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (model.getUserMatric().equalsIgnoreCase(Common.currentUser.getMatric())){
                            Intent userProf = new Intent(NewsDetails.this, ProfileSetting.class);
                            startActivity(userProf);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        } else {
                            Intent userProf = new Intent(NewsDetails.this, UsersProfile.class);
                            userProf.putExtra("user_id", model.getUserMatric());
                            startActivity(userProf);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void getNewsDetails(String newsId) {
        newsRef.child(newsId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentNews = dataSnapshot.getValue(NewsFeeds.class);

                if (!currentNews.getNewsImage().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext())
                            .load(currentNews.getNewsImage())
                            .placeholder(R.drawable.unilag_logo)
                            .into(newsImage);
                } else {

                    newsImage.setImageResource(R.drawable.unilag_logo);

                }


                newsTitle.setText(currentNews.getNewsTitle());
                newsTime.setText(currentNews.getTime());
                newsDetails.setText(currentNews.getNewsDetail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
