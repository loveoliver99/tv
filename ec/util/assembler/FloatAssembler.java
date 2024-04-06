package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import java.math.BigDecimal;

public class FloatAssembler implements UniversalObjectAssembler {
    public FloatAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return (new BigDecimal((double)(Float)var2)).toPlainString().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return Float.valueOf(new String(var2));
    }
}