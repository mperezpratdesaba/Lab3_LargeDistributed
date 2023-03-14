package edu.upf.storage;

import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import edu.upf.model.HashTagCount;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class DynamoHashTagRepository implements IHashtagRepository, Serializable {

  final static String endpoint = "dynamodb.us-east-1.amazonaws.com";
  final static String region = "us-east-1";
  @Override
  public void write(SimplifiedTweetWithHashtags h) {
    // TODO IMPLEMENT ME
  }


  @Override
  public List<HashTagCount> readTop10(String lang) {
    return Collections.emptyList(); // TODO IMPLEMENT ME
  }
}