//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.evangelsoft.econnect.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities {
    public DateUtilities() {
    }

    public static Date getNextTime(String pattern, Date referenceDate) {
        if (pattern != null && pattern.length() == 14 && referenceDate != null) {
            Integer[] var2 = new Integer[6];
            int[] var3 = new int[]{4, 2, 2, 2, 2, 2};
            int var4 = 0;

            for(int var5 = 0; var5 < 6; ++var5) {
                try {
                    var2[var5] = Integer.valueOf(pattern.substring(var4, var4 + var3[var5]));
                    if (var5 == 1) {
                        var2[var5] = var2[var5] - 1;
                    }
                } catch (Throwable var13) {
                }

                var4 += var3[var5];
            }

            int[] var14 = new int[]{1, 2, 5, 11, 12, 13};
            Calendar var6 = Calendar.getInstance();
            var6.setTime(referenceDate);
            Integer[] var7 = new Integer[6];

            for(int var8 = 0; var8 < 6; ++var8) {
                var7[var8] = var6.get(var14[var8]);
            }

            var6.clear();
            boolean var15 = false;

            for(int var9 = 0; var9 < 6; ++var9) {
                if (var2[var9] == null) {
                    var6.set(var14[var9], var15 ? (var9 == 2 ? 1 : 0) : var7[var9]);
                } else {
                    if (!var15) {
                        var15 = var2[var9].compareTo(var7[var9]) > 0;
                    }

                    var6.set(var14[var9], var2[var9]);
                }
            }

            Date var16 = var6.getTime();
            if (var16.compareTo(referenceDate) > 0) {
                return var16;
            } else {
                var16 = null;

                for(int var10 = 5; var10 >= 0; --var10) {
                    for(int var11 = var10; var11 >= 0; --var11) {
                        if (var2[var11] == null) {
                            var6.add(var14[var11], 1);

                            for(int var12 = var11 - 1; var12 >= 0; --var12) {
                                if (var2[var12] != null) {
                                    var6.set(var14[var12], var2[var12]);
                                }
                            }

                            if (var6.getTime().compareTo(referenceDate) > 0) {
                                var16 = var6.getTime();
                                break;
                            }
                        }
                    }

                    if (var16 != null) {
                        break;
                    }
                }

                return var16;
            }
        } else {
            return null;
        }
    }

    public static long getTimeInMillisecond(String duration) {
        long var1 = 0L;

        try {
            if (duration != null) {
                if (duration.endsWith("H")) {
                    var1 = (long)(Integer.parseInt(duration.substring(0, duration.length() - 1)) * 60 * 60 * 1000);
                } else if (duration.endsWith("N")) {
                    var1 = (long)(Integer.parseInt(duration.substring(0, duration.length() - 1)) * 60 * 1000);
                } else if (duration.endsWith("S")) {
                    var1 = (long)(Integer.parseInt(duration.substring(0, duration.length() - 1)) * 1000);
                }
            }
        } catch (Throwable var4) {
        }

        return var1;
    }

    public static Date skip(Date date, String interval, boolean forward) {
        Calendar var3 = Calendar.getInstance();
        var3.setTime(date);
        int var4 = 0;

        try {
            var4 = Integer.parseInt(interval.substring(0, interval.length() - 1));
        } catch (Throwable var6) {
        }

        if (!forward) {
            var4 = -var4;
        }

        if (var4 != 0) {
            switch(interval.charAt(interval.length() - 1)) {
            case 'D':
                var3.add(6, var4);
                break;
            case 'H':
                var3.add(11, var4);
                break;
            case 'M':
                var3.add(2, var4);
                break;
            case 'N':
                var3.add(12, var4);
                break;
            case 'S':
                var3.add(13, var4);
                break;
            case 'Y':
                var3.add(1, var4);
            }
        }

        return new Date(var3.getTimeInMillis());
    }

    public static Date getDateSection(Date date) {
        Calendar var1 = Calendar.getInstance();
        var1.setTime(date);
        int var2 = var1.get(1);
        int var3 = var1.get(2);
        int var4 = var1.get(5);
        var1.clear();
        var1.set(var2, var3, var4);
        return new Date(var1.getTimeInMillis());
    }

    public static Date getTimeSection(Date date) {
        Calendar var1 = Calendar.getInstance();
        var1.setTime(date);
        int var2 = var1.get(11);
        int var3 = var1.get(12);
        int var4 = var1.get(13);
        var1.clear();
        var1.set(11, var2);
        var1.set(12, var3);
        var1.set(13, var4);
        return new Date(var1.getTimeInMillis());
    }
}
