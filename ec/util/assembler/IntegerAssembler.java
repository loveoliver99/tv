package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class IntegerAssembler implements UniversalObjectAssembler {
    public IntegerAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((Integer)var2).toString().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return Integer.valueOf(new String(var2));
    }
}
