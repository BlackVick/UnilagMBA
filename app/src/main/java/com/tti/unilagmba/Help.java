package com.tti.unilagmba;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

public class Help extends AppCompatActivity {

    private Toolbar mToolbar;
    private LinearLayout faq, contact, appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mToolbar = (Toolbar)findViewById(R.id.helpAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Help");

        faq = (LinearLayout)findViewById(R.id.faq);
        contact = (LinearLayout)findViewById(R.id.contactUs);
        appInfo = (LinearLayout)findViewById(R.id.appInfo);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faq = new Intent(Help.this, Faq.class);
                startActivity(faq);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to = {"simonrileyindustries@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Unilag MBA Support Request!");
                intent.putExtra(Intent.EXTRA_TEXT, "Please Describe Your Enquiry Below, Please Provide Screenshots If Available As Well For Complaints");
                intent.setType("message/rfc822");
                Intent chooser = Intent.createChooser(intent, "Send Request");
                startActivity(chooser);
            }
        });

        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(Help.this, AppInfo.class);
                startActivity(info);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
