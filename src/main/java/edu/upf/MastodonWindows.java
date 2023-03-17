package edu.upf;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import edu.upf.util.LanguageMapUtils;
import scala.Tuple2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class MastodonWindows {
    public static void main(String[] args) {
        String input = args[0];

        SparkConf conf = new SparkConf().setAppName("Real-time Mastodon Stateful with Windows Exercise");
        AppConfig appConfig = AppConfig.getConfig();

        StreamingContext sc = new StreamingContext(conf, Durations.seconds(20)); // 20 second micro-batch
        JavaStreamingContext jsc = new JavaStreamingContext(sc);
        jsc.checkpoint("/tmp/checkpoint");

        final JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();
        final JavaDStream<SimplifiedTweetWithHashtags> windowedStream = stream.window(Durations.seconds(60)); // 1 minute window

        //Language file
        JavaRDD<String> inputRDD = jsc.sparkContext().textFile(input);
        JavaPairRDD<String, String> languages = LanguageMapUtils.buildLanguageMap(inputRDD).distinct(); //returns pair "es" "spanish"
        
        //STREAM

        JavaDStream<String> tweetLanguage = stream.map(tweet -> tweet.getLanguage()); //Transform DStream of tweets to a DStream of languages
        JavaPairDStream<String, Integer> tweetLanguageCount = tweetLanguage.mapToPair(str -> new Tuple2<>(str, 1))
                                                                        .reduceByKey((x, y) -> x + y); //DStream of languages to DStream of (language,1)
                                                                        
        JavaPairDStream<String, Tuple2<Integer, String>> tweetCompleteLanguageCount = tweetLanguageCount.transformToPair(rdd -> rdd.join(languages));
        JavaPairDStream<String, Integer> tweetLanguageCountWithName = tweetCompleteLanguageCount.mapToPair(tuple -> tuple._2)
                                                                                                .transformToPair(rdd -> rdd.sortByKey(false))
                                                                                                .mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1));
        

        //WINDOW
 
        JavaDStream<String> tweetLanguageWindowed = windowedStream.map(tweet -> tweet.getLanguage()); //Transform DStream of tweets to a DStream of languages
        JavaPairDStream<String, Integer> tweetLanguageCountWindowed = tweetLanguageWindowed.mapToPair(str -> new Tuple2<>(str, 1))
                                                                        .reduceByKey((x, y) -> x + y); //DStream of languages to DStream of (language,1)

                                                                        
        JavaPairDStream<String, Tuple2<Integer, String>> tweetCompleteLanguageCountWindowed = tweetLanguageCountWindowed.transformToPair(rdd -> rdd.join(languages));
        JavaPairDStream<String, Integer> tweetLanguageCountWithNameWindowed = tweetCompleteLanguageCountWindowed.mapToPair(tuple -> tuple._2)
                                                                                                .transformToPair(rdd -> rdd.sortByKey(false))
                                                                                                .mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1));
        
        tweetLanguageCountWithName.print(15); //print 15 most frequent languages
        tweetLanguageCountWithNameWindowed.print(15);

        // Start the application and wait for termination signal
        sc.start();
        sc.awaitTermination();
    }

}
