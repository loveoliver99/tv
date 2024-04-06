package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import com.evangelsoft.econnect.util.UniversalObjectDeserializer;
import com.evangelsoft.econnect.util.UniversalObjectSerializer;
import java.util.HashMap;

public class HashMapAssembler implements UniversalObjectAssembler {
    public HashMapAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        HashMap var3 = (HashMap)var2;
        UniversalObjectSerializer var4 = new UniversalObjectSerializer();

        try {
            var4.open();

            try {
                Object[] var6 = var3.keySet().toArray();
                Object[] var10 = var6;
                int var9 = var6.length;

                for(int var8 = 0; var8 < var9; ++var8) {
                    Object var7 = var10[var8];
                    var4.writeObject(var7);
                    var4.writeObject(var3.get(var7));
                }

                byte[] var5 = var4.export();
                return var5;
            } finally {
                var4.close();
                var4 = null;
            }
        } catch (Exception var15) {
            throw new IllegalArgumentException(var15.getMessage());
        }
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        UniversalObjectDeserializer var3 = new UniversalObjectDeserializer();

        try {
            HashMap var4 = new HashMap();
            var3.prepare(var2);

            try {
                while(!var3.eof()) {
                    var4.put(var3.readObject(), var3.readObject());
                }
            } finally {
                var3.close();
                var3 = null;
            }

            return var4;
        } catch (Exception var9) {
            throw new IllegalArgumentException(var9.getMessage());
        }
    }
}