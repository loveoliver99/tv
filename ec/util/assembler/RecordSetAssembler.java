package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.RecordSet;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class RecordSetAssembler implements UniversalObjectAssembler {
    public RecordSetAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((RecordSet)var2).marshal();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        RecordSet var3 = new RecordSet();
        var3.unmarshal(var2);
        return var3;
    }
}