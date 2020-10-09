package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        // toolbar and logo
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo_whiteonimage);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Tweet tweet =  (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvNumFavorites;
        TextView tvNumRetweets;
        ImageView ivPhotoMedia;

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvNumFavorites = findViewById(R.id.tvNumFavorites);
        tvNumRetweets = findViewById(R.id.tvNumRetweet);
        ivPhotoMedia = findViewById(R.id.ivPhotoMedia);

        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvName.setText("@" + tweet.user.name);
        tvNumFavorites.setText(Integer.toString(tweet.favorites) + "  FAVORITES");
        tvNumRetweets.setText(Integer.toString(tweet.retweets) + "  RETWEETS");

        //profile image loaded with Glide
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new RoundedCornersTransformation(10, 0)).into(ivProfileImage);

        //if post has an embedded photo, loads it with glide
        if (tweet.mediaType.equals("photo") || tweet.mediaType.equals("animated_gif")) {
            ivPhotoMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.mediaUrl).into(ivPhotoMedia);
        } else {
            ivPhotoMedia.setVisibility(View.GONE);
        }

    }
}