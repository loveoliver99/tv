package com.evangelsoft.econnect.util;

import com.evangelsoft.econnect.dataformat.FormatException;

public interface UniversalTransferable {
    byte[] serialize();

    void deserialize(byte[] var1) throws FormatException;
}