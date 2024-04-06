package com.evangelsoft.econnect.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class JavaObjectDeserializer implements ObjectDeserializer {
    private ByteArrayInputStream A = null;
    private ObjectInputStream B = null;

    public JavaObjectDeserializer() {
    }

    public void prepare(byte[] var1) throws IOException {
        if (this.B != null) {
            this.B.close();
        }

        if (this.A != null) {
            this.A.close();
        }

        this.A = new ByteArrayInputStream(var1);
        this.B = new ObjectInputStream(this.A);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return this.B.readObject();
    }

    public void close() throws IOException {
        if (this.B != null) {
            this.B.close();
            this.B = null;
        }

        if (this.A != null) {
            this.A.close();
            this.A = null;
        }

    }
}
