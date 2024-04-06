package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class ByteAssembler implements UniversalObjectAssembler {
    public ByteAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return new byte[]{(Byte)var2};
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return new Byte(var2[0]);
    }
}