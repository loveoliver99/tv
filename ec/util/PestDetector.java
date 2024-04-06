package com.evangelsoft.econnect.util;

import com.evangelsoft.econnect.plant.WaiterFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

public class PestDetector {
    private static String A = Encrypter.decrypt("0CAEF985B2EBA7EAE3EB5FEFC02D5076");
    private static Hashtable<String, String> C = new Hashtable();
    private static Hashtable<String, String> B = new Hashtable();

    public PestDetector() {
    }

    public static boolean detect(String var0) {
        if (var0 != null && var0.length() != 0) {
            boolean var1 = true;
            String[] var2 = var0.split(";");
            String[] var6 = var2;
            int var5 = var2.length;

            for(int var4 = 0; var4 < var5; ++var4) {
                String var3 = var6[var4];
                var3 = var3.trim();
                if (var3.endsWith("?")) {
                    try {
                        var3 = WaiterFactory.getWaiterClassName(Class.forName(var3.substring(0, var3.length() - 1)));
                        var3 = var3.replace('.', '/') + ".class";
                    } catch (ClassNotFoundException var18) {
                    }
                }

                if (var3.length() > 0) {
                    String var7 = (String)C.get(var3);
                    if (var7 == null) {
                        InputStream var8 = ResourceLocater.class.getClassLoader().getResourceAsStream(var3);
                        if (var8 != null) {
                            try {
                                var7 = generateStamp(var8);
                                C.put(var3, var7);
                                var8.close();
                            } catch (IOException var17) {
                            }
                        }
                    }

                    if (var7 != null) {
                        String var20 = (String)B.get(var3);
                        if (var20 == null) {
                            int var9 = var3.lastIndexOf(47);
                            if (var9 < 0) {
                                var9 = 0;
                            } else {
                                ++var9;
                            }

                            String var10 = var3.substring(0, var9) + A;
                            InputStream var11 = ResourceLocater.class.getClassLoader().getResourceAsStream(var10);
                            if (var11 != null) {
                                try {
                                    byte[] var12 = new byte[var11.available()];
                                    var11.read(var12);
                                    String var13 = new String(var12);
                                    var13 = Encrypter.decrypt(var13);
                                    var11.close();
                                    ByteArrayInputStream var21 = new ByteArrayInputStream(var13.getBytes());
                                    Properties var14 = new Properties();
                                    var14.load(var21);
                                    var21.close();
                                    var20 = var14.getProperty(var3);
                                    Iterator var16 = var14.keySet().iterator();

                                    while(var16.hasNext()) {
                                        Object var15 = var16.next();
                                        B.put((String)var15, (String)var14.get(var15));
                                    }
                                } catch (IOException var19) {
                                }
                            }
                        }

                        if (var20 == null || !var7.equals(var20)) {
                            var1 = false;
                            break;
                        }
                    }
                }
            }

            return var1;
        } else {
            return true;
        }
    }

    public static String generateStamp(InputStream var0) throws IOException {
        byte[] var1 = new byte[4];
        byte[] var2 = new byte[4];

        int var3;
        for(int var4 = 0; (var3 = var0.read(var2)) > 0; var4 = (var4 + var3) % 4) {
            for(int var5 = 0; var5 < var3; ++var5) {
                var1[(var4 + var5) % 4] ^= var2[var5];
            }
        }

        String var6 = StringUtilities.encodeHexString(var1);
        return Encrypter.encrypt(var6, var6);
    }
}
