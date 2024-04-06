package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.condutil.SortList;
import com.evangelsoft.econnect.util.StringUtilities;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class SortListAssembler implements UniversalObjectAssembler {
    public SortListAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return StringUtilities.unicodeToUtf8(((SortList)var2).encode()).getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        SortList var3 = new SortList();
        var3.decode(StringUtilities.utf8ToUnicode(new String(var2)));
        return var3;
    }
}