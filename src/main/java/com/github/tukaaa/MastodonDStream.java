package com.github.tukaaa;

import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.dstream.ReceiverInputDStream;
import org.apache.spark.streaming.receiver.Receiver;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

public class MastodonDStream extends ReceiverInputDStream<SimplifiedTweetWithHashtags> {

    private final AppConfig config;
    StorageLevel sl = StorageLevel.MEMORY_AND_DISK_2();
    private final static ClassTag<SimplifiedTweetWithHashtags> tag =
            ClassTag$.MODULE$.apply(SimplifiedTweetWithHashtags.class);

    public MastodonDStream(StreamingContext _ssc, AppConfig config) {
        super(_ssc, tag);
        this.config = config;
    }

    @Override
    public Receiver<SimplifiedTweetWithHashtags> getReceiver() {
        return new MastodonDStreamReceiver(sl, config.getSleepInterval(), config.getMastodonServer());
    }

    public JavaDStream<SimplifiedTweetWithHashtags> asJStream() {
        return JavaDStream.fromDStream(this, tag);
    }
}
