package com.tti.unilagmba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;

import io.paperdb.Paper;

public class UnilagMBAExcosDetail extends AppCompatActivity {

    private Toolbar mToolbar;
    String adminId = "";
    String adminName = "";
    private FirebaseDatabase db;
    private DatabaseReference facAdmin, usersRef;
    private TextView name, email, phone, position, office;
    private ImageView picture;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unilag_mbaexcos_detail);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);

        if (getIntent() != null) {
            adminId = getIntent().getStringExtra("AdminId");
            adminName = getIntent().getStringExtra("AdminName");
        }

        mToolbar = (Toolbar)findViewById(R.id.unilagExcosAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(adminName);

        picture = (ImageView)findViewById(R.id.adminDetailImage);

        name = (TextView)findViewById(R.id.adminDetailName);
        email = (TextView)findViewById(R.id.adminDetailEmail);
        phone = (TextView)findViewById(R.id.adminDetailPhone);
        position = (TextView)findViewById(R.id.adminDetailPost);
        office = (TextView)findViewById(R.id.adminDetailLocation);

        db = FirebaseDatabase.getInstance();
        facAdmin = db.getReference("UnilagMBAExcos").child(adminId);
        facAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                phone.setText(dataSnapshot.child("phone").getValue().toString());
                position.setText(dataSnapshot.child("post").getValue().toString());
                office.setText(dataSnapshot.child("officeLocation").getValue().toString());

                Picasso.with(getBaseContext()).load(dataSnapshot.child("profilePicture").getValue().toString())
                        .placeholder(R.drawable.profile)
                        .into(picture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

            Intent help = new Intent(UnilagMBAExcosDetail.this, Help.class);
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
            usersRef.child("online").setValue(false);
        }
    }
}
