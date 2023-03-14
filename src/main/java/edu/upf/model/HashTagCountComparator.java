package edu.upf.model;

import java.util.Comparator;

public class HashTagCountComparator implements Comparator<HashTagCount> {
    @Override
    public int compare(HashTagCount o1, HashTagCount o2) {
        return Comparator
                .comparingLong((HashTagCount ht ) -> ht.count )
                .thenComparing(ht -> ht.lang)
                .thenComparing(ht -> ht.hashTag)
                .compare(o1, o2);
    }
}