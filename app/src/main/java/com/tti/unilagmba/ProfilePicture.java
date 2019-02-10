package com.tti.unilagmba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;

public class ProfilePicture extends AppCompatActivity {

    private ImageView pic;
    String pictureLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        pic = (ImageView)findViewById(R.id.enlargedProfilePic);

        pictureLink = getIntent().getStringExtra("picture");

        if (!pictureLink.equals("")) {
            Picasso.with(getBaseContext())
                    .load(pictureLink)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile)
                    .into(pic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getBaseContext())
                                    .load(pictureLink)
                                    .placeholder(R.drawable.profile)
                                    .into(pic);
                        }
                    });
        }
    }
}
