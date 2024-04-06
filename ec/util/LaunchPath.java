package com.evangelsoft.econnect.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

public class LaunchPath {
    public LaunchPath() {
    }

    private static boolean A(String var0, String var1) {
        return var1 == null || var0.toLowerCase().endsWith(var1.toLowerCase());
    }

    public static String[] listFileNames(String var0) {
        return listFileNames((String)null, var0);
    }

    public static String[] listFileNames(String var0, String var1) {
        String var2 = LaunchDirectory.getDirectory();
        char var3 = System.getProperty("file.separator").charAt(0);
        String var4 = var2;
        if (var0 != null && var0.length() > 0) {
            var4 = (var2 == null ? "" : var2 + var3) + var0;
        }

        ArrayList var5 = new ArrayList();
        File var6 = new File(var4);
        if (var6.exists()) {
            File[] var7 = var6.listFiles();
            if (var7 != null) {
                for(int var8 = 0; var8 < var7.length; ++var8) {
                    File var9 = var7[var8];
                    if (var9.isFile()) {
                        String var10 = (var0 != null && var0.length() > 0 ? var0 : "") + var9.getName();
                        if (var3 != '/') {
                            var10 = var10.replace(var3, '/');
                        }

                        if (A(var10, var1) && !var5.contains(var10)) {
                            var5.add(var10);
                        }
                    }
                }
            }
        }

        try {
            String var14 = var0 != null && var0.length() > 0 ? var0 : "";
            if (var3 != '/') {
                var14 = var14.replace(var3, '/');
            }

            Enumeration var15 = LaunchPath.class.getClassLoader().getResources(var14 + "ROOT.FILES");

            while(var15.hasMoreElements()) {
                URL var16 = (URL)var15.nextElement();
                Properties var17 = new Properties();
                var17.load(var16.openStream());
                Iterator var11 = var17.keySet().iterator();

                while(var11.hasNext()) {
                    String var12 = (String)var11.next();
                    if (var0 != null && var0.length() > 0) {
                        var12 = var0 + var12;
                    }

                    if (var3 != '/') {
                        var12 = var12.replace(var3, '/');
                    }

                    if (A(var12, var1) && !var5.contains(var12)) {
                        var5.add(var12);
                    }
                }
            }
        } catch (IOException var13) {
        }

        return (String[])var5.toArray(new String[0]);
    }
}
