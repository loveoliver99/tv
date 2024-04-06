package com.evangelsoft.econnect.util;

import java.io.IOException;

public interface ObjectSerializer {
    void open() throws IOException;

    void writeObject(Object var1) throws IOException;

    byte[] export() throws IOException;

    void close() throws IOException;
}
