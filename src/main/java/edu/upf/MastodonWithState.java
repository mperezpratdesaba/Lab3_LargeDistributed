package edu.upf;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import scala.Tuple2;
import org.apache.spark.api.java.Optional;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class MastodonWithState {
    public static void main(String[] args) throws InterruptedException {
        String language = args[0];
        SparkConf conf = new SparkConf().setAppName("Real-time Mastodon With State");
        AppConfig appConfig = AppConfig.getConfig();

        StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
        JavaStreamingContext jsc = new JavaStreamingContext(sc);
        jsc.checkpoint("/tmp/checkpoint");

        JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();
        JavaPairDStream<String, Integer> filteredTweets = stream.filter(x -> x.getLanguage() != null &&  x.getLanguage().equals(language))
                                                                .map(tweet -> tweet.getUserName())
                                                                .mapToPair(str -> new Tuple2<>(str, 1));
                                                                
        JavaPairDStream<String, Integer> accumulated = filteredTweets.updateStateByKey((newCounts, currentState) -> {
                                                                                        int sum = currentState.orElse(0);
                                                                                        for (int count : newCounts) {
                                                                                            sum += count;
                                                                                        }
                                                                                        return Optional.of(sum);
                                                                                        });
                                                                                        //.mapToPair(x -> new Tuple2<>(x._2, x._1));
                                                                                        //.transformToPair(rdd -> rdd.sortByKey(false))
                                                                                        //.mapToPair(x -> new Tuple2<>(x._2, x._1));
        
        JavaPairDStream<String, Integer> accumulatedSorted = accumulated.mapToPair(Tuple2::swap)
                                                                        .transformToPair(rdd -> rdd.sortByKey(false))
                                                                        .mapToPair(Tuple2::swap);
        

        // TODO IMPLEMENT ME
        filteredTweets.print(10);
        accumulatedSorted.print(10);
        // Start the application and wait for termination signal
        jsc.start();
        jsc.awaitTermination();
    }

}

