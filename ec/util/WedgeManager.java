package com.evangelsoft.econnect.util;

import java.io.InputStream;
import java.util.Properties;

public class WedgeManager {
    public static final String wedgeMapFileName = "wedge.map";
    private static Properties A = new Properties();

    static {
        try {
            InputStream var0 = ResourceLocater.loadStream("wedge.map");
            if (var0 != null) {
                try {
                    A.load(var0);
                } catch (Throwable var3) {
                    System.out.println(var3.getMessage());
                }

                try {
                    var0.close();
                } catch (Throwable var2) {
                }
            }
        } catch (Throwable var4) {
            System.out.println(var4.getMessage());
        }

    }

    public WedgeManager() {
    }

    public static Object run(String var0, Object var1) throws Exception {
        String var2 = A.getProperty(var0);
        if (var2 != null && var2.length() != 0) {
            Object var3 = null;
            String[] var5 = var2.split(";");
            String[] var9 = var5;
            int var8 = var5.length;

            for(int var7 = 0; var7 < var8; ++var7) {
                String var6 = var9[var7];
                if (var6.length() != 0) {
                    Class var4 = Class.forName(var6);
                    Wedge var10 = (Wedge)var4.newInstance();
                    var3 = var10.run(var1);
                }
            }

            return var3;
        } else {
            return null;
        }
    }
}
