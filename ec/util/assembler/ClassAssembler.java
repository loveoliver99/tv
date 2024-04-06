
package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.FormatException;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import com.evangelsoft.econnect.util.UniversalObjectDeserializer;

public class ClassAssembler implements UniversalObjectAssembler {
    public ClassAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((Class)var2).getName().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        String var3 = new String(var2);

        try {
            boolean var4 = var3.startsWith("[L") && var3.endsWith(";");
            String var5 = var4 ? var3.substring(2, var3.length() - 1) : var3;
            Class var6 = UniversalObjectDeserializer.getClass(var5);
            if (var4) {
                var6 = Class.forName("[L" + var6.getName() + ";");
            }

            return var6;
        } catch (ClassNotFoundException var8) {
            try {
                return Class.forName(var3);
            } catch (ClassNotFoundException var7) {
                throw new FormatException(var7.getMessage());
            }
        }
    }
}