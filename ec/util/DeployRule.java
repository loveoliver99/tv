package com.evangelsoft.econnect.util;

import java.util.HashMap;
import java.util.Iterator;

public class DeployRule {
    public DeployRule() {
    }

    public static boolean match(String var0, String var1, HashMap<String, String> var2) {
        if (var0.equals(var1)) {
            return true;
        } else if (var0.length() == 0) {
            return false;
        } else {
            byte var6 = 0;
            int var8 = var0.indexOf(123);
            int var7;
            if (var8 >= 0) {
                var7 = var8 - 1;
            } else {
                var7 = var0.length() - 1;
            }

            int var9;
            if (var8 >= 0) {
                var9 = var0.indexOf(125, var8 + 1);
            } else {
                var9 = -1;
            }

            int var11 = var0.length() - 1;
            String var3;
            String var4;
            String var5;
            if (var8 >= 0 && var8 < var9) {
                var3 = var0.substring(var6, var7 + 1);
                var4 = var0.substring(var8, var9 + 1);
                int var10 = var9 + 1;

                while(var10 < var0.length() && var0.charAt(var10) == '{') {
                    ++var10;

                    while(var10 < var0.length() && var0.charAt(var10) != '}') {
                        ++var10;
                    }

                    if (var10 > var0.length()) {
                        break;
                    }

                    ++var10;
                    if (var0.charAt(var10) != '{') {
                        break;
                    }
                }

                var5 = "";
                if (var10 >= 0 && var10 < var0.length()) {
                    var11 = var0.indexOf(123, var10) - 1;
                    if (var10 > var11) {
                        var11 = var0.length() - 1;
                    }

                    var5 = var0.substring(var10, var11 + 1);
                }
            } else {
                var3 = var0;
                var4 = "";
                var5 = "";
            }

            if (var4.length() == 0) {
                return var1.equals(var0);
            } else if (var1.indexOf(var3) != 0) {
                return false;
            } else {
                String var13 = "";
                boolean var12;
                if (var5.length() == 0) {
                    var12 = true;
                    var13 = var1.substring(var8);
                } else {
                    var12 = false;

                    int var15;
                    for(int var14 = var3.length(); var14 < var1.length() && (var15 = var1.indexOf(var5, var14)) >= var14; var14 = var15) {
                        if (match(var0.substring(var11 + 1), var1.substring(var15 + var5.length()), var2)) {
                            var12 = true;
                            var13 = var1.substring(var14, var15);
                            break;
                        }

                        ++var15;
                    }
                }

                if (var12 && var2 != null) {
                    var2.put(var4, var13);
                }

                return var12;
            }
        }
    }

    public static boolean match(String var0, String var1) {
        return match(var0, var1, (HashMap)null);
    }

    public static String apply(String var0, HashMap<String, String> var1) {
        Iterator var2 = var1.keySet().iterator();

        while(var2.hasNext()) {
            String var3 = (String)var2.next();
            String var4 = (String)var1.get(var3);

            for(int var5 = 0; var5 < var0.length() && (var5 = var0.indexOf(var3)) >= 0; var5 += var4.length()) {
                var0 = var0.substring(0, var5) + var4 + var0.substring(var5 + var3.length());
            }
        }

        return var0;
    }
}
