package com.evangelsoft.econnect.util;

import com.evangelsoft.econnect.dataformat.FormatException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class UniversalObjectSerializer implements ObjectSerializer {
    private static HashMap<String, String> C = new HashMap();
    private static HashMap<String, UniversalObjectAssembler> A = new HashMap();
    private ByteArrayOutputStream B = null;

    static {
        Properties var0 = new Properties();
        String[] var4;
        int var3 = (var4 = new String[]{"UniversalClass.map", "UniversalClassEx.map"}).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            String var1 = var4[var2];

            try {
                InputStream var5 = ObjectParserFactory.class.getResourceAsStream(var1);
                if (var5 != null) {
                    var0.clear();
                    var0.load(var5);
                }
            } catch (Exception var14) {
            }

            Iterator var6 = var0.keySet().iterator();

            while(var6.hasNext()) {
                Object var15 = var6.next();
                String var7 = (String)var15;
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
                    C.put(var7, var10);
                    if (var11 != null && var11.length() > 0) {
                        try {
                            A.put(var7, (UniversalObjectAssembler)Class.forName(var11).newInstance());
                        } catch (Exception var13) {
                            System.out.println(var13.getMessage());
                        }
                    }
                }
            }
        }

    }

    public UniversalObjectSerializer() {
    }

    public static String getType(String var0) throws FormatException {
        String var1 = (String)C.get(var0);
        if (var1 == null) {
            throw new FormatException("Unsupported class " + var0);
        } else {
            return var1;
        }
    }

    public void open() throws IOException {
        if (this.B != null) {
            this.B.close();
        }

        this.B = new ByteArrayOutputStream();
    }

    public void writeObject(Object var1) throws IOException {
        this.B.write(new byte[]{60});
        if (var1 == null) {
            this.B.write(new byte[]{62});
        } else {
            Class var2 = var1.getClass();
            String var3 = var2.isArray() ? var2.getComponentType().getName() : var2.getName();
            String var4 = (String)C.get(var3);
            if (var4 == null) {
                var4 = var3;
            }

            this.B.write(var4.getBytes());
            if (var2.isArray()) {
                this.B.write(new byte[]{91, 93});
            }

            this.B.write(new byte[]{58});
            if (var2.isArray()) {
                int var5 = Array.getLength(var1);
                this.B.write(Integer.valueOf(var5).toString().getBytes());
                this.B.write(new byte[]{62});

                for(int var6 = 0; var6 < var5; ++var6) {
                    this.writeObject(Array.get(var1, var6));
                }
            } else {
                UniversalObjectAssembler var10 = (UniversalObjectAssembler)A.get(var3);

                byte[] var9;
                try {
                    if (var10 != null) {
                        var9 = var10.marshal(var2, var1);
                    } else {
                        var9 = ((UniversalTransferable)var1).serialize();
                    }
                } catch (Exception var8) {
                    throw new IOException(var8.getMessage());
                }

                this.B.write(Integer.valueOf(var9.length).toString().getBytes());
                this.B.write(new byte[]{62});
                this.B.write(var9);
            }
        }

    }

    public byte[] export() throws IOException {
        this.B.flush();
        return this.B.toByteArray();
    }

    public void close() throws IOException {
        if (this.B != null) {
            this.B.close();
            this.B = null;
        }

    }
}
