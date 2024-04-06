package com.evangelsoft.econnect.util;

public class IllogicException extends Exception {
    private static final long serialVersionUID = -6883199306271758709L;
    private int A;

    public IllogicException() {
        this(0);
    }

    public IllogicException(int var1) {
        this.A = 0;
        this.A = var1;
    }

    public IllogicException(String var1) {
        this(0, (String)var1);
    }

    public IllogicException(int var1, String var2) {
        super(var2);
        this.A = 0;
        this.A = var1;
    }

    public IllogicException(String var1, Throwable var2) {
        this(0, var1, var2);
    }

    public IllogicException(int var1, String var2, Throwable var3) {
        super(var2, var3);
        this.A = 0;
        this.A = var1;
    }

    public IllogicException(Throwable var1) {
        this(0, (Throwable)var1);
    }

    public IllogicException(int var1, Throwable var2) {
        super(var2);
        this.A = 0;
        this.A = var1;
    }

    public int getCode() {
        return this.A;
    }
}
