package com.evangelsoft.econnect.util;

import java.io.InputStream;
import java.util.Properties;

public class UnitManager {
    public static final String unitMapFileName = "unit.map";
    private static Properties A = new Properties();

    static {
        try {
            InputStream var0 = ResourceLocater.loadStream("unit.map");
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

    public UnitManager() {
    }

    public static Object getUnit(Class<?> var0) throws Exception {
        String var1 = A.getProperty(var0.getName());
        return var1 != null && var1.length() != 0 ? Class.forName(var1).newInstance() : null;
    }
}
