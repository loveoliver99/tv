package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.TransientRecordSet;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class TransientRecordSetAssembler implements UniversalObjectAssembler {
    public TransientRecordSetAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return ((TransientRecordSet)var2).marshal();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        TransientRecordSet var3 = new TransientRecordSet();
        var3.unmarshal(var2);
        return var3;
    }
}