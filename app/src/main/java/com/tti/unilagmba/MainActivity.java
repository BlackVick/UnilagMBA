package com.tti.unilagmba;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.User;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView fullNameText, matricNumber;
    CircleImageView profileImg;
    private DatabaseReference usersRef;
    String userSav = "";
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard");

        //initialize paper Db
        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        /*---   SET CURRENT USER   ---*/
        if (userSav != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
            usersRef.child("online").setValue(true);

            /*----------   CURRENT USER HANDLER   ----------*/
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String userSav = Paper.book().read(Common.USER_KEY);
                    User user = dataSnapshot.getValue(User.class);
                    user.setMatric(userSav);
                    Common.currentUser = user;

                    final String profilePictureShit = dataSnapshot.child("profilePictureThumb").getValue().toString();

                    fullNameText.setText(dataSnapshot.child("name").getValue().toString());
                    matricNumber.setText(dataSnapshot.getKey().toString());

                    if (profilePictureShit.equalsIgnoreCase("")){


                    } else {
                        Picasso.with(getBaseContext()).load(profilePictureShit).networkPolicy(NetworkPolicy.OFFLINE)
                                .resize(90, 90)
                                .centerCrop()
                                .placeholder(R.drawable.ic_image_black_24dp)
                                .into(profileImg, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getBaseContext())
                                        .load(profilePictureShit)
                                        .placeholder(R.drawable.ic_image_black_24dp)
                                        .into(profileImg);
                            }
                        });

                        profileImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile = new Intent(MainActivity.this, ProfileSetting.class);
                                startActivity(profile);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {

            Intent emergencyLogin = new Intent(MainActivity.this, Login.class);
            startActivity(emergencyLogin);
        }

        /*----------    TABS HANDLER   ----------*/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPager tabsPager = new TabsPager(getSupportFragmentManager());
        viewPager.setAdapter(tabsPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_news_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_materials_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_school_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_organizer_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_chat_black_24dp);

        /*---   NAVIGATION HANDLER   ---*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        fullNameText = (TextView)headerView.findViewById(R.id.userName);
        matricNumber = (TextView)headerView.findViewById(R.id.userMatric);
        profileImg = (CircleImageView)headerView.findViewById(R.id.navProfilePicture);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*----------   KEEP USERS ONLINE   ----------*/
        String userSav = Paper.book().read(Common.USER_KEY);

        if (userSav != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
            usersRef.child("online").setValue(true);
        } else {

            Intent emergencyLogin = new Intent(MainActivity.this, Login.class);
            startActivity(emergencyLogin);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.currentUser != null) {
            if (Common.currentUser.getMatric() != null) {
                usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
                usersRef.child("online").setValue(false);
            }
        } else {
            Intent emergencyLogin = new Intent(MainActivity.this, Login.class);
            startActivity(emergencyLogin);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
            usersRef.child("online").setValue(false);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Touch Again To Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2500);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_update_user_info) {
            Intent profile = new Intent(MainActivity.this, ProfileSetting.class);
            startActivity(profile);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (id == R.id.nav_update_user_password) {
            showChangePasswordDialog();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (id == R.id.nav_student_portal) {
            Intent studentPortal = new Intent(MainActivity.this, StudentPortal.class);
            startActivity(studentPortal);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (id == R.id.nav_update_news_feed) {
            Intent i = new Intent(MainActivity.this, UpdateNewsFeed.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (id == R.id.nav_election) {
            Intent j = new Intent(MainActivity.this, ElectionPortal.class);
            startActivity(j);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        } else if (id == R.id.nav_excos) {

            Intent excos = new Intent(MainActivity.this, UnilagMBAExcos.class);
            startActivity(excos);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.currentUser.getMatric());
            Paper.book().write("sub-user", "false");
            Intent signoutIntent = new Intent(MainActivity.this, Login.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signoutIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (id == R.id.nav_settings) {
            showSettingsDialog();
        } else if (id == R.id.nav_friend_requests){

            Intent req = new Intent(MainActivity.this, FriendRequestActivity.class);
            startActivity(req);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingsDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Settings");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_setting = inflater.inflate(R.layout.setting_subscribe_to_class_news, null);

        final CheckBox ckbSettings = (CheckBox)layout_setting.findViewById(R.id.ckb_sub_news);

        ckbSettings.setText("Subscribe to News Feed");

        //remember state of checkbox

        String isSubscribe = Paper.book().read("sub_new");
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
            ckbSettings.setChecked(false);
        else
            ckbSettings.setChecked(true);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ckbSettings.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);

                    //write value
                    Paper.book().write("sub_new", "true");
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);

                    //write value
                    Paper.book().write("sub_new", "false");
                }
            }
        });

        alertDialog.setView(layout_setting);
        alertDialog.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please Fill In All Information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText password = (MaterialEditText)layout_pwd.findViewById(R.id.changePass_Pass);
        final MaterialEditText newPassword = (MaterialEditText)layout_pwd.findViewById(R.id.changePass_NewPass);
        final MaterialEditText confirmPass = (MaterialEditText)layout_pwd.findViewById(R.id.changePass_ConfirmNewPass);

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Change Password here

                //always use android.app.alertdialog for spotsDialog
                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                //check pass
                if (password.getText().toString().isEmpty() && newPassword.getText().toString().isEmpty() && confirmPass.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (password.getText().toString().equals(Common.currentUser.getPassword())) {
                        if (newPassword.getText().toString().equals(confirmPass.getText().toString())) {

                            Map<String, Object> passwordUpdate = new HashMap<>();
                            passwordUpdate.put("password", newPassword.getText().toString());

                            DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                            user.child(Common.currentUser.getMatric()).updateChildren(passwordUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        } else {
                            waitingDialog.dismiss();
                            Toast.makeText(MainActivity.this, "New Passwords Do not Match :P", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Wrong Password :P", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

}
