package com.tti.unilagmba;

import android.*;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.DataMessage;
import com.tti.unilagmba.Model.Material;
import com.tti.unilagmba.Model.MyResponse;
import com.tti.unilagmba.Remote.APIService;
import com.tti.unilagmba.ViewHolder.MaterialsViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Documents extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab, fabAnnounce, fabManualNoti;
    FirebaseDatabase db;
    DatabaseReference materialList, usersRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    String courseId = "";
    String courseNameIntent = "";
    MaterialEditText docName, courseName, announcerName, announce, courseCode;
    EditText materialInfo;
    Button selectBTN, uploadBTN;
    Material newMaterial;
    Uri saveUri;
    RelativeLayout rootLayout;
    FirebaseRecyclerAdapter<Material, MaterialsViewHolder> adapter;
    DownloadManager downloadManager;
    MaterialSpinner spinner;
    private static final int STORAGE_REQUEST_CODE = 9999;
    CardView assignmentPortal;
    APIService mService;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabAnnounce = (FloatingActionButton) findViewById(R.id.fabExam);
        assignmentPortal = (CardView) findViewById(R.id.assignmentCard);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDocumentDialog();
            }
        });

        fabAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnnouncementDialog();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_REQUEST_CODE);
        }

        db = FirebaseDatabase.getInstance();
        materialList = db.getReference("Material");
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

        mService = Common.getFCMService();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_materials);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(Documents.this) {

                    private static final float SPEED = 300f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }

                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        if (Common.isConnectedToInternet(getBaseContext())) {

            if (getIntent() != null) {
                courseId = getIntent().getStringExtra("CourseId");
                courseNameIntent = getIntent().getStringExtra("CourseName");
                getSupportActionBar().setTitle(courseNameIntent);
            }
            if (!courseId.isEmpty())
                loadMaterialList(courseId);
        } else {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }

        assignmentPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assignment = new Intent(Documents.this, PastQuestion.class);
                assignment.putExtra("CourseId", courseId);
                startActivity(assignment);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void showAnnouncementDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Documents.this);
        alertDialog.setTitle("Compose Announcement");
        alertDialog.setMessage("Please Fill In Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_announcement_layout = inflater.inflate(R.layout.send_announcment,null);

        announcerName = (MaterialEditText)add_announcement_layout.findViewById(R.id.lecturerNameTxt);
        announce = (MaterialEditText)add_announcement_layout.findViewById(R.id.announcementTxt);

        alertDialog.setView(add_announcement_layout);
        alertDialog.setIcon(R.drawable.ic_school_black_24dp);

        alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (announcerName.getText().toString().isEmpty() || announce.getText().toString().isEmpty()){
                    Toast.makeText(Documents.this, "No Field Should Be Left Empty", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    sendNotificationAnnounce();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void sendNotification() {
        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "New Material");
        dataSend.put("message", "A New "+ courseName.getText().toString() +" Material Has Been Uploaded");
        dataSend.put("courseId", courseId);
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(courseId).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(Documents.this, "Material Uploaded!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(Documents.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMaterialList(String courseId) {
        adapter = new FirebaseRecyclerAdapter<Material, MaterialsViewHolder>(
                Material.class,
                R.layout.material_list,
                MaterialsViewHolder.class,
                materialList.orderByChild("levelId").equalTo(courseId)
        ) {
            @Override
            protected void populateViewHolder(final MaterialsViewHolder viewHolder, final Material model, int position) {
                viewHolder.materialName.setText(model.getName());
                viewHolder.downloadLink.setText(model.getDocumentLink());
                viewHolder.docType.setText(model.getDocumentType());
                viewHolder.courseName.setText(model.getCourseName());
                viewHolder.materialInfo.setText(model.getMaterialInfo());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(model.getDocumentLink());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        if (model.getDocumentType().equals("0")) {
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".pdf");
                        } else if (model.getDocumentType().equals("1")){
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".doc");
                        } else if (model.getDocumentType().equals("2")){
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".jpg");
                        } else if (model.getDocumentType().equals("3")){
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".xls");
                        } else if (model.getDocumentType().equals("4")){
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".ppt");
                        }
                        Toast.makeText(Documents.this, "Download Started !!!", Toast.LENGTH_SHORT).show();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.allowScanningByMediaScanner();
                        Long reference = downloadManager.enqueue(request);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showAddDocumentDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Documents.this);
        alertDialog.setTitle("Material Upload");
        alertDialog.setMessage("Upload New Material");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_material_layout = inflater.inflate(R.layout.add_material_document,null);

        spinner = (MaterialSpinner)add_material_layout.findViewById(R.id.docTypeSpinner);
        spinner.setItems("PDF", "WORD DOCUMENT", "IMAGE", "EXCEL", "POWER POINT");

        docName = (MaterialEditText) add_material_layout.findViewById(R.id.documentNameTxtAdd);
        courseName = (MaterialEditText) add_material_layout.findViewById(R.id.courseNameTxtAdd);
        materialInfo = (EditText) add_material_layout.findViewById(R.id.materialBriefInfo);
        selectBTN = (Button) add_material_layout.findViewById(R.id.selectBtn);
        uploadBTN = (Button) add_material_layout.findViewById(R.id.uploadBtn);

        selectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        uploadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_material_layout);
        alertDialog.setIcon(R.drawable.ic_materials_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (newMaterial != null){
                    materialList.push().setValue(newMaterial);
                    sendNotification();
                    Snackbar.make(rootLayout, "New Material "+newMaterial.getName()+" Was Added",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if (saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String fileName = UUID.randomUUID().toString();

            final StorageReference imageFolder = storageReference.child("materials/"+fileName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Documents.this, "Uploaded !!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMaterial = new Material();
                                    newMaterial.setName(docName.getText().toString());
                                    newMaterial.setCourseName(courseName.getText().toString());
                                    newMaterial.setDocumentLink(uri.toString());
                                    newMaterial.setLevelId(courseId);
                                    newMaterial.setDocumentType(String.valueOf(spinner.getSelectedIndex()));
                                    newMaterial.setMaterialInfo(materialInfo.getText().toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Documents.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded"+progress+"%");

                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
            selectBTN.setText("File Selected !");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadMaterialList(courseId);
                }
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.class_news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showSettingsDialog(courseId);
            return true;
        } else if (id == R.id.action_help){

            Intent help = new Intent(Documents.this, Help.class);
            startActivity(help);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog(final String levelId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Documents.this);
        alertDialog.setTitle("Settings");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_setting = inflater.inflate(R.layout.setting_subscribe_to_class_news, null);

        final CheckBox ckbSettings = (CheckBox)layout_setting.findViewById(R.id.ckb_sub_news);

        ckbSettings.setText("Subscribe to "+ courseNameIntent +" Department");

        //remember state of checkbox
        Paper.init(this);
        final String topicName = courseNameIntent;
        String isSubscribe = Paper.book().read(topicName);
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
            ckbSettings.setChecked(false);
        else
            ckbSettings.setChecked(true);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ckbSettings.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(levelId);

                    //write value
                    Paper.book().write(topicName, "true");
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(levelId);

                    //write value
                    Paper.book().write(topicName, "false");
                }
            }
        });

        alertDialog.setView(layout_setting);
        alertDialog.show();
    }

    private void sendNotificationAnnounce() {

        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "Announcement From " + " " + announcerName.getText().toString());
        dataSend.put("message", announce.getText().toString());
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(courseId).toString(), dataSend);
        mService.sendNotification(dataMessage)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(Documents.this, "Announcement Sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(Documents.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
