package edu.upf;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;

public class MastodonStreamingExample {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Real-time Mastodon Example");
        AppConfig appConfig = AppConfig.getConfig();
        StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
        // This is needed by spark to write down temporary data
        sc.checkpoint("/tmp/checkpoint");

        final JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();

        stream.print(10);

        // Start the application and wait for termination signal
        sc.start();
        sc.awaitTermination();
    }

}
