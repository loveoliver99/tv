package com.evangelsoft.econnect.util;

import java.io.IOException;

public interface ObjectDeserializer {
    void prepare(byte[] var1) throws IOException;

    Object readObject() throws IOException, ClassNotFoundException;

    void close() throws IOException;
}