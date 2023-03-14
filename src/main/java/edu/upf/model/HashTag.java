package edu.upf.model;

import com.google.gson.Gson;

import java.io.Serializable;

public final class HashTag implements Serializable {
    private static final Gson gson = new Gson();

    final String hashtag;
    final String lang;
    final long tweetId;
    final long userId;

    public HashTag(String hashtag, String lang, long tweetId, long userId) {
        this.hashtag = hashtag;
        this.lang = lang;
        this.tweetId = tweetId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public String getHashtag() {
        return hashtag;
    }

    public String getLang() {
        return lang;
    }

    public long getUserId() {
        return userId;
    }

    public long getTweetId() {
        return tweetId;
    }
}