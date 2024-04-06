package com.evangelsoft.econnect.util;

import com.evangelsoft.econnect.dataformat.VariantHolder;

public class ExceptionFormat {
    public ExceptionFormat() {
    }

    public static String format(Throwable var0) {
        String var1 = var0.getMessage();
        if ((var1 == null || var1.length() == 0) && var0.getCause() != null) {
            var1 = var0.getCause().getMessage();
        }

        if (var1 == null || var1.length() == 0) {
            var1 = var0.toString();
        }

        if (var1 == null) {
            var1 = "";
        }

        return var1;
    }

    public static void format(Throwable var0, VariantHolder<String> var1) {
        if (var1 != null) {
            var1.value = format(var0);
        }
    }
}
