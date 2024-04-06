package com.evangelsoft.econnect.util;

public class Des {
    public static final String DIR_ENCRYPT = "ENCRYPT";
    public static final String DIR_DECRYPT = "DECRYPT";
    private static final char[][] B = new char[][]{{'\u000e', '\u0004', '\r', '\u0001', '\u0002', '\u000f', '\u000b', '\b', '\u0003', '\n', '\u0006', '\f', '\u0005', '\t', '\u0000', '\u0007', '\u0000', '\u000f', '\u0007', '\u0004', '\u000e', '\u0002', '\r', '\u0001', '\n', '\u0006', '\f', '\u000b', '\t', '\u0005', '\u0003', '\b', '\u0004', '\u0001', '\u000e', '\b', '\r', '\u0006', '\u0002', '\u000b', '\u000f', '\f', '\t', '\u0007', '\u0003', '\n', '\u0005', '\u0000', '\u000f', '\f', '\b', '\u0002', '\u0004', '\t', '\u0001', '\u0007', '\u0005', '\u000b', '\u0003', '\u000e', '\n', '\u0000', '\u0006', '\r'}, {'\u000f', '\u0001', '\b', '\u000e', '\u0006', '\u000b', '\u0003', '\u0004', '\t', '\u0007', '\u0002', '\r', '\f', '\u0000', '\u0005', '\n', '\u0003', '\r', '\u0004', '\u0007', '\u000f', '\u0002', '\b', '\u000e', '\f', '\u0000', '\u0001', '\n', '\u0006', '\t', '\u000b', '\u0005', '\u0000', '\u000e', '\u0007', '\u000b', '\n', '\u0004', '\r', '\u0001', '\u0005', '\b', '\f', '\u0006', '\t', '\u0003', '\u0002', '\u000f', '\r', '\b', '\n', '\u0001', '\u0003', '\u000f', '\u0004', '\u0002', '\u000b', '\u0006', '\u0007', '\f', '\u0000', '\u0005', '\u000e', '\t'}, {'\n', '\u0000', '\t', '\u000e', '\u0006', '\u0003', '\u000f', '\u0005', '\u0001', '\r', '\f', '\u0007', '\u000b', '\u0004', '\u0002', '\b', '\r', '\u0007', '\u0000', '\t', '\u0003', '\u0004', '\u0006', '\n', '\u0002', '\b', '\u0005', '\u000e', '\f', '\u000b', '\u000f', '\u0001', '\r', '\u0006', '\u0004', '\t', '\b', '\u000f', '\u0003', '\u0000', '\u000b', '\u0001', '\u0002', '\f', '\u0005', '\n', '\u000e', '\u0007', '\u0001', '\n', '\r', '\u0000', '\u0006', '\t', '\b', '\u0007', '\u0004', '\u000f', '\u000e', '\u0003', '\u000b', '\u0005', '\u0002', '\f'}, {'\u0007', '\r', '\u000e', '\u0003', '\u0000', '\u0006', '\t', '\n', '\u0001', '\u0002', '\b', '\u0005', '\u000b', '\f', '\u0004', '\u000f', '\r', '\b', '\u000b', '\u0005', '\u0006', '\u000f', '\u0000', '\u0003', '\u0004', '\u0007', '\u0002', '\f', '\u0001', '\n', '\u000e', '\t', '\n', '\u0006', '\t', '\u0000', '\f', '\u000b', '\u0007', '\r', '\u000f', '\u0001', '\u0003', '\u000e', '\u0005', '\u0002', '\b', '\u0004', '\u0003', '\u000f', '\u0000', '\u0006', '\n', '\u0001', '\r', '\b', '\t', '\u0004', '\u0005', '\u000b', '\f', '\u0007', '\u0002', '\u000e'}, {'\u0002', '\f', '\u0004', '\u0001', '\u0007', '\n', '\u000b', '\u0006', '\b', '\u0005', '\u0003', '\u000f', '\r', '\u0000', '\u000e', '\t', '\u000e', '\u000b', '\u0002', '\f', '\u0004', '\u0007', '\r', '\u0001', '\u0005', '\u0000', '\u000f', '\n', '\u0003', '\t', '\b', '\u0006', '\u0004', '\u0002', '\u0001', '\u000b', '\n', '\r', '\u0007', '\b', '\u000f', '\t', '\f', '\u0005', '\u0006', '\u0003', '\u0000', '\u000e', '\u000b', '\b', '\f', '\u0007', '\u0001', '\u000e', '\u0002', '\r', '\u0006', '\u000f', '\u0000', '\t', '\n', '\u0004', '\u0005', '\u0003'}, {'\f', '\u0001', '\n', '\u000f', '\t', '\u0002', '\u0006', '\b', '\u0000', '\r', '\u0003', '\u0004', '\u000e', '\u0007', '\u0005', '\u000b', '\n', '\u000f', '\u0004', '\u0002', '\u0007', '\f', '\t', '\u0005', '\u0006', '\u0001', '\r', '\u000e', '\u0000', '\u000b', '\u0003', '\b', '\t', '\u000e', '\u000f', '\u0005', '\u0002', '\b', '\f', '\u0003', '\u0007', '\u0000', '\u0004', '\n', '\u0001', '\r', '\u000b', '\u0006', '\u0004', '\u0003', '\u0002', '\f', '\t', '\u0005', '\u000f', '\n', '\u000b', '\u000e', '\u0001', '\u0007', '\u0006', '\u0000', '\b', '\r'}, {'\u0004', '\u000b', '\u0002', '\u000e', '\u000f', '\u0000', '\b', '\r', '\u0003', '\f', '\t', '\u0007', '\u0005', '\n', '\u0006', '\u0001', '\r', '\u0000', '\u000b', '\u0007', '\u0004', '\t', '\u0001', '\n', '\u000e', '\u0003', '\u0005', '\f', '\u0002', '\u000f', '\b', '\u0006', '\u0001', '\u0004', '\u000b', '\r', '\f', '\u0003', '\u0007', '\u000e', '\n', '\u000f', '\u0006', '\b', '\u0000', '\u0005', '\t', '\u0002', '\u0006', '\u000b', '\r', '\b', '\u0001', '\u0004', '\n', '\u0007', '\t', '\u0005', '\u0000', '\u000f', '\u000e', '\u0002', '\u0003', '\f'}, {'\r', '\u0002', '\b', '\u0004', '\u0006', '\u000f', '\u000b', '\u0001', '\n', '\t', '\u0003', '\u000e', '\u0005', '\u0000', '\f', '\u0007', '\u0001', '\u000f', '\r', '\b', '\n', '\u0003', '\u0007', '\u0004', '\f', '\u0005', '\u0006', '\u000b', '\u0000', '\u000e', '\t', '\u0002', '\u0007', '\u000b', '\u0004', '\u0001', '\t', '\f', '\u000e', '\u0002', '\u0000', '\u0006', '\n', '\r', '\u000f', '\u0003', '\u0005', '\b', '\u0002', '\u0001', '\u000e', '\u0007', '\u0004', '\n', '\b', '\r', '\u000f', '\f', '\t', '\u0000', '\u0003', '\u0005', '\u0006', '\u000b'}};
    private char[] A = new char[8];

    public Des(String var1) {
        this.A(var1.getBytes());
    }

    public Des(byte[] var1) {
        this.A(var1);
    }

    public Des(char[] var1) {
        this.A(var1);
    }

    public byte[] encrypt(byte[] var1) {
        return this.desProcess("ENCRYPT", (byte[])var1, 0, var1.length);
    }

    public byte[] decrypt(byte[] var1) {
        return this.desProcess("DECRYPT", (byte[])var1, 0, var1.length);
    }

    public char[] encrypt(char[] var1) {
        return this.desProcess("ENCRYPT", (char[])var1, 0, var1.length);
    }

    public char[] decrypt(char[] var1) {
        return this.desProcess("DECRYPT", (char[])var1, 0, var1.length);
    }

    public byte[] desProcess(String var1, byte[] var2, int var3, int var4) {
        char[] var5 = new char[var4];

        for(int var6 = var3; var6 < var3 + var4; ++var6) {
            if (var2[var6] >= 0) {
                var5[var6] = (char)var2[var6];
            } else {
                var5[var6] = (char)(256 + var2[var6]);
            }
        }

        char[] var9 = this.desProcess(var1, (char[])var5, 0, var4);
        byte[] var7 = new byte[var9.length];

        for(int var8 = 0; var8 < var7.length; ++var8) {
            var7[var8] = (byte)var9[var8];
        }

        return var7;
    }

    public char[] desProcess(String var1, char[] var2, int var3, int var4) {
        int var5 = var2.length / 8 * 8 + (var2.length % 8 == 0 ? 0 : 8);
        char[] var6 = new char[var5];

        for(char[] var7 = new char[8]; var4 > 0; var4 -= 8) {
            System.arraycopy(var2, var3, var7, 0, var4 > 8 ? 8 : var4);

            for(int var8 = var4; var8 < 8; ++var8) {
                var7[var8] = 0;
            }

            this.desProcess8chars(var1, var7);
            System.arraycopy(var7, 0, var6, var3, 8);
            var3 += 8;
        }

        return var6;
    }

    public void desProcess8chars(String var1, char[] var2) {
        char[] var3 = new char[8];
        int[] var4 = new int[]{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        int[] var5 = new int[]{1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1};
        char[] var7 = new char[6];
        int[] var6;
        if (var1.equals("ENCRYPT")) {
            var6 = var4;
        } else {
            var6 = var5;
        }

        this.B(var2);
        System.arraycopy(this.A, 0, var3, 0, 8);

        int var9;
        for(var9 = 0; var9 < 16; ++var9) {
            this.A(var1, var3, var6[var9], var7);
            this.A(var2, var7);
        }

        for(var9 = 0; var9 < 4; ++var9) {
            char var8 = var2[var9 + 4];
            var2[var9 + 4] = var2[var9];
            var2[var9] = var8;
        }

        this.D(var2);
    }

    private void A(byte[] var1) {
        char[] var2 = new char[var1.length];

        for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1[var3] >= 0) {
                var2[var3] = (char)var1[var3];
            } else {
                var2[var3] = (char)(256 + var1[var3]);
            }
        }

        this.A(var2);
    }

    private void A(char[] var1) {
        if (var1.length != 8) {
            throw new IllegalArgumentException("Invalid DES key length.");
        } else {
            for(int var2 = 0; var2 < 8; ++var2) {
                if (var1[var2] > 255) {
                    throw new IllegalArgumentException("Invalid DES key format.");
                }
            }

            char[] var5 = new char[8];

            int var3;
            for(var3 = 0; var3 < 8; ++var3) {
                var5[var3] = 0;
            }

            for(var3 = 0; var3 < 8; ++var3) {
                for(int var4 = 0; var4 < 8; ++var4) {
                    var5[7 - var4] = (char)(var5[7 - var4] | (var1[var3] & 1 << var4) >> var4 << var3 & 255);
                }
            }

            for(var3 = 0; var3 < 4; ++var3) {
                this.A[var3] = var5[var3];
                this.A[var3 + 4] = var5[6 - var3];
            }

            char[] var10000 = this.A;
            var10000[3] = (char)(var10000[3] & 240);
            this.A[7] = (char)((this.A[7] & 15) << 4 & 255);
        }
    }

    private void B(char[] var1) {
        char[] var2 = new char[8];

        int var3;
        for(var3 = 0; var3 < 8; ++var3) {
            var2[var3] = 0;
        }

        for(var3 = 0; var3 < 8; ++var3) {
            for(int var4 = 0; var4 < 8; ++var4) {
                var2[7 - var4] = (char)(var2[7 - var4] | (var1[var3] >> var4 & 1) << var3 & 255);
            }
        }

        for(var3 = 0; var3 < 4; ++var3) {
            var1[var3] = var2[2 * var3 + 1];
            var1[var3 + 4] = var2[2 * var3];
        }

    }

    private void D(char[] var1) {
        char[] var2 = new char[8];

        int var3;
        for(var3 = 0; var3 < 8; ++var3) {
            var2[var3] = 0;
        }

        for(var3 = 0; var3 < 8; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
                var2[var3] = (char)(var2[var3] | (var1[var4] << 7 - var3 & 128) >> 2 * var4 + 1);
                var2[var3] = (char)(var2[var3] | (var1[var4 + 4] << 7 - var3 & 128) >> 2 * var4);
            }
        }

        for(var3 = 0; var3 < 8; ++var3) {
            var1[var3] = var2[var3];
        }

    }

    private void B(char[] var1, int var2) {
        this.A(var1, 0, var2);
    }

    private void A(char[] var1, int var2, int var3) {
        char var4 = (char)(255 << 8 - var3 & 255);
        char var5 = (char)((var1[var2] & var4) >> 4);
        var1[var2 + 3] |= var5;

        for(int var6 = var2; var6 < var2 + 3; ++var6) {
            var1[var6] = (char)(var1[var6] << var3);
            var1[var6] = (char)(var1[var6] & 255);
            var5 = (char)((var1[var6 + 1] & var4) >> 8 - var3);
            var1[var6] |= var5;
        }

        var1[var2 + 3] = (char)(var1[var2 + 3] << var3);
        var1[var2 + 3] = (char)(var1[var2 + 3] & 255);
    }

    private void C(char[] var1, int var2) {
        this.C(var1, 0, var2);
    }

    private void C(char[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = (char)(var1[var2] & 1);
            char var6 = (char)(var1[var2 + 1] & 1);
            var1[var2] = (char)(var1[var2] >> 1);
            var1[var2 + 1] = (char)(var1[var2 + 1] >> 1);
            var1[var2 + 1] = (char)(var1[var2 + 1] | var5 << 7 & 255);
            var5 = (char)(var1[var2 + 2] & 1);
            var1[var2 + 2] = (char)(var1[var2 + 2] >> 1);
            var1[var2 + 2] = (char)(var1[var2 + 2] | var6 << 7 & 255);
            var1[var2 + 3] = (char)(var1[var2 + 3] >> 1);
            var1[var2 + 3] = (char)(var1[var2 + 3] | var5 << 7 & 255);
            if ((var1[var2 + 3] & 15) != 0) {
                var1[var2] = (char)(var1[var2] | 128);
                var1[var2 + 3] = (char)(var1[var2 + 3] & 240);
            }
        }

    }

    private void A(String var1, char[] var2, int var3, char[] var4) {
        for(int var5 = 0; var5 < 6; ++var5) {
            var4[var5] = 0;
        }

        if (var1.equals("ENCRYPT")) {
            this.B(var2, var3);
            this.A(var2, 4, var3);
        }

        var4[0] = (char)((var2[1] & 4) << 5 | (var2[2] & 128) >> 1 | var2[1] & 32 | (var2[2] & 1) << 4 | (var2[0] & 128) >> 4 | (var2[0] & 8) >> 1 | (var2[0] & 32) >> 4 | (var2[3] & 16) >> 4);
        var4[1] = (char)((var2[1] & 2) << 6 | (var2[0] & 4) << 4 | (var2[2] & 8) << 2 | (var2[1] & 64) >> 2 | (var2[2] & 2) << 2 | (var2[2] & 32) >> 3 | (var2[1] & 16) >> 3 | (var2[0] & 16) >> 4);
        var4[2] = (char)((var2[3] & 64) << 1 | (var2[0] & 1) << 6 | (var2[1] & 1) << 5 | (var2[0] & 2) << 3 | (var2[3] & 32) >> 2 | (var2[2] & 16) >> 2 | (var2[1] & 8) >> 2 | (var2[0] & 64) >> 6);
        var4[3] = (char)((var2[5] & 8) << 4 | (var2[6] & 1) << 6 | var2[4] & 32 | (var2[5] & 128) >> 3 | (var2[6] & 32) >> 2 | (var2[7] & 32) >> 3 | (var2[4] & 64) >> 5 | (var2[5] & 16) >> 4);
        var4[4] = (char)((var2[6] & 2) << 6 | (var2[6] & 128) >> 1 | (var2[4] & 8) << 2 | var2[6] & 16 | (var2[5] & 1) << 3 | (var2[6] & 8) >> 1 | (var2[5] & 32) >> 4 | (var2[7] & 16) >> 4);
        var4[5] = (char)((var2[4] & 4) << 5 | (var2[7] & 128) >> 1 | (var2[6] & 64) >> 1 | (var2[5] & 4) << 2 | (var2[6] & 4) << 1 | (var2[4] & 1) << 2 | (var2[4] & 128) >> 6 | (var2[4] & 16) >> 4);
        if (var1.equals("DECRYPT")) {
            this.C(var2, var3);
            this.C(var2, 4, var3);
        }

    }

    private void C(char[] var1, char[] var2) {
        var2[0] = (char)((var1[7] & 1) << 7 | (var1[4] & 248) >> 1 | (var1[4] & 24) >> 3);
        var2[1] = (char)((var1[4] & 7) << 5 | (var1[4] & 1) << 3 | (var1[5] & 128) >> 3 | (var1[5] & 224) >> 5);
        var2[2] = (char)((var1[5] & 24) << 3 | (var1[5] & 31) << 1 | (var1[6] & 128) >> 7);
        var2[3] = (char)((var1[5] & 1) << 7 | (var1[6] & 248) >> 1 | (var1[6] & 24) >> 3);
        var2[4] = (char)((var1[6] & 7) << 5 | (var1[6] & 1) << 3 | (var1[7] & 128) >> 3 | (var1[7] & 224) >> 5);
        var2[5] = (char)((var1[7] & 24) << 3 | (var1[7] & 31) << 1 | (var1[4] & 128) >> 7);
    }

    private void C(char[] var1) {
        char[] var2 = new char[]{(char)((var1[1] & 1) << 7 | (var1[0] & 2) << 5 | (var1[2] & 16) << 1 | (var1[2] & 8) << 1 | var1[3] & 8 | (var1[1] & 16) >> 2 | (var1[3] & 16) >> 3 | (var1[2] & 128) >> 7), (char)(var1[0] & 128 | (var1[1] & 2) << 5 | (var1[2] & 2) << 4 | (var1[3] & 64) >> 2 | var1[0] & 8 | (var1[2] & 64) >> 4 | var1[3] & 2 | (var1[1] & 64) >> 6), (char)((var1[0] & 64) << 1 | (var1[0] & 1) << 6 | (var1[2] & 1) << 5 | (var1[1] & 4) << 2 | (var1[3] & 1) << 3 | (var1[3] & 32) >> 3 | (var1[0] & 32) >> 4 | (var1[1] & 128) >> 7), (char)((var1[2] & 32) << 2 | (var1[1] & 8) << 3 | (var1[3] & 4) << 3 | (var1[0] & 4) << 2 | (var1[2] & 4) << 1 | (var1[1] & 32) >> 3 | (var1[0] & 16) >> 3 | (var1[3] & 128) >> 7)};

        for(int var3 = 0; var3 < 4; ++var3) {
            var1[var3] = var2[var3];
        }

    }

    private char A(char[] var1, int var2) {
        return this.B(var1, 0, var2);
    }

    private char B(char[] var1, int var2, int var3) {
        int var6 = 0;
        int var4;
        int var5;
        if (var3 == 1 || var3 == 5) {
            var4 = (var1[var2] & 128) >> 6 | (var1[var2] & 4) >> 2;
            var5 = (var1[var2] & 120) >> 3;
            var6 = var4 * 16 + var5;
        }

        if (var3 == 2 || var3 == 6) {
            var4 = var1[var2] & 2 | (var1[var2 + 1] & 16) >> 4;
            var5 = (var1[var2] & 1) << 3 | (var1[var2 + 1] & 224) >> 5;
            var6 = var4 * 16 + var5;
        }

        if (var3 == 3 || var3 == 7) {
            var4 = (var1[var2 + 1] & 8) >> 2 | (var1[var2 + 2] & 64) >> 6;
            var5 = (var1[var2 + 1] & 7) << 1 | (var1[var2 + 2] & 128) >> 7;
            var6 = var4 * 16 + var5;
        }

        if (var3 == 4 || var3 == 8) {
            var4 = (var1[var2 + 2] & 32) >> 4 | var1[var2 + 2] & 1;
            var5 = (var1[var2 + 2] & 30) >> 1;
            var6 = var4 * 16 + var5;
        }

        return B[var3 - 1][var6];
    }

    private void B(char[] var1, char[] var2) {
        var2[0] = (char)(this.A(var1, 1) << 4 | this.A(var1, 2));
        var2[1] = (char)(this.A(var1, 3) << 4 | this.A(var1, 4));
        var2[2] = (char)(this.B(var1, 3, 5) << 4 | this.B(var1, 3, 6));
        var2[3] = (char)(this.B(var1, 3, 7) << 4 | this.B(var1, 3, 8));
    }

    private void A(char[] var1, char[] var2, char[] var3) {
        char[] var4 = new char[6];
        this.C(var1, var4);

        for(int var5 = 0; var5 < 6; ++var5) {
            var4[var5] ^= var2[var5];
        }

        this.B(var4, var3);
        this.C(var3);
    }

    private void A(char[] var1, char[] var2) {
        char[] var3 = new char[4];
        this.A(var1, var2, var3);

        for(int var4 = 0; var4 < 4; ++var4) {
            var3[var4] ^= var1[var4];
            var1[var4] = var1[var4 + 4];
            var1[var4 + 4] = var3[var4];
        }

    }
}
