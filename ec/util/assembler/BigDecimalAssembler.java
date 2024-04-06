package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import java.math.BigDecimal;

public class BigDecimalAssembler implements UniversalObjectAssembler {
    public BigDecimalAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((BigDecimal)var2).toPlainString().getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return new BigDecimal(new String(var2));
    }
}
