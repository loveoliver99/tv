package com.evangelsoft.econnect.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLocater {
    public ResourceLocater() {
    }

    public static InputStream loadStream(String var0) {
        Object var1 = null;

        try {
            var1 = new FileInputStream(LaunchDirectory.getFileName(var0));
        } catch (Throwable var5) {
            if (var1 != null) {
                try {
                    ((InputStream)var1).close();
                } catch (IOException var4) {
                }
            }

            var1 = null;
        }

        if (var1 == null) {
            var1 = ResourceLocater.class.getClassLoader().getResourceAsStream(var0);
        }

        return (InputStream)var1;
    }
}
