package com.tti.unilagmba;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.User;

import dmax.dialog.SpotsDialog;

public class Registration extends AppCompatActivity {

    private MaterialEditText matricEdt, nameEdt, username, passwordEdt, repeatPassEdt, secureCode, mail, number, occupation;
    private Button registerBtn;
    private TextView privacyShit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        /*---   INITIALIZING WIDGETS   ---*/
        matricEdt = (MaterialEditText)findViewById(R.id.newMatricEdt);
        nameEdt = (MaterialEditText)findViewById(R.id.newNameEdt);
        username = (MaterialEditText)findViewById(R.id.newUserNameEdt);
        passwordEdt = (MaterialEditText)findViewById(R.id.newPasswordEdt);
        secureCode = (MaterialEditText)findViewById(R.id.secureCodeEdt);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        repeatPassEdt = (MaterialEditText)findViewById(R.id.newPasswordRepeatEdt);
        mail = (MaterialEditText)findViewById(R.id.newUserMailEdt);
        number = (MaterialEditText)findViewById(R.id.newUserPhoneEdt);
        occupation = (MaterialEditText)findViewById(R.id.newUserOccupationEdt);
        privacyShit = (TextView)findViewById(R.id.privacy_policy);


        /*---   PRIVACY SHIIIT   ---*/
        String text = "By clicking on Register, You are confirming your acceptance of our PRIVACY POLICY.";
        SpannableString ss = new SpannableString(text);
        ClickableSpan policy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                String policyUrl = "http://www.teenqtech.com/customer-service-privacy-policy";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(policyUrl));
                startActivity(i);

            }
        };
        ss.setSpan(policy, 67, 81, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyShit.setText(ss);
        privacyShit.setMovementMethod(LinkMovementMethod.getInstance());


        /*---   INITIALIZING DATABASE   ---*/
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check for internet connection
                if (Common.isConnectedToInternet(getBaseContext())) {

                    //dialog showing progress
                    final android.app.AlertDialog mdialog = new SpotsDialog(Registration.this);
                    mdialog.show();

                    //first check if the text boxes are empty.
                    if (matricEdt.getText().toString().isEmpty() ||
                            nameEdt.getText().toString().isEmpty() ||
                            mail.getText().toString().isEmpty() ||
                            number.getText().toString().isEmpty() ||
                            occupation.getText().toString().isEmpty() ||
                            username.getText().toString().isEmpty() ||
                            passwordEdt.getText().toString().isEmpty() ||
                            secureCode.getText().toString().isEmpty()) {
                        mdialog.dismiss();
                        Toast.makeText(Registration.this, "Enter Valid Information", Toast.LENGTH_SHORT).show();
                    } else
                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Check if user already in Database
                                if (dataSnapshot.child(matricEdt.getText().toString()).exists()) {
                                    mdialog.dismiss();
                                    Toast.makeText(Registration.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                                } else if (passwordEdt.getText().toString().equals(repeatPassEdt.getText().toString())) {


                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    final DatabaseReference validate_user = database.getReference("ValidUsers");

                                    validate_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child(matricEdt.getText().toString()).exists()){

                                                mdialog.dismiss();
                                                User user = new User(nameEdt.getText().toString(),
                                                        username.getText().toString(),
                                                        mail.getText().toString(),
                                                        number.getText().toString(),
                                                        occupation.getText().toString(),
                                                        "",
                                                        "",
                                                        "I Am New To The Unilag MBA Association App",
                                                        passwordEdt.getText().toString(),
                                                        secureCode.getText().toString(),
                                                        "student");
                                                table_user.child(matricEdt.getText().toString()).setValue(user);

                                                Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                Intent signUpIntent = new Intent(Registration.this, Login.class);
                                                startActivity(signUpIntent);
                                                finish();
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                            } else {

                                                Toast.makeText(Registration.this, "You Have Not Been Verified", Toast.LENGTH_SHORT).show();
                                                mdialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else {
                                    mdialog.dismiss();
                                    Toast.makeText(Registration.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(Registration.this, "Process Cancelled Internally", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else
                {
                    Toast.makeText(Registration.this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
