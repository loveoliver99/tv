package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.FormatException;
import com.evangelsoft.econnect.dataformat.MetaRecordSet;
import com.evangelsoft.econnect.dataformat.Record;
import com.evangelsoft.econnect.dataformat.RecordFormat;
import com.evangelsoft.econnect.util.ExByteArrayOutputStream;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import java.io.ByteArrayInputStream;

public class RecordAssembler implements UniversalObjectAssembler {
    public RecordAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        try {
            Record var3 = (Record)var2;
            RecordFormat var4 = var3.getFormat();
            ExByteArrayOutputStream var5 = new ExByteArrayOutputStream();
            byte[] var6;
            if (var3.getFormat() != null) {
                var6 = ((var4.getName() == null ? "" : var4.getName()) + ":" + (var4.getLabel() == null ? "" : var4.getLabel())).getBytes();
                var5.write(("<" + var6.length + ">").getBytes());
                var5.write(var6);
                ExByteArrayOutputStream var7 = new ExByteArrayOutputStream();
                MetaRecordSet var8 = new MetaRecordSet(var4);
                var8.save(var7, true);
                var7.flush();
                var5.write(("<" + var7.size() + ">").getBytes());
                var5.write(var7.getBuffer(), 0, var7.size());
                var7.close();
                var7 = null;
                ExByteArrayOutputStream var9 = new ExByteArrayOutputStream();
                var3.save(var9, true);
                var9.flush();
                var5.write(("<" + var9.size() + ">").getBytes());
                var5.write(var9.getBuffer(), 0, var9.size());
                var9.close();
                var9 = null;
            }

            var5.flush();
            var6 = var5.getData();
            var5.close();
            var5 = null;
            return var6;
        } catch (Exception var10) {
            throw new RuntimeException(var10.getMessage());
        }
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        try {
            if (var2.length == 0) {
                return null;
            } else {
                Record var3 = null;
                String var7 = null;
                String var8 = null;
                int var13 = 0;

                for(int var9 = 0; var9 < 3; ++var9) {
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
                    } else if (var9 == 1) {
                        MetaRecordSet var14 = new MetaRecordSet();
                        ByteArrayInputStream var11 = new ByteArrayInputStream(var2, var5, var6);
                        var14.load(var11, true);
                        var11.close();
                        var3 = new Record(var14.toRecordFormat(var7, var8));
                    } else if (var3 != null) {
                        ByteArrayInputStream var15 = new ByteArrayInputStream(var2, var5, var6);
                        var3.load(var15, true);
                        var15.close();
                    }

                    var13 = var5 + var6;
                }

                return var3;
            }
        } catch (Exception var12) {
            String var4 = var12.getMessage();
            if (var4 == null || var4.length() == 0) {
                var4 = "Invalid RecordSet data format.";
            }

            throw new FormatException(var4);
        }
    }
}
