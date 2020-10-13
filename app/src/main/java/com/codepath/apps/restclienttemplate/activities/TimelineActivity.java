package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.FragmentComposeTweet;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class TimelineActivity extends AppCompatActivity implements FragmentComposeTweet.ComposeTweetListener {

    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    RecyclerView rvTimeline;
    List<Tweet> tweets;
    TweetAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    EndlessRecyclerViewScrollListener scrollListener;
    FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);



        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        configureToolbar(toolbar);

        //initialize list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);

        //recycler view, layout manager, adapter, and divider
        rvTimeline = (RecyclerView) findViewById(R.id.rvTimeline);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTimeline.setLayoutManager(linearLayoutManager);
        rvTimeline.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(rvTimeline.getContext(), VERTICAL);
        rvTimeline.addItemDecoration(decoration);

        //get client and populate timeline
        client = TwitterApp.getRestClient(this);
        populateHomeTimeline();

        //find swipe refresh layout
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                fetchTimelineAsync(0);

            }
        });

        //set colors for swipe refresh indicator
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefresh.setProgressViewOffset(false, 100, 200);


        //infinite scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        rvTimeline.addOnScrollListener(scrollListener);

        //Fab
        mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showComposeDialog();
            }
        });

    }


    //shows compose tweet dialog fragment
    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentComposeTweet fragmentComposeTweet = FragmentComposeTweet.newInstance();
        fragmentComposeTweet.show(fm, "fragment_compose_tweet");
    }


    //reloads timeline -- for swipe refresh
    public void fetchTimelineAsync(int page) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Fetch timeline error: " + throwable.toString());
            }
        });
    }


    //populates home timeline when activity is created
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }


    //adds more tweets to bottom of list for infinite scroll
    public void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextTweetPage(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for loadMoreData" + json.toString());
                // 2. Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    // 3. Append the new data objects to the existing set of items inside the array of items
                    // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure for loadMoreData", throwable);
            }
        }, tweets.get(tweets.size()-1).id);



    }


    //when tweet compose dialog is finished, adds composed tweet to top of list
    @Override
    public void onFinishComposeTweet(Tweet tweet) {

        //manually insert tweet in timeline
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);

        //scrolls recycler view up -> newly inserted tweet visible at top
        rvTimeline.smoothScrollToPosition(0);

        //add composed tweet to timeline via refresh **
        //populateHomeTimeline();
    }


    //toolbar settings
    private void configureToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo_whiteonimage);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
}