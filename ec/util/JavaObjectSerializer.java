package com.evangelsoft.econnect.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class JavaObjectSerializer implements ObjectSerializer {
    private ByteArrayOutputStream D = null;
    private ObjectOutputStream E = null;

    public JavaObjectSerializer() {
    }

    public void open() throws IOException {
        if (this.E != null) {
            this.E.close();
        }

        if (this.D != null) {
            this.D.close();
        }

        this.D = new ByteArrayOutputStream();
        this.E = new ObjectOutputStream(this.D);
    }

    public void writeObject(Object var1) throws IOException {
        this.E.writeObject(var1);
    }

    public byte[] export() throws IOException {
        this.E.flush();
        this.D.flush();
        return this.D.toByteArray();
    }

    public void close() throws IOException {
        if (this.E != null) {
            this.E.close();
            this.E = null;
        }

        if (this.D != null) {
            this.D.close();
            this.D = null;
        }

    }
}
