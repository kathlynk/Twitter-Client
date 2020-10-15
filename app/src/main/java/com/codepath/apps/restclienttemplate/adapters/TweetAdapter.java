package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimeFormatter;
import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;


import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    //pass in context and tweet list
    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    //for each row inflate tweet layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }


    //Bind data based on position of list element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Get data at position
        Tweet tweet = tweets.get(position);

        //Bind data with viewholder
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    //define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvCreatedAt;
        TextView tvNumFavorites;
        TextView tvNumRetweets;
        ImageView ivPhotoMedia;
        RelativeLayout container;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvNumFavorites = itemView.findViewById(R.id.tvNumFavorites);
            tvNumRetweets = itemView.findViewById(R.id.tvNumRetweet);
            ivPhotoMedia = itemView.findViewById(R.id.ivPhotoMedia);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvCreatedAt.setText(getFormattedTimestamp(tweet.createdAt));
            tvNumFavorites.setText(Integer.toString(tweet.favorites));
            tvNumRetweets.setText(Integer.toString(tweet.retweets));

            //profile image loaded with Glide
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCornersTransformation(10, 0)).into(ivProfileImage);

            //if post has an embedded photo, loads it with glide
            if (tweet.mediaType.equals("photo") || tweet.mediaType.equals("animated_gif")) {
                ivPhotoMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.mediaUrl).fitCenter().into(ivPhotoMedia);
            } else {
                ivPhotoMedia.setVisibility(View.GONE);
            }

            //if user clicks a tweet, launches the detail view activity
            container.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, TweetDetailActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                    //Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show();
                }
            });



        }

        private String getFormattedTimestamp(String createdAt) {
            return TimeFormatter.getTimeDifference(createdAt);
        }
    }
}
