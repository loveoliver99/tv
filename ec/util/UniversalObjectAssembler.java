package com.evangelsoft.econnect.util;

public interface UniversalObjectAssembler {
    byte[] marshal(Class<?> var1, Object var2);

    Object unmarshal(Class<?> var1, byte[] var2);
}