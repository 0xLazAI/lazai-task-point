package com.lazai.utils;

import java.security.SecureRandom;
import java.util.*;

public class RandomStringUtils {

    /** Common alphabets */
    public static final char[] ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static final char[] ALPHANUM_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    public static final char[] ALPHANUM_MIXED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    public static final char[] HEX_UPPER = "0123456789ABCDEF".toCharArray();
    public static final char[] BASE32_CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray(); // no I,L,O,U

    private static final SecureRandom RNG = new SecureRandom();

    /**
     * Generate ONE random string.
     */
    public static String one(int len, char[] alphabet) {
        Objects.requireNonNull(alphabet, "alphabet");
        if (len <= 0) throw new IllegalArgumentException("len must be > 0");
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            buf[i] = alphabet[RNG.nextInt(alphabet.length)];
        }
        return new String(buf);
    }

    /**
     * Generate `count` **unique** random strings.
     * Uses rejection sampling; fine when count << alphabet^len.
     *
     * @throws IllegalArgumentException if count exceeds the total combinatorial space
     *                                  (optional pre-check; disable by passing skipSpaceCheck=true overload)
     */
    public static List<String> uniqueStrings(int count, int len, char[] alphabet) {
        return uniqueStrings(count, len, alphabet, false);
    }

    /**
     * Generate `count` unique random strings, optionally skipping space check.
     */
    public static List<String> uniqueStrings(int count, int len, char[] alphabet, boolean skipSpaceCheck) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        if (!skipSpaceCheck) {
            checkSpace(count, len, alphabet.length);
        }
        Set<String> seen = new HashSet<>(Math.max(16, count * 2));
        while (seen.size() < count) {
            seen.add(one(len, alphabet));
        }
        return new ArrayList<>(seen);
    }

    /**
     * Hard upper bound check: alphabet^len must be >= count.
     * Uses BigInteger to avoid overflow.
     */
    private static void checkSpace(int count, int len, int alphaSize) {
        // quick cheap checks
        if (count == 0) return;
        if (alphaSize <= 1) {
            if (count > 1) throw new IllegalArgumentException("Alphabet of size 1 cannot produce >1 unique string.");
            return;
        }
        // approximate: if pow can exceed Long.MAX, skip exact but assume ok
        // safer exact using BigInteger:
        java.math.BigInteger space = java.math.BigInteger.valueOf(alphaSize).pow(len);
        if (space.compareTo(java.math.BigInteger.valueOf(count)) < 0) {
            throw new IllegalArgumentException("Requested " + count + " uniques, but space=" + space + " < count.");
        }
    }

    /**
     * Deterministic "shuffle space" approach (only for SMALL spaces):
     * Enumerate all possible strings up to spaceLimit, shuffle, take first count.
     * EXPLOSIVE growth — only use when alphabet^len <= spaceLimit (e.g., 36^3 ~ 46k OK).
     */
    public static List<String> enumerateAndShuffle(int count, int len, char[] alphabet, int spaceLimit) {
        int alpha = alphabet.length;
        long space = (long) Math.pow(alpha, len);
        if (space > spaceLimit) {
            throw new IllegalArgumentException("Space too large (" + space + ") for enumeration.");
        }
        if (count > space) {
            throw new IllegalArgumentException("count > space");
        }
        List<String> all = new ArrayList<>((int) space);
        char[] buf = new char[len];
        enumerateRecursive(all, buf, 0, alphabet);
        Collections.shuffle(all, RNG);
        return all.subList(0, count);
    }

    private static void enumerateRecursive(List<String> out, char[] buf, int pos, char[] alphabet) {
        if (pos == buf.length) {
            out.add(new String(buf));
            return;
        }
        for (char c : alphabet) {
            buf[pos] = c;
            enumerateRecursive(out, buf, pos + 1, alphabet);
        }
    }

}
