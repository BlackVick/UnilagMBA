package com.tti.unilagmba;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Login extends android.app.Activity {

    private FirebaseDatabase db;
    private DatabaseReference users;
    private EditText matric, password;
    private Button login, register;
    private TextView recovery, secureCodeRecovery;
    private ImageView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*--- DEFINING ALL VARIABLES   ---*/
        matric = (EditText) findViewById(R.id.loginMatricEdt);
        password = (EditText) findViewById(R.id.loginPasswordEdt);
        login = (Button) findViewById(R.id.loginBtn);
        register = (Button) findViewById(R.id.registerBtn);
        recovery = (TextView) findViewById(R.id.passwordRecoveryTxt);
        secureCodeRecovery = (TextView) findViewById(R.id.secureCodeRecoveryTxt);
        help = (ImageView)findViewById(R.id.helpButton);

        /*---   INITIALIZING ALL DATABASES   ---*/
        Paper.init(this);
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        /*---   READING INFO FROM PAPER DB    ---*/
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        /*---   SKIPPING LOGIN  IF ALREADY LOGGED IN   ---*/
        if (user != null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty()) {

                jumpInto();
            }
        }

        /*---   GETTING ANSWERS TO FAQs   ---*/
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(Login.this, Help.class);
                startActivity(help);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        /*---   LOG USER IN   ---*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    final android.app.AlertDialog mDialog = new SpotsDialog(Login.this);
                    mDialog.show();

                    /*---   VALIDATION OF TEXTBOXES   ---*/
                    if (matric.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                        mDialog.dismiss();
                        Toast.makeText(Login.this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
                    } else {
                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(matric.getText().toString()).exists()) {
                                    mDialog.dismiss();

                                    final User user = dataSnapshot.child(matric.getText().toString()).getValue(User.class);
                                    user.setMatric(matric.getText().toString());

                                    if (user.getPassword().equals(password.getText().toString())) {
                                        Toast.makeText(Login.this, "Sign In Successful !", Toast.LENGTH_SHORT).show();

                                        /*---   WRITE DETAILS INTO PAPER DB   ---*/
                                        Paper.book().write(Common.USER_KEY, matric.getText().toString());
                                        Paper.book().write(Common.PWD_KEY, password.getText().toString());

                                        /*---   TRANSFER TO DASHBOARD ACTIVITY   ---*/
                                        Intent logIn = new Intent(Login.this, MainActivity.class);
                                        Common.currentUser = user;
                                        FirebaseMessaging.getInstance().subscribeToTopic(Common.currentUser.getMatric());
                                        Paper.book().write("sub-user", "true");
                                        startActivity(logIn);
                                        finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                        /*---   REMOVE LISTENER   ---*/
                                        users.removeEventListener(this);
                                    } else {
                                        Toast.makeText(Login.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(Login.this, "User Does Not Exist in Database", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else
                {
                    Toast.makeText(Login.this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        /*---   REGISTER NEW VALID USERS   ---*/
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(Login.this, Registration.class);
                startActivity(register);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        /*---   RECOVER FORGOTTEN PASSWORD   ---*/
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        /*---   RECOVER FORGOTTEN SECURE CODE   ---*/
        secureCodeRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecureCodeRecoveryDialog();
            }
        });

    }

    private void jumpInto() {
        Intent jumpInto = new Intent(Login.this, MainActivity.class);
        startActivity(jumpInto);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void showSecureCodeRecoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Secure Code");
        builder.setMessage("Enter The Following Detail");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.recover_secure_code, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final EditText fullName = (EditText)forgot_view.findViewById(R.id.secureCodeFullNameEdt);
        final EditText matricNumber = (EditText)forgot_view.findViewById(R.id.secureCodeMatricNumEdt);
        final EditText username = (EditText)forgot_view.findViewById(R.id.secureCodeUsernameEdt);

        /*---   SEND REQUEST MAIL TO ADMIN   ---*/
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to = {"simonrileyindustries@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Unilag MBA Association Secure Code Recovery Request!");
                intent.putExtra(Intent.EXTRA_TEXT, "My Full Name is "+fullName.getText().toString()+". \n"+
                        "My Matriculation Number is "+matricNumber.getText().toString()+". \n"+
                        "My Username is "+username.getText().toString()+".");
                intent.setType("message/rfc822");
                Intent chooser = Intent.createChooser(intent, "Send Request");
                startActivity(chooser);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter Your Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.recover_password, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final EditText matric = (EditText)forgot_view.findViewById(R.id.forgotMatricEdt);
        final EditText secure = (EditText)forgot_view.findViewById(R.id.confirmSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(matric.getText().toString()).getValue(User.class);

                        if (matric.getText().toString().isEmpty() || secure.getText().toString().isEmpty()) {

                            Toast.makeText(Login.this, "Please Enter Valid Information !", Toast.LENGTH_SHORT).show();
                        }
                        else if (dataSnapshot.child(matric.getText().toString()).exists()){
                            if (user.getSecureCode().equals(secure.getText().toString())) {
                                Toast.makeText(Login.this, "Your Password : " + user.getPassword(), Toast.LENGTH_LONG).show();
                                users.removeEventListener(this);
                            }
                            else
                                Toast.makeText(Login.this, "Wrong Secure Code", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(Login.this, "Number Not Exist", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Login.this, "Process Cancelled Internally", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
