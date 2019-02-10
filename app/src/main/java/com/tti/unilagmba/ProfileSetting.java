package com.tti.unilagmba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.paperdb.Paper;

public class ProfileSetting extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference userProfile, usersRef;
    CircleImageView profileImage;
    TextView profileName, profileUsername, profileStatus, profileMail, profileNumber, profileOccupation;
    private static final int GALLERY_REQUEST_CODE = 1;
    private StorageReference mImageStorage;
    private android.app.AlertDialog mDialog;
    Button updateInfo;
    FloatingActionButton changeProfilepic;
    String userSav = "";
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        db = FirebaseDatabase.getInstance();


        /*----------   KEEP USERS ONLINE   ----------*/
        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);

        userProfile = db.getReference().child("User").child(userSav);
        userProfile.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        profileName = (TextView)findViewById(R.id.profileFullName);
        profileUsername = (TextView)findViewById(R.id.profileUsername);
        profileStatus = (TextView)findViewById(R.id.profileStatus);
        profileMail = (TextView)findViewById(R.id.profileMail);
        profileNumber = (TextView)findViewById(R.id.profilePhone);
        profileOccupation = (TextView)findViewById(R.id.profileOccupation);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picEnlarge = new Intent(ProfileSetting.this, ProfilePicture.class);
                picEnlarge.putExtra("picture", currentUser.getProfilePicture());
                startActivity(picEnlarge);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        changeProfilepic = (FloatingActionButton)findViewById(R.id.fabNewProfilePic);
        changeProfilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_intent, "Pick Image"), GALLERY_REQUEST_CODE);
            }
        });

        updateInfo = (Button)findViewById(R.id.updateInfoBtn);
        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent change = new Intent(ProfileSetting.this, UpdateUserInfo.class);
                change.putExtra("Status", currentUser.getStatus());
                change.putExtra("Name", currentUser.getUserName());
                change.putExtra("Mail", currentUser.getMail());
                change.putExtra("Phone", currentUser.getNumber());
                change.putExtra("Occupation", currentUser.getOccupation());
                startActivity(change);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });



        userProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);

                if (currentUser != null) {
                    profileUsername.setText(currentUser.getUserName());
                    profileName.setText(currentUser.getName());
                    profileStatus.setText(currentUser.getStatus());
                    profileMail.setText(currentUser.getMail());
                    profileNumber.setText(currentUser.getNumber());
                    profileOccupation.setText(currentUser.getOccupation());

                    if (!currentUser.getProfilePictureThumb().equalsIgnoreCase("")) {

                        Picasso.with(getBaseContext()).load(currentUser.getProfilePictureThumb())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile)
                                .into(profileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getBaseContext()).load(currentUser.getProfilePictureThumb())
                                                .placeholder(R.drawable.profile)
                                                .into(profileImage);
                                    }
                                });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mDialog = new SpotsDialog(ProfileSetting.this, "Uploading Picture");
                mDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());


                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(150)
                            .setMaxHeight(150)
                            .setQuality(35)
                            .compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 35, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    StorageReference filepath = mImageStorage.child("profile_pictures").child(currentUser.getMatric() + ".jpg");

                    final StorageReference thumbFilepath = mImageStorage.child("profile_picture_thumbs").child(currentUser.getMatric() + ".jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                final String downloadUrl = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();
                                UploadTask uploadTask = thumbFilepath.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        String thumbDownloadUrl = Objects.requireNonNull(thumb_task.getResult().getDownloadUrl()).toString();

                                        if (thumb_task.isSuccessful()){

                                            Map updateHashmap = new HashMap();
                                            updateHashmap.put("profilePicture", downloadUrl);
                                            updateHashmap.put("profilePictureThumb", thumbDownloadUrl);

                                            userProfile.updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mDialog.dismiss();
                                                        Toast.makeText(ProfileSetting.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(ProfileSetting.this, "Error Uploading Thumb", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                            } else {

                                mDialog.dismiss();
                                Toast.makeText(ProfileSetting.this, "Error Uploading", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

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

            Intent help = new Intent(ProfileSetting.this, Help.class);
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
