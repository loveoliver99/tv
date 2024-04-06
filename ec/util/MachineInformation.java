package com.evangelsoft.econnect.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public abstract class MachineInformation {
    public static MachineInformationGetter getter = null;
    public static MachineInformationGetter defaultGetter = new MachineInformationGetter() {
        public String getId() throws Exception {
            InetAddress var1 = InetAddress.getLocalHost();
            String var2 = var1.getHostName();
            String var3 = System.getProperty("machineId");
            if (var3 == null || var3.length() == 0) {
                try {
                    FileInputStream var4 = new FileInputStream(LaunchDirectory.getFileName("machine.id"));
                    if (var4 != null) {
                        Properties var5 = new Properties();
                        var5.load(var4);
                        var3 = var5.getProperty("id");
                        var4.close();
                    }
                } catch (Exception var8) {
                }
            }

            if (var3 == null || var3.length() == 0) {
                var3 = "$host$";
            }

            StringBuffer var9 = new StringBuffer();
            SymbolParser var10 = new SymbolParser(var3);

            int var6;
            for(var6 = 0; var10.find(); var10.moveTo(var6)) {
                String var7 = var10.getNet();
                var9.append(var3.substring(var6, var10.getBeginIndex()));
                var6 = var10.getEndIndex();
                if (var7.equals("host")) {
                    var9.append(var2);
                }
            }

            if (var6 < var3.length()) {
                var9.append(var3.substring(var6));
            }

            return var9.toString().toUpperCase();
        }

        public String getGene() throws Exception {
            String var1 = System.getProperty("os.name");
            String var2 = System.getProperty("os.version");
            String var3 = System.getProperty("os.arch");
            InetAddress var4 = InetAddress.getLocalHost();
            String var5 = var4.getHostName();
            String var6 = System.getenv("PROCESSOR_IDENTIFIER");
            StringBuffer var7 = new StringBuffer();
            var7.append(var1 + ';');
            var7.append(var2 + ';');
            var7.append(var3 + ';');
            var7.append(var5 + ';');
            var7.append(var6 + ';');
            return Encrypter.encrypt(var7.toString());
        }
    };

    static {
        try {
            InputStream var0 = ResourceLocater.loadStream("machine.getter");
            if (var0 != null) {
                Properties var1 = new Properties();

                try {
                    var1.load(var0);
                } catch (Throwable var6) {
                    System.out.println(var6.getMessage());
                }

                try {
                    var0.close();
                } catch (Throwable var5) {
                }

                String var2 = var1.getProperty("class");
                if (var2 != null && var2.length() > 0) {
                    try {
                        Object var3 = Class.forName(var2).newInstance();
                        if (var3 instanceof MachineInformationGetter) {
                            getter = (MachineInformationGetter)var3;
                        }
                    } catch (Exception var4) {
                        System.out.println(var4.getMessage());
                    }
                }
            }
        } catch (Exception var7) {
        }

    }

    public MachineInformation() {
    }

    public static MachineInformationGetter getInstance() {
        return getter != null ? getter : defaultGetter;
    }
}
