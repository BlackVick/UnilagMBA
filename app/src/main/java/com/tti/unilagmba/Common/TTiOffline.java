package com.tti.unilagmba.Common;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Scarecrow on 6/18/2018.
 */

public class TTiOffline extends MultiDexApplication {

    private DatabaseReference users;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        /*-------PICASSO--------*/
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        if (Common.currentUser != null) {
            users = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        users.child("online").onDisconnect().setValue(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}
