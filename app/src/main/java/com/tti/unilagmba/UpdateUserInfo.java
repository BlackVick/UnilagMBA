package com.tti.unilagmba;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class UpdateUserInfo extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference updateData, usersRef;
    private Toolbar mToolbar;
    private MaterialEditText status, name, mail, number, occupation;
    private Button acceptChanges;
    private android.app.AlertDialog mDialog;
    String userSav = "";
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        db = FirebaseDatabase.getInstance();
        updateData = db.getReference().child("User").child(userSav);

        /*----------   KEEP USERS ONLINE   ----------*/
        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);

        mToolbar = (Toolbar)findViewById(R.id.changeInfoAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Your Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("Status");
        String name_value = getIntent().getStringExtra("Name");
        String mail_value = getIntent().getStringExtra("Mail");
        String phone_value = getIntent().getStringExtra("Phone");
        String occupation_value = getIntent().getStringExtra("Occupation");

        status = (MaterialEditText)findViewById(R.id.updateStatusTxt);
        name = (MaterialEditText)findViewById(R.id.updateNameTxt);
        mail = (MaterialEditText)findViewById(R.id.updateMailTxt);
        number = (MaterialEditText)findViewById(R.id.updatePhoneTxt);
        occupation = (MaterialEditText)findViewById(R.id.updateOccupationTxt);

        status.setText(status_value);
        name.setText(name_value);
        mail.setText(mail_value);
        number.setText(phone_value);
        occupation.setText(occupation_value);

        acceptChanges = (Button)findViewById(R.id.acceptInfoChange);
        acceptChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new SpotsDialog(UpdateUserInfo.this, "Updating Details");
                mDialog.show();
                String theStatus = status.getText().toString();
                String theName = name.getText().toString();
                String theMail = mail.getText().toString();
                String thePhone = number.getText().toString();
                String theOccupation = occupation.getText().toString();


                updateData.child("status").setValue(theStatus);
                updateData.child("userName").setValue(theName);
                updateData.child("mail").setValue(theMail);
                updateData.child("number").setValue(thePhone);
                updateData.child("occupation").setValue(theOccupation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(UpdateUserInfo.this, "Info Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateUserInfo.this, "An Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
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

            Intent help = new Intent(UpdateUserInfo.this, Help.class);
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
