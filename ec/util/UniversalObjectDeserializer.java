package com.evangelsoft.econnect.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class UniversalObjectDeserializer implements ObjectDeserializer {
    private static HashMap<String, Class<?>> D = new HashMap();
    private static HashMap<String, UniversalObjectAssembler> C = new HashMap();
    private byte[] E = null;
    private int F = -1;

    static {
        Properties var0 = new Properties();
        String[] var4;
        int var3 = (var4 = new String[]{"UniversalType.map", "UniversalTypeEx.map"}).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            String var1 = var4[var2];

            try {
                InputStream var5 = ObjectParserFactory.class.getResourceAsStream(var1);
                if (var5 != null) {
                    var0.clear();
                    var0.load(var5);
                }
            } catch (Exception var15) {
            }

            Iterator var6 = var0.keySet().iterator();

            while(var6.hasNext()) {
                Object var16 = var6.next();
                String var7 = (String)var16;
                String var8 = var0.getProperty(var7);
                int var9 = var8.indexOf(59);
                String var10;
                String var11;
                if (var9 >= 0) {
                    var10 = var8.substring(0, var9).trim();
                    var11 = var8.substring(var9 + 1).trim();
                } else {
                    var10 = var8;
                    var11 = null;
                }

                if (var10.length() > 0) {
                    try {
                        Class var12;
                        if (var7.equals("void")) {
                            var12 = Void.TYPE;
                        } else if (var7.equals("char")) {
                            var12 = Character.TYPE;
                        } else if (var7.equals("byte")) {
                            var12 = Byte.TYPE;
                        } else if (var7.equals("boolean")) {
                            var12 = Boolean.TYPE;
                        } else if (var7.equals("short")) {
                            var12 = Short.TYPE;
                        } else if (var7.equals("int")) {
                            var12 = Integer.TYPE;
                        } else if (var7.equals("long")) {
                            var12 = Long.TYPE;
                        } else if (var7.equals("single")) {
                            var12 = Float.TYPE;
                        } else if (var7.equals("double")) {
                            var12 = Double.TYPE;
                        } else {
                            var12 = Class.forName(var10);
                        }

                        D.put(var7, var12);
                    } catch (ClassNotFoundException var14) {
                        System.out.println(var14.getMessage());
                    }

                    if (var11 != null && var11.length() > 0) {
                        try {
                            C.put(var7, (UniversalObjectAssembler)Class.forName(var11).newInstance());
                        } catch (Exception var13) {
                            System.out.println(var13.getMessage());
                        }
                    }
                }
            }
        }

    }

    public UniversalObjectDeserializer() {
    }

    public static Class<?> getClass(String var0) throws ClassNotFoundException {
        Class var1 = (Class)D.get(var0);
        if (var1 == null) {
            var1 = Class.forName(var0);
            synchronized(D) {
                D.put(var0, var1);
            }
        }

        return var1;
    }

    public void prepare(byte[] var1) throws IOException {
        this.E = var1;
        this.F = 0;
    }

    public boolean eof() {
        return this.E == null || this.F >= this.E.length;
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        if (this.eof()) {
            throw new IOException("End of data stream reached.");
        } else {
            String var1 = null;
            int var2 = -1;
            if (this.E[this.F] == 60) {
                ++this.F;

                int var3;
                for(var3 = this.F; this.F < this.E.length && this.E[this.F] != 62; ++this.F) {
                }

                if (this.F < this.E.length) {
                    String var4 = new String(this.E, var3, this.F - var3);
                    int var5 = var4.indexOf(58);
                    String var6;
                    if (var5 >= 0) {
                        var1 = var4.substring(0, var5);
                        var6 = var4.substring(var5 + 1);
                    } else {
                        var1 = var4;
                        var6 = "";
                    }

                    if (var6.length() > 0) {
                        try {
                            var2 = Integer.parseInt(var6);
                        } catch (NumberFormatException var9) {
                            var2 = -1;
                        }
                    } else {
                        var2 = 0;
                    }

                    ++this.F;
                }
            }

            if (var1 == null || var2 < 0 || var1.length() == 0 && var2 > 0) {
                throw new IOException("Invalid universal object format.");
            } else {
                Object var10;
                if (var1.length() == 0) {
                    var10 = null;
                } else if (var1.endsWith("[]") && var1.length() > 2) {
                    var1 = var1.substring(0, var1.length() - 2);
                    var10 = Array.newInstance(getClass(var1), var2);

                    for(int var12 = 0; var12 < var2; ++var12) {
                        Array.set(var10, var12, this.readObject());
                    }
                } else {
                    byte[] var11 = new byte[var2];
                    System.arraycopy(this.E, this.F, var11, 0, var2);
                    this.F += var2;
                    Class var13 = getClass(var1);
                    UniversalObjectAssembler var14 = (UniversalObjectAssembler)C.get(var1);

                    try {
                        if (var14 != null) {
                            var10 = var14.unmarshal(var13, var11);
                        } else {
                            var10 = var13.newInstance();
                            ((UniversalTransferable)var10).deserialize(var11);
                        }
                    } catch (Exception var8) {
                        throw new IOException(var8.getMessage());
                    }
                }

                return var10;
            }
        }
    }

    public void close() throws IOException {
        this.E = null;
    }
}
