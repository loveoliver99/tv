package com.evangelsoft.econnect.util;

import java.io.ByteArrayOutputStream;

public class ExByteArrayOutputStream extends ByteArrayOutputStream {
    public ExByteArrayOutputStream() {
    }

    public byte[] getBuffer() {
        return this.buf;
    }

    public byte[] getData() {
        if (this.buf.length > this.count) {
            byte[] var1 = new byte[this.count];
            System.arraycopy(this.buf, 0, var1, 0, this.count);
            return var1;
        } else {
            return this.buf;
        }
    }
}