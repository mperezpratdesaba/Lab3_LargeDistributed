package com.github.tukaaa.adapter;

import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import org.apache.spark.api.java.Optional;
import org.jsoup.Jsoup;
import social.bigbone.api.entity.Status;
import social.bigbone.api.entity.Tag;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SimplifiedTweetAdapter {

    public static Optional<SimplifiedTweetWithHashtags> fromStatus(Status status) {
        try {
            SimplifiedTweetWithHashtags tweet = new SimplifiedTweetWithHashtags(
                    Long.parseLong(status.getId()),
                    Jsoup.parse(status.getContent()).text(),
                    Long.parseLong(status.getAccount().getId()),
                    status.getAccount().getDisplayName(),
                    status.getLanguage(),
                    ZonedDateTime.parse(status.getCreatedAt()).toInstant().toEpochMilli(),
                    status.getTags().stream().map(Tag::getName).collect(Collectors.toList())
            );
            return Optional.of(tweet);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
