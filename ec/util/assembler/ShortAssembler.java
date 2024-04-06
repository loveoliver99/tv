package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class ShortAssembler implements UniversalObjectAssembler {
    public ShortAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((Short)var2).toString().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return new Short(new String(var2));
    }
}