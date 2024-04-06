package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.VariantHolder;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import com.evangelsoft.econnect.util.UniversalObjectDeserializer;
import com.evangelsoft.econnect.util.UniversalObjectSerializer;

public class VariantHolderAssembler implements UniversalObjectAssembler {
    public VariantHolderAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        VariantHolder var3 = (VariantHolder)var2;
        UniversalObjectSerializer var4 = new UniversalObjectSerializer();

        try {
            var4.open();

            byte[] var5;
            try {
                var4.writeObject(var3.value);
                var5 = var4.export();
            } finally {
                var4.close();
                var4 = null;
            }

            return var5;
        } catch (Exception var10) {
            throw new IllegalArgumentException(var10.getMessage());
        }
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        UniversalObjectDeserializer var3 = new UniversalObjectDeserializer();

        try {
            VariantHolder var4 = new VariantHolder();
            var3.prepare(var2);

            try {
                var4.value = var3.readObject();
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
