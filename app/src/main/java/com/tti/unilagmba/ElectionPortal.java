package com.tti.unilagmba;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Common.Common;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.VotingCandidates;
import com.tti.unilagmba.ViewHolder.VotingCandidateViewHolder;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class ElectionPortal extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference voteStatus, votes, voteCandidates, usersRef;
    private Toolbar mToolbar;
    private LinearLayoutManager layoutManager;
    private Button castVote;
    private LinearLayout presidentLayout, vpFullTimeLayout, vpPartTimeLayout, vpExecLayout, genSecLayout, finSecLayout, treasurerLayout, legalAdviserLayout, assistantGenSecLayout, welfareLayout, proLayout, directorOfSocialsLayout, auditorLayout;
    private android.app.AlertDialog mDialog;
    private RecyclerView president, vpFullTime, vpPartTime, vpExec, genSec, finSec, treasurer, legalAdviser, assistantGenSec, welfare, pro, directorOfSocials, auditor;
    private FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder> adapter, adapter2, adapter3, adapter4, adapter5, adapter6, adapter7, adapter8, adapter9, adapter10, adapter11, adapter12, adapter13;
    String userSav = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new SpotsDialog(ElectionPortal.this, "Checking Status");
        mDialog.show();
        db = FirebaseDatabase.getInstance();

        Paper.init(this);
        userSav = Paper.book().read(Common.USER_KEY);

        voteStatus = db.getReference("VotingStatus").child("Status").child("electionStatus");
        voteStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("active")) {
                    mDialog.dismiss();
                    setContentView(R.layout.activity_election_portal);


                    /*----------   KEEP USERS ONLINE   ----------*/
                    if (Common.currentUser != null) {
                        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(Common.currentUser.getMatric());
                    } else {
                        usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(userSav);
                    }
                    usersRef.child("online").setValue(true);
                    usersRef.keepSynced(true);

                    mToolbar = (Toolbar)findViewById(R.id.electtionAppBar);
                    setSupportActionBar(mToolbar);
                    getSupportActionBar().setTitle("Cast Your Votes");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


                    presidentLayout = (LinearLayout)findViewById(R.id.presidentLayout);
                    vpFullTimeLayout = (LinearLayout)findViewById(R.id.vpftLayout);
                    vpPartTimeLayout = (LinearLayout)findViewById(R.id.vpptLayout);
                    vpExecLayout = (LinearLayout)findViewById(R.id.vpExecLayout);
                    genSecLayout = (LinearLayout)findViewById(R.id.genSecLayout);
                    finSecLayout = (LinearLayout)findViewById(R.id.finSecLayout);
                    treasurerLayout = (LinearLayout)findViewById(R.id.treasurerLayout);
                    legalAdviserLayout = (LinearLayout)findViewById(R.id.legalAdviserLayout);
                    assistantGenSecLayout = (LinearLayout)findViewById(R.id.assGenSecLayout);
                    welfareLayout = (LinearLayout)findViewById(R.id.welfareOfficerLayout);
                    proLayout = (LinearLayout)findViewById(R.id.proLayout);
                    directorOfSocialsLayout = (LinearLayout)findViewById(R.id.directorOfSocialsLayout);
                    auditorLayout = (LinearLayout)findViewById(R.id.auditorLayout);


                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadCandidates();
                    } else {
                        Toast.makeText(getBaseContext(), "Please Check Connection !", Toast.LENGTH_SHORT).show();
                    }




                } else {
                    mDialog.dismiss();
                    setContentView(R.layout.no_voting_activity);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadCandidates() {
        /*----------   THE PRESIDENT   ----------*/
        president = (RecyclerView)findViewById(R.id.presidentRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        president.setLayoutManager(layoutManager);
        db = FirebaseDatabase.getInstance();
        voteCandidates = db.getReference("VotingCandidates");

        adapter = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("President")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model, int position) {
                viewHolder.candidateName.setText(model.getName());
                if (!model.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "President";
                final String name = model.getName();
                final String category = "President";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);

                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            presidentLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        president.setAdapter(adapter);




        /*----------   THE VICE PRESIDENT FULL TIME   ----------*/
        vpFullTime = (RecyclerView)findViewById(R.id.vpFullTimeRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        vpFullTime.setLayoutManager(layoutManager);

        adapter2 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("VpFullTime")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model2, int position) {
                viewHolder.candidateName.setText(model2.getName());
                if (!model2.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model2.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Vice President Full Time";
                final String name = model2.getName();
                final String category = "VpFullTime";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            vpFullTimeLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        vpFullTime.setAdapter(adapter2);


        /*----------   THE VICE PRESIDENT PART TIME   ----------*/
        vpPartTime = (RecyclerView)findViewById(R.id.vpPartTimeRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        vpPartTime.setLayoutManager(layoutManager);

        adapter3 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("VpPartTime")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model3, int position) {
                viewHolder.candidateName.setText(model3.getName());
                if (!model3.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model3.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Vice President Part Time";
                final String name = model3.getName();
                final String category = "VpPartTime";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            vpPartTimeLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        vpPartTime.setAdapter(adapter3);


        /*----------   THE VICE PRESIDENT EXECUTIVE   ----------*/
        vpExec = (RecyclerView)findViewById(R.id.vpExecutiveRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        vpExec.setLayoutManager(layoutManager);

        adapter4 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("VpExec")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model4, int position) {
                viewHolder.candidateName.setText(model4.getName());
                if (!model4.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model4.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Vice President Executive";
                final String name = model4.getName();
                final String category = "VpExec";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            vpExecLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        vpExec.setAdapter(adapter4);


        /*----------   THE GENERAL SECRETARY   ----------*/
        genSec = (RecyclerView)findViewById(R.id.generalSecretaryRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        genSec.setLayoutManager(layoutManager);

        adapter5 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("GenSec")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model5, int position) {
                viewHolder.candidateName.setText(model5.getName());
                if (!model5.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model5.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "General Secretary";
                final String name = model5.getName();
                final String category = "GenSec";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            genSecLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        genSec.setAdapter(adapter5);


        /*----------   THE FINANCIAL SECRETARY   ----------*/
        finSec = (RecyclerView)findViewById(R.id.financialSecretaryRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        finSec.setLayoutManager(layoutManager);

        adapter6 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("FinSec")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model6, int position) {
                viewHolder.candidateName.setText(model6.getName());
                if (!model6.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model6.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Financial Secretary";
                final String name = model6.getName();
                final String category = "FinSec";


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            finSecLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        finSec.setAdapter(adapter6);


        /*----------   THE TREASURER   ----------*/
        treasurer = (RecyclerView)findViewById(R.id.treasurerRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        treasurer.setLayoutManager(layoutManager);

        adapter7 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("Treasurer")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model7, int position) {
                viewHolder.candidateName.setText(model7.getName());
                if (!model7.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model7.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Treasurer";
                final String name = model7.getName();
                final String category = "Treasurer";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            treasurerLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        treasurer.setAdapter(adapter7);


        /*----------   THE LEGAL ADVISER   ----------*/
        legalAdviser = (RecyclerView)findViewById(R.id.legalAdviserRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        legalAdviser.setLayoutManager(layoutManager);

        adapter8 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("LegalAdviser")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model8, int position) {
                viewHolder.candidateName.setText(model8.getName());
                if (!model8.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model8.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Legal Adviser";
                final String name = model8.getName();
                final String category = "LegalAdviser";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            legalAdviserLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        legalAdviser.setAdapter(adapter8);


        /*----------   THE ASSISTANT GENERAL SECRETARY   ----------*/
        assistantGenSec = (RecyclerView)findViewById(R.id.assistantGeneralSecretaryRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        assistantGenSec.setLayoutManager(layoutManager);

        adapter9 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("AssistantGenSec")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model9, int position) {
                viewHolder.candidateName.setText(model9.getName());
                if (!model9.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model9.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Assistant General Secretary";
                final String name = model9.getName();
                final String category = "AssistantGenSec";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            assistantGenSecLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        assistantGenSec.setAdapter(adapter9);


        /*----------   THE WELFARE OFFICER   ----------*/
        welfare = (RecyclerView)findViewById(R.id.welfareOfficerRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        welfare.setLayoutManager(layoutManager);

        adapter10 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("WelfareOfficer")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model10, int position) {
                viewHolder.candidateName.setText(model10.getName());
                if (!model10.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model10.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Welfare Officer";
                final String name = model10.getName();
                final String category = "WelfareOfficer";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            welfareLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        welfare.setAdapter(adapter10);


        /*----------   THE PUBLIC RELATION OFFICER   ----------*/
        pro = (RecyclerView)findViewById(R.id.proRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pro.setLayoutManager(layoutManager);

        adapter11 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("PRO")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model11, int position) {
                viewHolder.candidateName.setText(model11.getName());
                if (!model11.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model11.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "P.R.O";
                final String name = model11.getName();
                final String category = "PRO";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            proLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        pro.setAdapter(adapter11);


        /*----------   THE DIRECTOR OF SOCIALS   ----------*/
        directorOfSocials = (RecyclerView)findViewById(R.id.directorOfSocialsRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        directorOfSocials.setLayoutManager(layoutManager);

        adapter12 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("DirectorOfSocials")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model12, int position) {
                viewHolder.candidateName.setText(model12.getName());
                if (!model12.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model12.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Director Of Socials";
                final String name = model12.getName();
                final String category = "DirectorOfSocials";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            directorOfSocialsLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        directorOfSocials.setAdapter(adapter12);



        /*----------   THE AUDITOR   ----------*/
        auditor = (RecyclerView)findViewById(R.id.auditorRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        auditor.setLayoutManager(layoutManager);

        adapter13 = new FirebaseRecyclerAdapter<VotingCandidates, VotingCandidateViewHolder>(
                VotingCandidates.class,
                R.layout.election_candidate_item,
                VotingCandidateViewHolder.class,
                voteCandidates.child("Auditor")) {
            @Override
            protected void populateViewHolder(final VotingCandidateViewHolder viewHolder, final VotingCandidates model13, int position) {
                viewHolder.candidateName.setText(model13.getName());
                if (!model13.getThumb().equalsIgnoreCase("")){
                    Picasso.with(getBaseContext()).load(model13.getThumb())
                            .placeholder(R.drawable.profile)
                            .into(viewHolder.candidateImage);
                }

                final String post = "Auditor";
                final String name = model13.getName();
                final String category = "Auditor";

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        openConfirmationdialog(post, name, category);
                    }
                });

                votes = db.getReference("VotingResults").child(category);
                votes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Common.currentUser.getMatric()).exists()){
                            auditorLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        auditor.setAdapter(adapter13);
    }

    private void openConfirmationdialog(String post, final String name, final String category) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_view = inflater.inflate(R.layout.voting_confirmation, null);

        final TextView conf = (TextView)confirm_view.findViewById(R.id.confirmationText);

        conf.setText("Are You Sure You Want To Vote "+ name + " In As Your New " + post);

        builder.setView(confirm_view);
        builder.setIcon(R.drawable.ic_voting_24dp);



        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db = FirebaseDatabase.getInstance();
                votes = db.getReference("VotingResults").child(category);

                if (Common.isConnectedToInternet(getBaseContext())) {
                    votes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(userSav).exists()) {

                                votes = db.getReference("VotingResults").child(category);

                                votes.child(userSav).setValue(name);

                            } else {

                                Toast.makeText(ElectionPortal.this, "Sorry Mate You Voted Already", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {

                    Toast.makeText(ElectionPortal.this, "Please Check Connection !", Toast.LENGTH_SHORT).show();

                }

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help){

            Intent help = new Intent(ElectionPortal.this, Help.class);
            startActivity(help);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
