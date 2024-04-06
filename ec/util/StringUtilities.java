package com.evangelsoft.econnect.util;

public class StringUtilities {
    private static final byte[] A = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    public StringUtilities() {
    }

    private static final byte A(char var0) {
        switch(var0) {
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '_':
        case '`':
        default:
            return 0;
        case 'A':
            return 10;
        case 'B':
            return 11;
        case 'C':
            return 12;
        case 'D':
            return 13;
        case 'E':
            return 14;
        case 'F':
            return 15;
        case 'a':
            return 10;
        case 'b':
            return 11;
        case 'c':
            return 12;
        case 'd':
            return 13;
        case 'e':
            return 14;
        case 'f':
            return 15;
        }
    }

    public static String stringOfChar(char var0, int var1) {
        char[] var2 = new char[var1];

        for(int var3 = 0; var3 < var1; ++var3) {
            var2[var3] = var0;
        }

        return new String(var2);
    }

    public static boolean match(String var0, String var1) {
        if (var0.length() == 0) {
            return true;
        } else {
            String[] var2 = var0.split("%");
            if (var2.length == 0) {
                return true;
            } else {
                boolean var3 = var0.charAt(0) == '%';
                boolean var4 = var0.charAt(var0.length() - 1) == '%';
                boolean var5 = true;
                int var6 = 0;

                for(int var7 = 0; var7 < var2.length; ++var7) {
                    String var8 = var2[var7];
                    if (var8.length() != 0) {
                        boolean var9 = false;

                        for(int var10 = var6; var10 <= var1.length() - var8.length(); ++var10) {
                            boolean var11 = true;

                            for(int var12 = 0; var12 < var8.length(); ++var12) {
                                if (var8.charAt(var12) != '_' && Character.toUpperCase(var8.charAt(var12)) != Character.toUpperCase(var1.charAt(var10 + var12))) {
                                    var11 = false;
                                    break;
                                }
                            }

                            if (var11) {
                                var9 = true;
                                var6 = var10 + var8.length();
                                break;
                            }

                            if (!var3 && var7 == 0) {
                                break;
                            }
                        }

                        if (!var9) {
                            var5 = false;
                            break;
                        }
                    }
                }

                if (var6 < var1.length() && !var4) {
                    var5 = false;
                }

                return var5;
            }
        }
    }

    public static String encodeHexString(byte[] var0) {
        return encodeHexString(var0, 0, var0.length);
    }

    public static String encodeHexString(byte[] var0, int var1, int var2) {
        byte[] var3 = new byte[2 * var2];
        int var4 = 0;

        for(int var5 = var1; var5 < var2; ++var5) {
            byte var6 = var0[var5];
            int var7 = var6 & 255;
            var3[var4++] = A[var7 >>> 4];
            var3[var4++] = A[var7 & 15];
        }

        return new String(var3);
    }

    public static byte[] decodeHexString(String var0) {
        char[] var1 = var0.toCharArray();
        byte[] var2 = new byte[var1.length / 2];

        for(int var3 = 0; var3 < var2.length; ++var3) {
            byte var4 = 0;
            byte var5 = (byte)(var4 | A(var1[var3 * 2]));
            var5 = (byte)(var5 << 4);
            var5 |= A(var1[var3 * 2 + 1]);
            var2[var3] = var5;
        }

        return var2;
    }

    public static String encodeObfuscatedString(byte[] var0) {
        byte[] var1 = new byte[var0.length / 8 * 9 + (var0.length % 8 != 0 ? 9 : 0)];

        int var3;
        int var4;
        for(int var2 = 0; var2 < var1.length / 9; ++var2) {
            var3 = var2 == var1.length - 1 ? var0.length - 8 * (var2 - 1) : 8;
            System.arraycopy(var0, 8 * var2, var1, 9 * var2, var3);
            if (var3 < 8) {
                for(var4 = var3; var4 < 8; ++var4) {
                    var1[9 * var2 + var4] = 0;
                }
            }

            var1[9 * var2 + 8] = 32;
        }

        byte[] var5 = new byte[var1.length * 2];

        for(var3 = 0; var3 < var1.length; ++var3) {
            var5[var3 * 2] = (byte)((var1[var3] & 240) >> 4);
            var5[var3 * 2 + 1] = (byte)(var1[var3] & 15);
        }

        byte[] var6 = new byte[var5.length * 2 / 3];

        for(var4 = 0; var4 < var5.length / 3; ++var4) {
            var6[2 * var4] = (byte)(((var5[3 * var4] & 15) << 2 | (var5[3 * var4 + 1] & 12) >> 2) + 48);
            var6[2 * var4 + 1] = (byte)(((var5[3 * var4 + 1] & 3) << 4 | var5[3 * var4 + 2] & 15) + 48);
        }

        return new String(var6);
    }

    public static byte[] decodeObfuscatedString(String var0) {
        byte[] var1 = var0.getBytes();
        byte[] var2;
        if (var1.length % 12 != 0) {
            var2 = new byte[var1.length + 12 - var1.length % 12];
            System.arraycopy(var1, 0, var2, 0, var1.length);

            for(int var3 = var1.length; var3 < var2.length; ++var3) {
                var2[var3] = 0;
            }
        } else {
            var2 = var1;
        }

        byte[] var7 = new byte[var2.length * 3 / 2];

        for(int var4 = 0; var4 < var2.length / 2; ++var4) {
            var7[3 * var4] = (byte)((var2[2 * var4] & 255) - 48 >> 2);
            var7[3 * var4 + 1] = (byte)((((var2[2 * var4] & 255) - 48 & 3) << 2) + ((var2[2 * var4 + 1] & 255) - 48 >> 4));
            var7[3 * var4 + 2] = (byte)((var2[2 * var4 + 1] & 255) - 48 & 15);
        }

        byte[] var8 = new byte[var7.length * 4 / 9];
        int var5 = 0;

        for(int var6 = 0; var5 < var8.length; ++var6) {
            if (var5 > 0 && var5 % 8 == 0) {
                ++var6;
            }

            var8[var5] = (byte)((var7[2 * var6] & 15) << 4 | var7[2 * var6 + 1] & 15);
            ++var5;
        }

        return var8;
    }

    public static String utf8ToUnicode(String var0) {
        return utf8ToUnicode(var0, (StringBuffer)null);
    }

    public static String utf8ToUnicode(String var0, StringBuffer var1) {
        int var2 = 0;
        int var3 = var0.length();
        StringBuffer var4;
        if (var1 == null) {
            var4 = new StringBuffer();
        } else {
            var4 = var1;
            var1.setLength(0);
        }

        while(true) {
            while(true) {
                while(var2 < var3) {
                    char var5 = var0.charAt(var2++);
                    if (var5 == '\\') {
                        if (var2 + 4 < var3 && var0.charAt(var2) == 'u') {
                            ++var2;
                            int var6 = 0;

                            for(int var7 = 0; var7 < 4; ++var7) {
                                var5 = var0.charAt(var2++);
                                switch(var5) {
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                    var6 = (var6 << 4) + var5 - 48;
                                case ':':
                                case ';':
                                case '<':
                                case '=':
                                case '>':
                                case '?':
                                case '@':
                                case 'G':
                                case 'H':
                                case 'I':
                                case 'J':
                                case 'K':
                                case 'L':
                                case 'M':
                                case 'N':
                                case 'O':
                                case 'P':
                                case 'Q':
                                case 'R':
                                case 'S':
                                case 'T':
                                case 'U':
                                case 'V':
                                case 'W':
                                case 'X':
                                case 'Y':
                                case 'Z':
                                case '[':
                                case '\\':
                                case ']':
                                case '^':
                                case '_':
                                case '`':
                                default:
                                    break;
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                    var6 = (var6 << 4) + 10 + var5 - 65;
                                    break;
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                    var6 = (var6 << 4) + 10 + var5 - 97;
                                }
                            }

                            var4.append((char)var6);
                        } else {
                            var4.append(var5);
                            if (var2 < var3 && var0.charAt(var2) == '\\') {
                                ++var2;
                            }
                        }
                    } else {
                        var4.append(var5);
                    }
                }

                return var4.toString();
            }
        }
    }

    public static String unicodeToUtf8(String var0) {
        return unicodeToUtf8(var0, (StringBuffer)null);
    }

    public static String unicodeToUtf8(String var0, StringBuffer var1) {
        int var2 = var0.length();
        StringBuffer var3;
        if (var1 == null) {
            var3 = new StringBuffer();
        } else {
            var3 = var1;
            var1.setLength(0);
        }

        for(int var4 = 0; var4 < var2; ++var4) {
            char var5 = var0.charAt(var4);
            if (var5 > '~') {
                var3.append('\\');
                var3.append('u');
                var3.append((char)A[var5 >> 12 & 15]);
                var3.append((char)A[var5 >> 8 & 15]);
                var3.append((char)A[var5 >> 4 & 15]);
                var3.append((char)A[var5 & 15]);
            } else {
                if (var5 == '\\') {
                    var3.append('\\');
                }

                var3.append(var5);
            }
        }

        return var3.toString();
    }
}
