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

public class MastodonStateless {
    public static void main(String[] args) {
        String input = args[0];

        SparkConf conf = new SparkConf().setAppName("Real-time Twitter Stateless Exercise");
        AppConfig appConfig = AppConfig.getConfig();

        StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
        JavaStreamingContext jsc = new JavaStreamingContext(sc);
        jsc.checkpoint("/tmp/checkpoint");

        JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();

        // Transforming the map.tsv into a JavaPairRDD<LanguageCode, LanguageName>
        JavaRDD<String> inputRDD = jsc.sparkContext().textFile(input);
        JavaPairRDD<String, String> languages = LanguageMapUtils.buildLanguageMap(inputRDD).distinct(); //returns pair "es" "spanish"

        // stream -> RDD <languageCode> 
        JavaDStream<String> tweetLanguage = stream.map(tweet -> tweet.getLanguage()); //Transform DStream of tweets to a DStream of languages
        JavaPairDStream<String, Integer> tweetLanguageCount = tweetLanguage.mapToPair(str -> new Tuple2<>(str, 1))
                                                                        .reduceByKey((x, y) -> x + y); //DStream of languages to DStream of (language,1)
        //reduce per contar quantes vegades es repeteix un idioma

        //Join per tenir el nom complet, no el codi 
        JavaPairDStream<String, Tuple2<Integer, String>> tweetCompleteLanguageCount = tweetLanguageCount.transformToPair(rdd -> rdd.join(languages));
        JavaPairDStream<String, Integer> tweetLanguageCountWithName = tweetCompleteLanguageCount.mapToPair(tuple -> tuple._2)
                                                                                                .transformToPair(rdd -> rdd.sortByKey(false))
                                                                                                .mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1));
        tweetLanguageCountWithName.print(10);
        // Start the application and wait for termination signal
        sc.start();
        sc.awaitTermination();
    }
}
