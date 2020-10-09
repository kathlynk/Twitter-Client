package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public long id;
    public int retweets;
    public int favorites;
    public String mediaUrl;
    public String mediaType;


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        tweet.retweets = jsonObject.getInt("retweet_count");
        tweet.favorites = jsonObject.getInt("favorite_count");

        JSONObject entities = jsonObject.getJSONObject("entities");

        if (entities.has("media")) {
            JSONObject media = entities.getJSONArray("media").getJSONObject(0);
            tweet.mediaUrl = media.getString("media_url_https");
            tweet.mediaType = media.getString("type");
        } else {
            tweet.mediaUrl = "";
            tweet.mediaType = "";
        }

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
