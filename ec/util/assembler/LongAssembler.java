package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class LongAssembler implements UniversalObjectAssembler {
    public LongAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((Long)var2).toString().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return Long.valueOf(new String(var2));
    }
}
