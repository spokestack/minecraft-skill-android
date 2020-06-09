package io.spokestack.minecraft_android;

/**
 * Computes the Levenshtein edit distance between two strings.
 * <p>
 * Shamelessly copied from http://rosettacode.org/wiki/Levenshtein_distance#Iterative_space_optimized_.28even_bounded.29
 * to avoid another dependency.
 */
public class Levenshtein {

    /**
     * Compute the Levenshtein distance between two strings.
     * @param a The first string.
     * @param b The second string.
     * @return The Levenshtein distance between {@code a} and {@code b}.
     */
    public static int distance(String a, String b) {
        if (a.equals(b)) {
            return 0;
        }
        int la = a.length();
        int lb = b.length();
        if (la == 0) {
            return lb;
        }
        if (lb == 0) {
            return la;
        }
        if (la < lb) {
            int tl = la;
            la = lb;
            lb = tl;
            String ts = a;
            a = b;
            b = ts;
        }

        int[] cost = new int[lb + 1];
        for (int i = 1; i <= la; i++) {
            cost[0] = i;
            int prv = i - 1;
            int min = prv;
            for (int j = 1; j <= lb; j++) {
                int act = prv + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                prv = cost[j];
                cost[j] = min(1 + prv, 1 + cost[j - 1], act);
                if (prv < min) {
                    min = prv;
                }
            }
        }
        return cost[lb];
    }

    private static int min(int... arr) {
        int min = Integer.MAX_VALUE;
        for (int i : arr) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }
}
