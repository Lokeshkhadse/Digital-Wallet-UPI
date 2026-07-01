package com.example.Kyc_Service.util;


import org.apache.commons.text.similarity.LevenshteinDistance;

public class FuzzyMatchingUtil {

    private FuzzyMatchingUtil() {
    }

    public static double calculateSimilarity(
            String source,
            String target) {

        if (source == null || target == null) {
            return 0;
        }

        source = source.trim().toUpperCase();
        target = target.trim().toUpperCase();

        int maxLength =
                Math.max(
                        source.length(),
                        target.length());

        if (maxLength == 0) {
            return 100;
        }

        LevenshteinDistance distance =
                new LevenshteinDistance();

        int result =
                distance.apply(
                        source,
                        target);

        return ((double)
                (maxLength - result)
                / maxLength) * 100;
    }
}
