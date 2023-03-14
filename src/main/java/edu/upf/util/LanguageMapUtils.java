package edu.upf.util;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import scala.Tuple2;

public class LanguageMapUtils {

    public static JavaPairRDD<String, String> buildLanguageMap(JavaRDD<String> lines) {
//        return null;// IMPLEMENT ME
        return lines
                .map(l -> l.split("\t"))
                .mapToPair(splits -> Tuple2.apply(splits[1], splits[2]));
    }
}
