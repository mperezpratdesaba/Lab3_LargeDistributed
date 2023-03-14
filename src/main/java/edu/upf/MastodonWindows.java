package edu.upf;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class MastodonWindows {
    public static void main(String[] args) {
        String input = args[0];

        SparkConf conf = new SparkConf().setAppName("Real-time Mastodon Stateful with Windows Exercise");
        AppConfig appConfig = AppConfig.getConfig();

        StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
        JavaStreamingContext jsc = new JavaStreamingContext(sc);
        jsc.checkpoint("/tmp/checkpoint");

        JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();

        // TODO IMPLEMENT ME

        // Start the application and wait for termination signal
        sc.start();
        sc.awaitTermination();
    }

}
