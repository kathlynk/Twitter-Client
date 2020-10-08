package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    RecyclerView rvTimeline;
    List<Tweet> tweets;
    TweetAdapter adapter;
    SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo_whiteonimage);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        client = TwitterApp.getRestClient(this);

        //find swipe refresh layout
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                fetchTimelineAsync(0);

            }
        });

        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        //find recyclerview
        rvTimeline = findViewById(R.id.rvTimeline);

        //initialize list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);

        //configure recycler view: layout manager and adapter
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rvTimeline.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(rvTimeline.getContext(), VERTICAL);
        rvTimeline.addItemDecoration(decoration);
        populateHomeTimeline();
    }

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
}