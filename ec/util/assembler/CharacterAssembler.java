package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.util.StringUtilities;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;

public class CharacterAssembler implements UniversalObjectAssembler {
    public CharacterAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        return StringUtilities.unicodeToUtf8(((Character)var2).toString()).getBytes();
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        return StringUtilities.utf8ToUnicode(new String(var2)).charAt(0);
    }
}