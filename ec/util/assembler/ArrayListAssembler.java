package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import com.evangelsoft.econnect.util.UniversalObjectDeserializer;
import com.evangelsoft.econnect.util.UniversalObjectSerializer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayListAssembler implements UniversalObjectAssembler {
    public ArrayListAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        List var3 = (List)var2;
        UniversalObjectSerializer var4 = new UniversalObjectSerializer();

        try {
            var4.open();

            try {
                Iterator var7 = var3.iterator();

                while(var7.hasNext()) {
                    Object var6 = var7.next();
                    var4.writeObject(var6);
                }

                byte[] var5 = var4.export();
                return var5;
            } finally {
                var4.close();
                var4 = null;
            }
        } catch (Exception var12) {
            throw new IllegalArgumentException(var12.getMessage());
        }
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        UniversalObjectDeserializer var3 = new UniversalObjectDeserializer();

        try {
            ArrayList var4 = new ArrayList();
            var3.prepare(var2);

            try {
                while(!var3.eof()) {
                    var4.add(var3.readObject());
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
