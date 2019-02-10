package com.tti.unilagmba;

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
import com.tti.unilagmba.Model.PastQuestions;
import com.tti.unilagmba.Remote.APIService;
import com.tti.unilagmba.ViewHolder.MaterialsViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastQuestion extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab, fabAnnounce, fabManualNoti;
    FirebaseDatabase db;
    DatabaseReference materialList, usersRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    String courseId = "";
    MaterialEditText docName, courseName, lecturerName, announce, courseCode;
    EditText materialInfo;
    Button selectBTN, uploadBTN;
    PastQuestions newMaterial;
    Uri saveUri;
    RelativeLayout rootLayout;
    FirebaseRecyclerAdapter<PastQuestions, MaterialsViewHolder> adapter;
    DownloadManager downloadManager;
    MaterialSpinner spinner;
    private static final int STORAGE_REQUEST_CODE = 9999;
    APIService mService;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDocumentDialog();
            }
        });


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_REQUEST_CODE);
        }

        db = FirebaseDatabase.getInstance();
        materialList = db.getReference("PastQuestions");
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
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(PastQuestion.this) {

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

        if (getIntent() != null) {
            courseId = getIntent().getStringExtra("CourseId");
        }
        if (!courseId.isEmpty())
            loadMaterialList(courseId);

    }

    private void sendNotification() {
        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "New Past Question");
        dataSend.put("message", "A New "+ courseName.getText().toString() +" Past Question Has Been Uploaded");
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(courseId).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(PastQuestion.this, "Past Question Uploaded!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(PastQuestion.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMaterialList(String courseId) {
        adapter = new FirebaseRecyclerAdapter<PastQuestions, MaterialsViewHolder>(
                PastQuestions.class,
                R.layout.material_list,
                MaterialsViewHolder.class,
                materialList.orderByChild("levelId").equalTo(courseId)
        ) {
            @Override
            protected void populateViewHolder(final MaterialsViewHolder viewHolder, final PastQuestions model, int position) {
                viewHolder.materialName.setText(model.getName());
                viewHolder.downloadLink.setText(model.getDocumentLink());
                viewHolder.docType.setText(model.getDocumentType());
                viewHolder.courseName.setText(model.getCourseName());

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
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".xls");
                        } else if (model.getDocumentType().equals("3")){
                            request.setDestinationInExternalPublicDir("/Unilag MBA/Documents/Materials", model.getName() + ".ppt");
                        }
                        Toast.makeText(PastQuestion.this, "Download Started !!!", Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PastQuestion.this);
        alertDialog.setTitle("Past Question Upload");
        alertDialog.setMessage("Upload New Past Question");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_material_layout = inflater.inflate(R.layout.add_past_questions,null);

        spinner = (MaterialSpinner)add_material_layout.findViewById(R.id.docTypeSpinner);
        spinner.setItems("PDF", "WORD DOCUMENT", "EXCEL", "POWER POINT");

        docName = (MaterialEditText) add_material_layout.findViewById(R.id.documentNameTxtAdd);
        courseName = (MaterialEditText) add_material_layout.findViewById(R.id.courseNameTxtAdd);
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
                            Toast.makeText(PastQuestion.this, "Uploaded !!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMaterial = new PastQuestions();
                                    newMaterial.setName(docName.getText().toString());
                                    newMaterial.setCourseName(courseName.getText().toString());
                                    newMaterial.setDocumentLink(uri.toString());
                                    newMaterial.setLevelId(courseId);
                                    newMaterial.setDocumentType(String.valueOf(spinner.getSelectedIndex()));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(PastQuestion.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
