package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.FormatException;
import com.evangelsoft.econnect.dataformat.MetaRecordSet;
import com.evangelsoft.econnect.dataformat.RecordFormat;
import com.evangelsoft.econnect.util.ExByteArrayOutputStream;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import java.io.ByteArrayInputStream;

public class RecordFormatAssembler implements UniversalObjectAssembler {
    public RecordFormatAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        try {
            RecordFormat var3 = (RecordFormat)var2;
            ExByteArrayOutputStream var4 = new ExByteArrayOutputStream();
            byte[] var5 = ((var3.getName() == null ? "" : var3.getName()) + ":" + (var3.getLabel() == null ? "" : var3.getLabel())).getBytes();
            var4.write(("<" + var5.length + ">").getBytes());
            var4.write(var5);
            ExByteArrayOutputStream var6 = new ExByteArrayOutputStream();
            MetaRecordSet var7 = new MetaRecordSet(var3);
            var7.save(var6, true);
            var6.flush();
            var4.write(("<" + var6.size() + ">").getBytes());
            var4.write(var6.getBuffer(), 0, var6.size());
            var6.close();
            var6 = null;
            var4.flush();
            byte[] var8 = var4.getData();
            var4.close();
            var4 = null;
            return var8;
        } catch (Exception var9) {
            throw new RuntimeException(var9.getMessage());
        }
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        try {
            if (var2.length == 0) {
                return null;
            } else {
                RecordFormat var3 = null;
                String var7 = null;
                String var8 = null;
                int var13 = 0;

                for(int var9 = 0; var9 < 2; ++var9) {
                    if (var2[var13] != 60) {
                        throw new Exception();
                    }

                    int var5;
                    for(var5 = var13 + 1; var5 < var2.length && var2[var5] != 62; ++var5) {
                    }

                    if (var5 >= var2.length) {
                        throw new Exception();
                    }

                    int var6 = Integer.parseInt(new String(var2, var13 + 1, var5 - var13 - 1));
                    ++var5;
                    if (var9 == 0) {
                        String[] var10 = (new String(var2, var5, var6)).split(":");
                        var7 = var10[0];
                        if (var10.length > 1) {
                            var8 = var10[1];
                        }
                    } else {
                        MetaRecordSet var14 = new MetaRecordSet();
                        ByteArrayInputStream var11 = new ByteArrayInputStream(var2, var5, var6);
                        var14.load(var11, true);
                        var11.close();
                        var3 = var14.toRecordFormat(var7, var8);
                    }

                    var13 = var5 + var6;
                }

                return var3;
            }
        } catch (Exception var12) {
            String var4 = var12.getMessage();
            if (var4 == null || var4.length() == 0) {
                var4 = "Invalid RecordFormat data format.";
            }

            throw new FormatException(var4);
        }
    }
}
