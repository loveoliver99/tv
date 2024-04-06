package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.condutil.ConditionTree;
import com.evangelsoft.econnect.util.StringUtilities;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class ConditionTreeAssembler implements UniversalObjectAssembler {
    public ConditionTreeAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return StringUtilities.unicodeToUtf8(((ConditionTree)var2).encode()).getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        ConditionTree var3 = new ConditionTree();
        var3.decode(StringUtilities.utf8ToUnicode(new String(var2)));
        return var3;
    }
}
