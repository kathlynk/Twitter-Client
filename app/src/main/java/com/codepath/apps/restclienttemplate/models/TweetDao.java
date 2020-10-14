package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT " +
            "Tweet.id AS tweet_id," +
            "Tweet.body AS tweet_body, " +
            "Tweet.createdAt AS tweet_createdAt, " +
            "Tweet.retweets AS tweet_retweets, " +
            "Tweet.favorites AS tweet_favorites, " +
            "Tweet.mediaUrl AS tweet_mediaUrl, " +
            "Tweet.mediaType AS tweet_mediaType, " +
            "User.* " +
            "FROM Tweet INNER JOIN User ON Tweet.userID = User.id ORDER BY Tweet.createdAt DESC LIMIT 100")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
