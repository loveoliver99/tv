package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class ObjectAssembler implements UniversalObjectAssembler {
    public ObjectAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return new byte[0];
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return new Object();
    }
}