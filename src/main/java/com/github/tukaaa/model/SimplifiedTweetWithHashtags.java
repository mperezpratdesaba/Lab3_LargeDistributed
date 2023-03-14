package com.github.tukaaa.model;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.List;

public class SimplifiedTweetWithHashtags implements Serializable {

  private static Gson gson = new Gson();
  private static JsonParser parser = new JsonParser();

  private final long tweetId;			  // the id of the tweet ('id')
  private final String text;  		      // the content of the tweet ('text')
  private final long userId;			  // the user id ('user->id')
  private final String userName;		  // the user name ('user'->'name')
  private final String language;          // the language of a tweet ('lang')
  private final long timestampMs;		  // seconds from epoch ('timestamp_ms')

  private final List<String> hashtags;

  public SimplifiedTweetWithHashtags(long tweetId, String text, long userId, String userName,
                                     String language, long timestampMs, List<String> hashtags) {
    this.tweetId = tweetId;
    this.text = text;
    this.userId = userId;
    this.userName = userName;
    this.language = language;
    this.timestampMs = timestampMs;
    this.hashtags = hashtags;
  }

  public long getTweetId() {
    return tweetId;
  }

  public String getText() {
    return text;
  }

  public long getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public long getTimestampMs() {
    return timestampMs;
  }

  @Override
  public String toString() {
    return gson.toJson(this);
  }

  public String getLanguage() { return language; }

  public List<String> getHashtags() {
    return hashtags;
  }
}
