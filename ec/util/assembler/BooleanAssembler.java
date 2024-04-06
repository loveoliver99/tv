package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class BooleanAssembler implements UniversalObjectAssembler {
    public BooleanAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return new byte[]{(byte)((Boolean)var2 ? 84 : 70)};
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return var2[0] == 84 ? Boolean.TRUE : Boolean.FALSE;
    }
}
