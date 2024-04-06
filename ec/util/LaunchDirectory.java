package com.evangelsoft.econnect.util;

import java.io.File;

public class LaunchDirectory {
    private static String A = null;

    public LaunchDirectory() {
    }

    public static String getDirectory() {
        String var0 = A;
        if (var0 == null) {
            var0 = System.getProperty("launchDirectory");
            if (var0 == null || var0.length() == 0) {
                var0 = "user.dir";
            }

            if (var0.equals("user.home") || var0.equals("user.dir")) {
                var0 = System.getProperty(var0);
            }

            if (var0 == null) {
                var0 = "";
            }

            A = var0;
        }

        return var0;
    }

    public static String getFileName(String var0) {
        String var1 = getDirectory();
        if (var1.length() > 0 && var1.charAt(var1.length() - 1) != File.separatorChar) {
            var1 = var1 + File.separator;
        }

        return var1 + var0;
    }
}
