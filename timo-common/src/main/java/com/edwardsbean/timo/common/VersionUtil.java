package com.edwardsbean.timo.common;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by edwardsbean on 2015/5/2 0002.
 */
public class VersionUtil {
    public static int compare(String version1, String version2) {
        int[] canonicalVersion1 = getCanonicalVersion(version1);
        int[] canonicalVersion2 = getCanonicalVersion(version2);
        return canonicalVersion1[0] < canonicalVersion2[0]?-1:(canonicalVersion1[0] > canonicalVersion2[0]?1:(canonicalVersion1[1] < canonicalVersion2[1]?-1:(canonicalVersion1[1] > canonicalVersion2[1]?1:(canonicalVersion1[2] < canonicalVersion2[2]?-1:(canonicalVersion1[2] > canonicalVersion2[2]?1:(canonicalVersion1[3] < canonicalVersion2[3]?-1:(canonicalVersion1[3] > canonicalVersion2[3]?1:0)))))));
    }

    public static int[] getCanonicalVersion(String version) {
        int[] canonicalVersion = new int[]{1, 0, 0, 0};
        String DASH_DELIM = "_";
        String DOT_DELIM = ".";
        StringTokenizer tokenizer = new StringTokenizer(version, ".");
        try {


        String token = tokenizer.nextToken();
        canonicalVersion[0] = Integer.parseInt(token);
        token = tokenizer.nextToken();
        StringTokenizer subTokenizer;
        if(token.indexOf("_") == -1) {
            canonicalVersion[1] = Integer.parseInt(token);
        } else {
            subTokenizer = new StringTokenizer(token, "_");
            canonicalVersion[1] = Integer.parseInt(subTokenizer.nextToken());
            canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
        }

        if(tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if(token.indexOf("_") == -1) {
                canonicalVersion[2] = Integer.parseInt(token);
                if(tokenizer.hasMoreTokens()) {
                    canonicalVersion[3] = Integer.parseInt(tokenizer.nextToken());
                }
            } else {
                subTokenizer = new StringTokenizer(token, "_");
                canonicalVersion[2] = Integer.parseInt(subTokenizer.nextToken());
                canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
            }
        }
        } catch (NoSuchElementException e) {
        }
        return canonicalVersion;
    }
}
