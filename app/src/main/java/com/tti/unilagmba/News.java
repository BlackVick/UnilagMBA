package com.tti.unilagmba;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tti.unilagmba.Interface.ItemClickListener;
import com.tti.unilagmba.Model.Banner;
import com.tti.unilagmba.Model.Events;
import com.tti.unilagmba.Model.MissingItems;
import com.tti.unilagmba.Model.NewsFeeds;
import com.tti.unilagmba.ViewHolder.NewsFeedViewHolder;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class News extends Fragment {

    FirebaseDatabase db;
    DatabaseReference news, missing, event, bannerStatus;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<NewsFeeds, NewsFeedViewHolder> adapter;
    FirebaseRecyclerAdapter<MissingItems, NewsFeedViewHolder> missAdapter;
    FirebaseRecyclerAdapter<Events, NewsFeedViewHolder> eventAdapter;
    View v;
    private RecyclerView recyclerView;

    /*--- SLIDER BANNER ADS   ---*/
    HashMap<String, String> imageList;
    SliderLayout mSlider;

    public News() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        db = FirebaseDatabase.getInstance();
        news = db.getReference("NewsFeeds");
        news.keepSynced(true);
        missing = db.getReference("MissingItems");
        missing.keepSynced(true);
        event = db.getReference("Events");
        event.keepSynced(true);
        bannerStatus = db.getReference("BannerStatus");

        v = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.newsRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext()){

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {

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
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        mSlider = (SliderLayout)v.findViewById(R.id.slider);

        loadNewsFeed();

        bannerStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("status").getValue().toString().equals("active")){

                    mSlider.setVisibility(View.VISIBLE);
                    setupSlider();

                } else {

                    mSlider.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void setupSlider() {

        imageList = new HashMap<>();
        final DatabaseReference banners = FirebaseDatabase.getInstance().getReference("BannerAds");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Banner banner = postSnapshot.getValue(Banner.class);
                    imageList.put(banner.getName()+"@@@"+banner.getLink(), banner.getImage());

                }

                for (String key:imageList.keySet()){

                    String[] keySplit = key.split("@@@");
                    String titleOfAd = keySplit[0];
                    final String adId = keySplit[1];

                    final TextSliderView textSliderView = new TextSliderView(getContext());
                    textSliderView
                            .description(titleOfAd)
                            .image(imageList.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent i = new Intent(getContext(), AdView.class);
                                    i.putExtras(textSliderView.getBundle());
                                    i.putExtra("Link", adId);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });

                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("LLink", adId);
                    mSlider.addSlider(textSliderView);


                    banners.removeEventListener(this);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(8000);

    }

    private void loadNewsFeed() {
        adapter = new FirebaseRecyclerAdapter<NewsFeeds, NewsFeedViewHolder>(NewsFeeds.class, R.layout.news_item, NewsFeedViewHolder.class, news) {
            @Override
            protected void populateViewHolder(final NewsFeedViewHolder viewHolder, final NewsFeeds model, int position) {
                viewHolder.newsTitle.setText(model.getNewsTitle());
                viewHolder.newsTime.setText(model.getTime());
                Picasso.with(getContext()).load(model.getNewsImage())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .resize(120, 120)
                        .centerCrop()
                        .into(viewHolder.newsPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getContext()).load(model.getNewsImage())
                                        .resize(120, 120)
                                        .centerCrop()
                                        .into(viewHolder.newsPicture);
                            }
                        });

                final NewsFeeds local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent newsDetail = new Intent(getContext(), NewsDetails.class);
                        newsDetail.putExtra("NewsId",adapter.getRef(position).getKey());
                        newsDetail.putExtra("NewsCategory", model.getNewsCategory());
                        startActivity(newsDetail);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_main_feed) {
            Toast.makeText(getContext(), "Main News Feed", Toast.LENGTH_SHORT).show();
            loadNewsFeed();
            return true;

        } else if (id == R.id.missing_items){
            Toast.makeText(getContext(), "Missing Items", Toast.LENGTH_SHORT).show();
            loadMissingItems();
            return true;

        } else if (id == R.id.events_menu){
            Toast.makeText(getContext(), "Events' News ", Toast.LENGTH_SHORT).show();
            loadEvents();
            return true;
        } else if (id == R.id.action_help){
            Intent help = new Intent(getContext(), Help.class);
            startActivity(help);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadEvents() {
        eventAdapter = new FirebaseRecyclerAdapter<Events, NewsFeedViewHolder>(Events.class, R.layout.news_item, NewsFeedViewHolder.class, event) {
            @Override
            protected void populateViewHolder(final NewsFeedViewHolder viewHolder, final Events model, int position) {
                viewHolder.newsTitle.setText(model.getNewsTitle());
                viewHolder.newsTime.setText(model.getTime());
                Picasso.with(getContext()).load(model.getNewsImage())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .resize(120, 120)
                        .centerCrop()
                        .into(viewHolder.newsPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getContext()).load(model.getNewsImage())
                                        .resize(120, 120)
                                        .centerCrop()
                                        .into(viewHolder.newsPicture);
                            }
                        });

                final Events local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent newsDetail = new Intent(getContext(), NewsDetails.class);
                        newsDetail.putExtra("NewsId",eventAdapter.getRef(position).getKey());
                        newsDetail.putExtra("NewsCategory", model.getNewsCategory());
                        startActivity(newsDetail);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        recyclerView.setAdapter(eventAdapter);
    }

    private void loadMissingItems() {
        missAdapter = new FirebaseRecyclerAdapter<MissingItems, NewsFeedViewHolder>(MissingItems.class, R.layout.news_item, NewsFeedViewHolder.class, missing) {
            @Override
            protected void populateViewHolder(final NewsFeedViewHolder viewHolder, final MissingItems model, int position) {
                viewHolder.newsTitle.setText(model.getNewsTitle());
                viewHolder.newsTime.setText(model.getTime());
                Picasso.with(getContext()).load(model.getNewsImage())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .resize(120, 120)
                        .centerCrop()
                        .into(viewHolder.newsPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getContext()).load(model.getNewsImage())
                                        .resize(120, 120)
                                        .centerCrop()
                                        .into(viewHolder.newsPicture);
                            }
                        });

                final MissingItems local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent newsDetail = new Intent(getContext(), NewsDetails.class);
                        newsDetail.putExtra("NewsId",missAdapter.getRef(position).getKey());
                        newsDetail.putExtra("NewsCategory", model.getNewsCategory());
                        startActivity(newsDetail);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        };
        recyclerView.setAdapter(missAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSlider.stopAutoCycle();
    }
}