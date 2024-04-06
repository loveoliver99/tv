package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.DeltaRecordSet;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class DeltaRecordSetAssembler implements UniversalObjectAssembler {
    public DeltaRecordSetAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((DeltaRecordSet)var2).marshal();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        DeltaRecordSet var3 = new DeltaRecordSet();
        var3.unmarshal(var2);
        return var3;
    }
}
