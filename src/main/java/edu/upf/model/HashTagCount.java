package edu.upf.model;

import com.google.gson.Gson;

public final class HashTagCount {
  protected static Gson gson = new Gson();

  final String hashTag;
  final String lang;
  final Long count;

  public HashTagCount(String hashTag, String lang, Long count) {
    this.hashTag = hashTag;
    this.lang = lang;
    this.count = count;
  }

  @Override
  public String toString() {
    return gson.toJson(this);
  }
}
