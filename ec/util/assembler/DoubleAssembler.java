package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import java.math.BigDecimal;

public class DoubleAssembler implements UniversalObjectAssembler {
    public DoubleAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return (new BigDecimal((Double)var2)).toPlainString().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return Double.valueOf(new String(var2));
    }
}
