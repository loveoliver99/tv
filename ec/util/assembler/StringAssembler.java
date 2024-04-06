package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.StringUtilities;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class StringAssembler implements UniversalObjectAssembler {
    public StringAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return StringUtilities.unicodeToUtf8((String)var2).getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return StringUtilities.utf8ToUnicode(new String(var2));
    }
}