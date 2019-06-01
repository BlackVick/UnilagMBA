package com.tti.unilagmba;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Model.DataMessage;
import com.tti.unilagmba.Model.Message;
import com.tti.unilagmba.Model.MyResponse;
import com.tti.unilagmba.Remote.APIService;
import com.tti.unilagmba.ViewHolder.ChatMessageViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    public FirebaseDatabase db;
    public DatabaseReference currentFriendRef, messageRef2, usersRef, messageRef, rootRef, chatMessagesRef,  chatMessagesRef2, chatMessagesRef3, checkMessageRef;
    public TextView friendsName;
    private CircleImageView friendsProfilPicture;
    private ImageButton sendMessage, addAttachment;
    private ImageView onlinePresenceCheck;
    EditText chatBox;
    String user_id = "";
    String user_name = "";
    RecyclerView messagesRecycler;
    SwipeRefreshLayout refreshChats;
    private LinearLayoutManager mLinearLayoutManager;
    private final List<Message> messageList = new ArrayList<>();
    FirebaseRecyclerAdapter<Message, ChatMessageViewHolder> adapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 15;
    private int mCurrentPage = 1;
    private static final int GALLERY_REQUEST_CODE = 1;
    FirebaseStorage storage;
    StorageReference storageReference;
    private android.app.AlertDialog mDialog;
    private APIService mService;
    Uri imageUri;
    DownloadManager downloadManager;
    private static final int STORAGE_REQUEST_CODE = 9999;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar)findViewById(R.id.chatAppBar);
        setSupportActionBar(mToolbar);

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mService = Common.getFCMService();
        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        /*----------   KEEP USERS ONLINE   ----------*/
        if (Common.currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
        } else {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
        }
        usersRef.child("online").setValue(true);

        /*-----------   CUSTOM ACTION BAR STUFF   ----------*/

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_REQUEST_CODE);
        }

        chatBox = (EditText)findViewById(R.id.chatEdt);
        sendMessage = (ImageButton) findViewById(R.id.sendMessageBtn);
        addAttachment = (ImageButton) findViewById(R.id.sendAttachmentBtn);

        friendsName = (TextView)findViewById(R.id.chat_custon_title);
        friendsProfilPicture = (CircleImageView)findViewById(R.id.customChatBarImage);
        onlinePresenceCheck = (ImageView)findViewById(R.id.chat_custom_indicator);

        friendsProfilPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prof = new Intent(ChatActivity.this, UsersProfile.class);
                prof.putExtra("user_id", user_id);
                startActivity(prof);
            }
        });

        messagesRecycler = (RecyclerView)findViewById(R.id.messagesRecycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        messagesRecycler.setHasFixedSize(true);
        messagesRecycler.setLayoutManager(mLinearLayoutManager);

        loadMessages();

        currentFriendRef = db.getReference().child("User").child(user_id);

        currentFriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String theFriendProfilePic = dataSnapshot.child("profilePictureThumb").getValue().toString();
                Boolean onlineStat = (Boolean) dataSnapshot.child("online").getValue();

                if (dataSnapshot.hasChild("online")) {
                    if (onlineStat == true) {
                        onlinePresenceCheck.setVisibility(View.VISIBLE);
                    } else {
                        onlinePresenceCheck.setVisibility(View.INVISIBLE);
                    }
                }

                if (theFriendProfilePic.equalsIgnoreCase("")){

                } else {
                    Picasso.with(getBaseContext()).load(theFriendProfilePic).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile)
                            .into(friendsProfilPicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getBaseContext()).load(theFriendProfilePic)
                                            .placeholder(R.drawable.profile)
                                            .into(friendsProfilPicture);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        friendsName.setText(user_name);

        /*---------------   CLICK ACTIVITY FOR SEND BUTTON   ---------------*/
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendTheMessage();
            }
        });

        addAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionDialog();
            }
        });
    }

    private void openOptionDialog() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.chat_attachment_choice,null);

        final ImageView imageUpload = (ImageView) viewOptions.findViewById(R.id.imageUpload);
        final ImageView documentUpload = (ImageView) viewOptions.findViewById(R.id.documentUpload);

        alertDialog.setView(viewOptions);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                alertDialog.dismiss();
            }
        });

        documentUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDocument();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadDocument() {

        Intent gallery_intent = new Intent();
        gallery_intent.setType("*/*");
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery_intent, "Pick Document"), Common.PICK_DOCUMENT_REQUEST);

    }

    private void uploadImage() {
        Intent gallery_intent = new Intent();
        gallery_intent.setType("image/*");
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery_intent, "Pick Image"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            mDialog = new SpotsDialog(ChatActivity.this, "Uploading Document");
            mDialog.show();

            Uri resultUri = data.getData();
            File file= new File(resultUri.getPath());
            final String fileName = file.getName();

            final StorageReference chatDoc = storageReference.child("chat_documents").child(fileName);
            chatDoc.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(ChatActivity.this, "Document Uploaded !", Toast.LENGTH_SHORT).show();
                    chatDoc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            checkMessageRef = db.getReference("ChatMessages");
                            DatabaseReference pushIdRef = checkMessageRef.child(Common.currentUser.getMatric())
                                    .child(user_id).push();

                            final String pushId = pushIdRef.getKey();

                            checkMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long date = System.currentTimeMillis();
                                    final Message newMessage = new Message(uri.toString(), fileName, date, "sent", Common.currentUser.getMatric());
                                    messageRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
                                    messageRef2.child(pushId).setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference recieverRef = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                                            recieverRef.child(pushId).setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendNotification();
                                                }
                                            });
                                        }
                                    });

                                    chatBox.setText("");


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            mDialog.dismiss();

                        }
                    });

                }
            });



        } else if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK){

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                mDialog = new SpotsDialog(ChatActivity.this, "Uploading Picture");
                mDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setQuality(90)
                            .compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    String fileName = UUID.randomUUID().toString();
                    final StorageReference thumbFilepath = storageReference.child("chat_images").child(fileName + ".jpg");

                    thumbFilepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                UploadTask uploadTask = thumbFilepath.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        final String thumbDownloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                        if (thumb_task.isSuccessful()){

                                            checkMessageRef = db.getReference("ChatMessages");
                                            DatabaseReference pushIdRef = checkMessageRef.child(Common.currentUser.getMatric())
                                                    .child(user_id).push();

                                            final String pushId = pushIdRef.getKey();

                                            checkMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    long date = System.currentTimeMillis();
                                                    final Message newMessage = new Message(thumbDownloadUrl, "image", date, "sent", Common.currentUser.getMatric());
                                                    messageRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
                                                    messageRef2.child(pushId).setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            DatabaseReference recieverRef = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                                                            recieverRef.child(pushId).setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    sendNotification();
                                                                }
                                                            });
                                                        }
                                                    });

                                                    chatBox.setText("");


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                            mDialog.dismiss();

                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(ChatActivity.this, "Error Uploading", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {

                                mDialog.dismiss();
                                Toast.makeText(ChatActivity.this, "Error Uploading", Toast.LENGTH_SHORT).show();

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

    private void loadMessages() {

        chatMessagesRef = db.getReference("ChatMessages");

        if (Common.currentUser != null) {
            chatMessagesRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
        } else {
            chatMessagesRef2 = db.getReference("ChatMessages").child(userSav).child(user_id);
        }

        adapter = new FirebaseRecyclerAdapter<Message, ChatMessageViewHolder>(
                Message.class,
                R.layout.single_message_item,
                ChatMessageViewHolder.class,
                chatMessagesRef2
        ) {
            @Override
            protected void populateViewHolder(final ChatMessageViewHolder viewHolder, final Message model, final int position) {

                if (model.getFrom().equalsIgnoreCase(Common.currentUser.getMatric())) {

                    if (model.getType().equalsIgnoreCase("text")){
                        viewHolder.yourWholeMessageLayout.setVisibility(View.VISIBLE);
                        viewHolder.messageTextYou.setVisibility(View.VISIBLE);
                        viewHolder.messageTextSender.setVisibility(View.GONE);
                        viewHolder.imageSender.setVisibility(View.GONE);
                        viewHolder.imageYou.setVisibility(View.GONE);
                        viewHolder.documentSender.setVisibility(View.GONE);
                        viewHolder.documentYou.setVisibility(View.GONE);

                        if (model.getSeen().equalsIgnoreCase("sent")) {
                            viewHolder.sentAbbvr.setText("D");
                        } else if (model.getSeen().equalsIgnoreCase("read")){
                            viewHolder.sentAbbvr.setText("R");
                        }

                        viewHolder.messageTextYou.setText(model.getMessage());

                    }  else if (model.getType().equalsIgnoreCase("image")) {

                        viewHolder.yourWholeMessageLayout.setVisibility(View.VISIBLE);
                        viewHolder.messageTextYou.setVisibility(View.GONE);
                        viewHolder.messageTextSender.setVisibility(View.GONE);
                        viewHolder.imageSender.setVisibility(View.GONE);
                        viewHolder.imageYou.setVisibility(View.VISIBLE);
                        viewHolder.documentSender.setVisibility(View.GONE);
                        viewHolder.documentYou.setVisibility(View.GONE);

                        if (model.getSeen().equalsIgnoreCase("sent")) {
                            viewHolder.sentAbbvr.setText("D");
                        } else if (model.getSeen().equalsIgnoreCase("read")) {
                            viewHolder.sentAbbvr.setText("R");
                        }

                        Picasso.with(getBaseContext()).load(model.getMessage())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.ic_image_black_24dp)
                                .into(viewHolder.imageYou, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getBaseContext()).load(model.getMessage())
                                                .placeholder(R.drawable.ic_image_black_24dp)
                                                .into(viewHolder.imageYou);
                                    }
                                });
                        viewHolder.imageYou.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String downloadLink = model.getMessage();
                                String time = Objects.toString(model.getTime());
                                viewImage(downloadLink, time);
                            }
                        });

                    } else {

                        viewHolder.yourWholeMessageLayout.setVisibility(View.VISIBLE);
                        viewHolder.messageTextYou.setVisibility(View.GONE);
                        viewHolder.messageTextSender.setVisibility(View.GONE);
                        viewHolder.imageSender.setVisibility(View.GONE);
                        viewHolder.imageYou.setVisibility(View.GONE);
                        viewHolder.documentSender.setVisibility(View.GONE);
                        viewHolder.documentYou.setVisibility(View.VISIBLE);

                        if (model.getSeen().equalsIgnoreCase("sent")) {
                            viewHolder.sentAbbvr.setText("D");
                        } else if (model.getSeen().equalsIgnoreCase("read")){
                            viewHolder.sentAbbvr.setText("R");
                        }


                    }

                } else if (model.getFrom().equalsIgnoreCase(user_id)){

                    viewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                        }
                    });

                    viewHolder.yourWholeMessageLayout.setVisibility(View.GONE);

                    if (model.getType().equalsIgnoreCase("text")){
                        viewHolder.messageTextSender.setVisibility(View.VISIBLE);
                        viewHolder.messageTextYou.setVisibility(View.GONE);
                        viewHolder.imageSender.setVisibility(View.GONE);
                        viewHolder.imageYou.setVisibility(View.GONE);
                        viewHolder.documentSender.setVisibility(View.GONE);
                        viewHolder.documentYou.setVisibility(View.GONE);
                        viewHolder.messageTextSender.setText(model.getMessage());

                        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Boolean onlineStat = (Boolean) dataSnapshot.child("online").getValue();

                                if (onlineStat == true){

                                    if (model.getSeen().equalsIgnoreCase("read")){

                                    } else {
                                        messageRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
                                        messageRef2.child(adapter.getRef(position).getKey()).child("seen").setValue("read").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                messageRef2 = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                                                messageRef2.child(adapter.getRef(position).getKey()).child("seen").setValue("read");
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }  else if (model.getType().equalsIgnoreCase("image")){

                        viewHolder.messageTextYou.setVisibility(View.GONE);
                        viewHolder.messageTextSender.setVisibility(View.GONE);
                        viewHolder.imageSender.setVisibility(View.VISIBLE);
                        viewHolder.imageYou.setVisibility(View.GONE);
                        viewHolder.documentSender.setVisibility(View.GONE);
                        viewHolder.documentYou.setVisibility(View.GONE);

                        Picasso.with(getBaseContext()).load(model.getMessage())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.ic_image_black_24dp)
                                .into(viewHolder.imageSender, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getBaseContext()).load(model.getMessage())
                                                .placeholder(R.drawable.ic_image_black_24dp)
                                                .into(viewHolder.imageSender);
                                    }
                                });


                        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Boolean onlineStat = (Boolean) dataSnapshot.child("online").getValue();

                                if (onlineStat == true){

                                    if (model.getSeen().equalsIgnoreCase("read")){

                                    } else {
                                        messageRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
                                        messageRef2.child(adapter.getRef(position).getKey()).child("seen").setValue("read").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                messageRef2 = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                                                messageRef2.child(adapter.getRef(position).getKey()).child("seen").setValue("read");
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        viewHolder.imageSender.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String downloadLink = model.getMessage();
                                String time = Objects.toString(model.getTime());
                                viewImage(downloadLink, time);
                            }
                        });

                    } else {

                        viewHolder.messageTextYou.setVisibility(View.GONE);
                        viewHolder.messageTextSender.setVisibility(View.GONE);
                        viewHolder.imageSender.setVisibility(View.GONE);
                        viewHolder.imageYou.setVisibility(View.GONE);
                        viewHolder.documentSender.setVisibility(View.VISIBLE);
                        viewHolder.documentYou.setVisibility(View.GONE);


                        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Boolean onlineStat = (Boolean) dataSnapshot.child("online").getValue();

                                if (onlineStat == true){

                                    if (model.getSeen().equalsIgnoreCase("read")){

                                    } else {
                                        messageRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
                                        messageRef2.child(adapter.getRef(position).getKey()).child("seen").setValue("read").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                messageRef2 = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                                                messageRef2.child(adapter.getRef(position).getKey()).child("seen").setValue("read");
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        viewHolder.documentSender.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(model.getMessage());
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir("/Unilag MBA/Documents", model.getType());
                                Toast.makeText(ChatActivity.this, "Download Started !!!", Toast.LENGTH_SHORT).show();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.allowScanningByMediaScanner();
                                Long reference = downloadManager.enqueue(request);
                            }
                        });

                    }
                }
            }
        };
        messagesRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messagesRecycler.scrollToPosition(positionStart);

                }
                mLinearLayoutManager.smoothScrollToPosition(messagesRecycler, null, adapter.getItemCount());
            }
        });

    }

    private void viewImage(final String downloadLink, final String time) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View viewImage = inflater.inflate(R.layout.image_view_layout,null);

        final ImageView theImg = (ImageView) viewImage.findViewById(R.id.viewChatImage);

        Picasso.with(getBaseContext()).load(downloadLink)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(theImg, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getBaseContext()).load(downloadLink)
                                .placeholder(R.drawable.ic_image_black_24dp)
                                .into(theImg);
                    }
                });

        alertDialog.setView(viewImage);

        alertDialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(downloadLink);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir("/Unilag MBA/Images", time + ".jpg");
                Toast.makeText(ChatActivity.this, "Download Started !!!", Toast.LENGTH_SHORT).show();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                Long reference = downloadManager.enqueue(request);
                dialog.dismiss();

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

    private void sendTheMessage() {

        final String message = chatBox.getText().toString();

        if (message.isEmpty()){


        } else {

            if (Common.isConnectedToInternet(getBaseContext())) {

                chatBox.setText("");
                checkMessageRef = db.getReference("ChatMessages");
                DatabaseReference pushIdRef = checkMessageRef.child(Common.currentUser.getMatric())
                        .child(user_id).push();

                final String pushId = pushIdRef.getKey();

                checkMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long date = System.currentTimeMillis();
                        final Message newMessage = new Message(message, "text", date, "sent", Common.currentUser.getMatric());
                        messageRef2 = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
                        messageRef2.child(pushId).setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference recieverRef = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                                recieverRef.child(pushId).setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendNotification();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {

                Toast.makeText(this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String chatMessageId = adapter.getRef(item.getOrder()).getKey();

        if (item.getTitle().equals(Common.DELETE_SINGLE)){
            deleteCategory(chatMessageId);
        } else if (item.getTitle().equals(Common.DELETE_BOTH)){

            deleteBothCategory(chatMessageId);
        }

        return super.onContextItemSelected(item);
    }

    private void deleteBothCategory(final String chatMessageId) {
        DatabaseReference chat = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
        Query messageQuery = chat.orderByKey().equalTo(chatMessageId);
        messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    postSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DatabaseReference chat = db.getReference("ChatMessages").child(user_id).child(Common.currentUser.getMatric());
                            Query messageQuery = chat.orderByKey().equalTo(chatMessageId);
                            messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                                        postSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chat.child(chatMessageId).removeValue();
        Toast.makeText(this, "Message Deleted", Toast.LENGTH_SHORT).show();
    }

    private void deleteCategory(final String chatMessageId) {

        DatabaseReference chat = db.getReference("ChatMessages").child(Common.currentUser.getMatric()).child(user_id);
        Query messageQuery = chat.orderByKey().equalTo(chatMessageId);
        messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    postSnapshot.getRef().removeValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chat.child(chatMessageId).removeValue();
        Toast.makeText(this, "Message Deleted", Toast.LENGTH_SHORT).show();

    }

    private void sendNotification() {
        Map<String, String> dataSend = new HashMap<>();
        dataSend.put("title", "New Message");
        dataSend.put("message", "You Have A New Message From  "+ Common.currentUser.getUserName());
        dataSend.put("user_id", Common.currentUser.getMatric());
        dataSend.put("user_name", Common.currentUser.getUserName());
        DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(user_id).toString(), dataSend);

        mService.sendNotification(dataMessage)
                .enqueue(new retrofit2.Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.currentUser.getMatric() != null) {
            usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
            usersRef.child("online").setValue(false);
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
                    loadMessages();
                }
            }
            break;
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

            Intent help = new Intent(ChatActivity.this, Help.class);
            startActivity(help);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}