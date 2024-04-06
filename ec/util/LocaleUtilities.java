package com.evangelsoft.econnect.util;

import java.util.HashMap;
import java.util.Locale;

public class LocaleUtilities {
    private static HashMap<String, Locale> A = new HashMap();

    static {
        Locale[] var0 = Locale.getAvailableLocales();
        Locale[] var4 = var0;
        int var3 = var0.length;

        for(int var2 = 0; var2 < var3; ++var2) {
            Locale var1 = var4[var2];
            A.put(var1.toString(), var1);
        }

    }

    public LocaleUtilities() {
    }

    public static Locale getLocale(String var0) {
        return (Locale)A.get(var0);
    }
}
