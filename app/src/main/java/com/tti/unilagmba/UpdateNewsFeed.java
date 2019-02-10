package com.tti.unilagmba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.DataMessage;
import com.tti.unilagmba.Model.MyResponse;
import com.tti.unilagmba.Model.NewsFeeds;
import com.tti.unilagmba.Remote.APIService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateNewsFeed extends AppCompatActivity {

    MaterialEditText topic;
    EditText broadcast;
    Button sendBc;
    ImageButton imageUpload;
    FirebaseDatabase db;
    DatabaseReference news, missing, events, usersRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    NewsFeeds newNews;
    Uri imageUri;
    MaterialSpinner spinner;
    private final static int GALLERY_REQUEST_CODE = 2;
    private android.app.AlertDialog mDialog;
    APIService mService;
    String userSav = "";

    static String[] words = {"fuck", "fukk", "fucks", "fucker", "fucking", "fuckin", "fu*k", "phuck", "motherfucker", "muthafuka", "muthafucka",
            "shit", "sh*t", "bullshit", "bullcrap",
            "arse", "asshole", "arsehole", "bitch", "nigga", "bitchassnigga", "punani", "punk", "puni", "pucci", "pussy", "toto",
            "blokus", "puci", "naked", "blowjob", "@$$", "booty", "boobs", "dick", "clit"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_news_feed);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        mService = Common.getFCMService();
        db = FirebaseDatabase.getInstance();
        news = db.getReference("NewsFeeds");
        missing = db.getReference("MissingItems");
        events = db.getReference("Events");

        spinner = (MaterialSpinner)findViewById(R.id.newsCategorySpinner);
        spinner.setItems("Main News Feed", "Missing Items", "Events");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);
        usersRef.keepSynced(true);

        topic = (MaterialEditText)findViewById(R.id.newsTopic);
        broadcast = (EditText)findViewById(R.id.newsDetails);
        sendBc = (Button)findViewById(R.id.updateNewsFeedBtn);
        imageUpload = (ImageButton)findViewById(R.id.uploadNewsImageButton);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topic.getText().toString().isEmpty() || broadcast.getText().toString().isEmpty() || String.valueOf(spinner.getSelectedIndex()).isEmpty()) {
                    Toast.makeText(UpdateNewsFeed.this, "Please Fill The Text Fields", Toast.LENGTH_SHORT).show();
                } else
                {
                    Intent gallery_intent = new Intent();
                    gallery_intent.setType("image/*");
                    gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery_intent, "Pick Image"), GALLERY_REQUEST_CODE);
                }
            }
        });


        sendBc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newNews != null && String.valueOf(spinner.getSelectedIndex()).equals("0")){
                    news.push().setValue(newNews);
                    sendNotification();
                    Intent clear = new Intent(UpdateNewsFeed.this, MainActivity.class);
                    startActivity(clear);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                } else if (newNews != null && String.valueOf(spinner.getSelectedIndex()).equals("1")){
                    missing.push().setValue(newNews);
                    sendNotification();
                    Intent clear = new Intent(UpdateNewsFeed.this, MainActivity.class);
                    startActivity(clear);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                } else if (newNews != null && String.valueOf(spinner.getSelectedIndex()).equals("2")){
                    events.push().setValue(newNews);
                    sendNotification();
                    Intent clear = new Intent(UpdateNewsFeed.this, MainActivity.class);
                    startActivity(clear);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

    }

    public static String censor(String input) {
        StringBuilder s = new StringBuilder(input);
        for (int i = 0; i < input.length(); i++) {
            for (String word : words) {
                try {
                    if (input.substring(i, word.length() + i).equalsIgnoreCase(word)) {
                        for (int j = i; j < i + word.length(); j++) {
                            s.setCharAt(j, '*');
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return s.toString();
    }

    private void sendNotification() {

        final String censoredTopic = censor(topic.getText().toString());
        final String censoredBroadcast = censor(broadcast.getText().toString());

        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "News Alert");
        dataSend.put("message", censoredTopic);
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(Common.topicName).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(UpdateNewsFeed.this, "Broadcast Sent!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(UpdateNewsFeed.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK)
        {

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mDialog = new SpotsDialog(UpdateNewsFeed.this, "Uploading Picture");
                mDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setQuality(40)
                            .compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    final StorageReference thumbFilepath = storageReference.child("newsfeedimages").child(censor(topic.getText().toString()) + ".jpg");

                    thumbFilepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                UploadTask uploadTask = thumbFilepath.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        imageUpload.setImageResource(R.drawable.ic_uploaded_shit);

                                        String thumbDownloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                        if (thumb_task.isSuccessful()){

                                            long date = System.currentTimeMillis();

                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                                            final String dateString = sdf.format(date);
                                            final String censoredTopic = censor(topic.getText().toString());
                                            final String censoredBroadcast = censor(broadcast.getText().toString());

                                            newNews = new NewsFeeds(censoredTopic, Common.currentUser.getUserName(), censoredBroadcast, thumbDownloadUrl, dateString, String.valueOf(spinner.getSelectedIndex()));

                                            mDialog.dismiss();

                                        } else {
                                            mDialog.dismiss();
                                            imageUpload.setImageResource(R.drawable.ic_image_black_24dp);
                                            Toast.makeText(UpdateNewsFeed.this, "Error Uploading Thumb", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {

                                mDialog.dismiss();
                                Toast.makeText(UpdateNewsFeed.this, "Error Uploading", Toast.LENGTH_SHORT).show();

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
