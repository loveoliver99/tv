package com.evangelsoft.econnect.util;

import java.io.UnsupportedEncodingException;

public class Encrypter {
    private static String A = decrypt(Encrypter.class.getName().substring(Encrypter.class.getName().length() - 8, Encrypter.class.getName().length()), "A619FC1DFB6436DA");

    public Encrypter() {
    }

    public static String encrypt(String var0) {
        return encrypt(A, var0);
    }

    public static String encryptEx(String var0, String var1) throws UnsupportedEncodingException {
        return encryptEx(A, var0, var1);
    }

    public static String encrypt(String var0, String var1) {
        if (var1 == null) {
            var1 = "";
        }

        return StringUtilities.encodeHexString((new Des(var0)).encrypt(var1.getBytes()));
    }

    public static String encryptEx(String var0, String var1, String var2) throws UnsupportedEncodingException {
        if (var1 == null) {
            var1 = "";
        }

        return StringUtilities.encodeHexString((new Des(var0)).encrypt(var1.getBytes(var2)));
    }

    public static String decrypt(String var0) {
        return decrypt(A, var0);
    }

    public static String decryptEx(String var0, String var1) throws UnsupportedEncodingException {
        return decryptEx(A, var0, var1);
    }

    public static String decrypt(String var0, String var1) {
        if (var1 == null) {
            var1 = "";
        }

        byte[] var2 = (new Des(var0)).decrypt(StringUtilities.decodeHexString(var1));

        int var3;
        for(var3 = var2.length - 1; var3 >= 0 && var2[var3] == 0; --var3) {
        }

        return new String(var2, 0, var3 + 1);
    }

    public static String decryptEx(String var0, String var1, String var2) throws UnsupportedEncodingException {
        if (var1 == null) {
            var1 = "";
        }

        byte[] var3 = (new Des(var0)).decrypt(StringUtilities.decodeHexString(var1));

        int var4;
        for(var4 = var3.length - 1; var4 >= 0 && var3[var4] == 0; --var4) {
        }

        return new String(var3, 0, var4 + 1, var2);
    }

    public static String generateCkc(String var0) {
        return generateCkc(A, var0);
    }

    public static String generateCkcEx(String var0, String var1) throws UnsupportedEncodingException {
        return generateCkcEx(A, var0, var1);
    }

    public static String generateCkc(String var0, String var1) {
        if (var1 == null) {
            var1 = "";
        }

        byte[] var2 = new byte[16];
        byte[] var3 = var1.getBytes();

        for(int var4 = 0; var4 < var3.length; ++var4) {
            int var5 = var4 % var2.length;
            var2[var5] += var3[var4];
        }

        return StringUtilities.encodeHexString((new Des(var0)).encrypt(var2));
    }

    public static String generateCkcEx(String var0, String var1, String var2) throws UnsupportedEncodingException {
        if (var1 == null) {
            var1 = "";
        }

        byte[] var3 = new byte[16];
        byte[] var4 = var1.getBytes(var2);

        for(int var5 = 0; var5 < var4.length; ++var5) {
            int var6 = var5 % var3.length;
            var3[var6] += var4[var5];
        }

        return StringUtilities.encodeHexString((new Des(var0)).encrypt(var3));
    }
}
