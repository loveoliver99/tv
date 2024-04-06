//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.evangelsoft.econnect.util;

public class Pool<E> {
    public int MAX_SIZE = 512;
    private Object[] E;
    private long[] H;
    private int I;
    private int F;
    private int A;
    private int C;
    private ObjectFactory<E> D;
    private PooledObjectCleaner G;
    private Thread B;

    public Pool(int var1, int var2, ObjectFactory<E> var3, PooledObjectCleaner var4) {
        if (var1 < 0) {
            this.A = 0;
        } else if (var1 > this.MAX_SIZE) {
            this.A = this.MAX_SIZE;
        } else {
            this.A = var1;
        }

        this.E = new Object[this.A];
        this.H = new long[this.A];
        this.F = 0;
        this.I = 0;
        this.C = var2;
        this.D = var3;
        this.G = var4;
        if (this.C > 0) {
            this.B = new Thread(new Pool.PoolCleaner(), "Cleaner");
            this.B.setDaemon(true);
            this.B.start();
        }

    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public int getCapicity() {
        return this.A;
    }

    public void setCapicity(int var1) {
        if (var1 < 0) {
            var1 = 0;
        }

        Object[] var2 = null;
        synchronized(this) {
            if (var1 == this.A) {
                return;
            }

            int var4 = this.I - var1;
            if (var4 > 0 && this.G != null) {
                var2 = new Object[var4];
            }

            int var5 = 0;

            while(true) {
                if (var5 >= var4) {
                    Object[] var12 = new Object[var1];
                    long[] var6 = new long[var1];
                    int var7;
                    if (this.F + this.I > this.A) {
                        var7 = this.A - this.F;
                    } else {
                        var7 = this.I;
                    }

                    if (var7 > 0) {
                        System.arraycopy(this.E, this.F, var12, 0, var7);
                        System.arraycopy(this.H, this.F, var6, 0, var7);
                    }

                    int var8 = this.I - var7;
                    if (var8 > 0) {
                        System.arraycopy(this.E, 0, var12, var7, var8);
                        System.arraycopy(this.H, 0, var6, var7, var8);
                    }

                    this.E = var12;
                    this.H = var6;
                    this.F = 0;
                    this.A = var1;
                    break;
                }

                if (var2 != null) {
                    var2[var5] = this.E[this.F];
                }

                this.E[this.F] = null;
                ++this.F;
                if (this.F >= this.A) {
                    this.F -= this.A;
                }

                --this.I;
                ++var5;
            }
        }

        if (this.G != null && var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
                Object var11 = var2[var3];
                var2[var3] = null;

                try {
                    this.G.clean(var11);
                } catch (Throwable var9) {
                }

                var11 = null;
            }

            var2 = null;
        }

    }

    public int getLifePerdiod() {
        return this.C;
    }

    public void setLifePeriod(int var1) {
        if (var1 <= 0) {
            this.C = 2147483647;
        } else {
            this.C = var1;
        }

    }

    public int getSize() {
        return this.I;
    }

    public void clear() {
        Object[] var1 = null;
        synchronized(this) {
            if (this.G != null) {
                var1 = new Object[this.I];
            }

            int var3 = this.I;
            int var4 = 0;

            while(true) {
                if (var4 >= var3) {
                    break;
                }

                if (var1 != null) {
                    var1[var4] = this.E[this.F];
                }

                this.E[this.F] = null;
                ++this.F;
                if (this.F >= this.A) {
                    this.F -= this.A;
                }

                --this.I;
                ++var4;
            }
        }

        if (this.G != null && var1 != null) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
                Object var7 = var1[var2];
                var1[var2] = null;

                try {
                    this.G.clean(var7);
                } catch (Throwable var5) {
                }

                var7 = null;
            }

            var1 = null;
        }

    }

    public void close() {
        this.clear();
        if (this.B != null) {
            this.B.interrupt();
            this.B = null;
        }

    }

    public void push(E var1) {
        if (var1 != null) {
            boolean var2 = false;
            synchronized(this) {
                if (this.I < this.A) {
                    int var4 = this.F + this.I;
                    if (var4 >= this.A) {
                        var4 -= this.A;
                    }

                    this.E[var4] = var1;
                    this.H[var4] = System.currentTimeMillis() + (long)this.C;
                    ++this.I;
                    var2 = true;
                }
            }

            if (!var2 && this.G != null) {
                try {
                    this.G.clean(var1);
                } catch (Throwable var5) {
                }
            }

        }
    }

    public Object pop() throws Exception {
        Object var1 = null;
        synchronized(this) {
            if (this.I > 0) {
                var1 = this.E[this.F];
                this.E[this.F] = null;
                ++this.F;
                if (this.F >= this.A) {
                    this.F -= this.A;
                }

                --this.I;
            }
        }

        if (var1 == null) {
            var1 = this.D.getObjectInstance();
        }

        return var1;
    }

    public void discard(E var1) {
        if (var1 != null) {
            synchronized(this) {
                int var3 = -1;

                int var4;
                int var5;
                for(var4 = 0; var4 < this.I; ++var4) {
                    var5 = this.F + var4;
                    if (var5 >= this.A) {
                        var5 -= this.A;
                    }

                    if (this.E[var5] != null && this.E[var5].equals(var1)) {
                        var3 = var5;
                        break;
                    }
                }

                if (var3 >= 0) {
                    this.E[var3] = null;
                    --this.I;
                    var4 = this.I - ((var3 >= this.F ? var3 : var3 + this.A) - this.F);
                    if (var3 + 1 + var4 > this.A) {
                        var5 = this.A - (var3 + 1);
                    } else {
                        var5 = var4;
                    }

                    if (var5 > 0) {
                        System.arraycopy(this.E, var3 + 1, this.E, var3, var5);
                        System.arraycopy(this.H, var3 + 1, this.H, var3, var5);
                        var4 -= var5;
                    }

                    if (var4 > 0) {
                        this.E[this.A - 1] = this.E[0];
                        this.H[this.A - 1] = this.H[0];
                        --var4;
                    }

                    if (var4 > 0) {
                        System.arraycopy(this.E, 1, this.E, 0, var4);
                        System.arraycopy(this.H, 1, this.H, 0, var4);
                    }

                    this.E[(this.F + this.I) % this.A] = null;
                }
            }

            if (this.G != null) {
                try {
                    this.G.clean(var1);
                } catch (Throwable var6) {
                }
            }

        }
    }

    private class PoolCleaner implements Runnable {
        private PoolCleaner() {
        }

        public void run() {
            Object[] var1 = null;

            while(true) {
                int var2;
                do {
                    do {
                        try {
                            Thread.sleep((long)Pool.this.C);
                        } catch (InterruptedException var8) {
                            return;
                        }

                        var2 = 0;
                        long var3 = System.currentTimeMillis();
                        synchronized(Pool.this) {
                            int var6 = Pool.this.F;

                            int var7;
                            for(var7 = 0; var7 < Pool.this.I && Pool.this.H[var6] <= var3; ++var7) {
                                ++var2;
                                ++var6;
                                if (var6 >= Pool.this.A) {
                                    var6 -= Pool.this.A;
                                }
                            }

                            if (Pool.this.G != null && (var1 == null || var1.length < var2)) {
                                var1 = new Object[var2];
                            }

                            for(var7 = 0; var7 < var2; ++var7) {
                                if (var1 != null) {
                                    var1[var7] = Pool.this.E[Pool.this.F];
                                }

                                Pool.this.E[Pool.this.F] = null;
                                Pool var10000 = Pool.this;
                                var10000.F = var10000.F + 1;
                                if (Pool.this.F >= Pool.this.A) {
                                    var10000 = Pool.this;
                                    var10000.F = var10000.F - Pool.this.A;
                                }

                                var10000 = Pool.this;
                                var10000.I = var10000.I - 1;
                            }
                        }
                    } while(Pool.this.G == null);
                } while(var1 == null);

                for(int var5 = 0; var5 < var2; ++var5) {
                    Object var11 = var1[var5];
                    var1[var5] = null;

                    try {
                        Pool.this.G.clean(var11);
                    } catch (Throwable var9) {
                    }

                    var11 = null;
                }
            }
        }
    }
}
