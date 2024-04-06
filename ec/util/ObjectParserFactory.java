package com.evangelsoft.econnect.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

public class ObjectParserFactory {
    private static String C = "JAVA";
    private static HashMap<String, Class<?>> B = new HashMap();
    private static HashMap<String, Class<?>> A = new HashMap();
    private static ResourceBundle D = ResourceBundle.getBundle(ObjectParserFactory.class.getPackage().getName() + ".Res");

    static {
        try {
            Properties var0 = new Properties();
            var0.load(ObjectParserFactory.class.getResourceAsStream("ObjectParser.def"));
            Iterator var2 = var0.keySet().iterator();

            while(var2.hasNext()) {
                Object var1 = var2.next();
                String var3 = (String)var1;
                int var4 = var3.indexOf(46);
                if (var4 >= 0) {
                    String var5 = var3.substring(0, var4);
                    String var6 = var0.getProperty(var3);
                    if (var3.equals(var5 + ".serializer")) {
                        B.put(var5, Class.forName(var6));
                    } else if (var3.equals(var5 + ".deserializer")) {
                        A.put(var5, Class.forName(var6));
                    }
                }
            }
        } catch (Exception var7) {
            System.out.println(var7.getMessage());
        }

    }

    public ObjectParserFactory() {
    }

    public static ObjectSerializer getSerializer(String var0) throws Exception {
        if (var0 == null) {
            var0 = C;
        }

        Class var1 = (Class)B.get(var0);
        if (var1 == null) {
            throw new Exception(MessageFormat.format(D.getString("MSG_NO_OBJECT_SERIALIZER_FOUND_FOR_TYPE"), var0));
        } else {
            return (ObjectSerializer)var1.newInstance();
        }
    }

    public static ObjectDeserializer getDeserializer(String var0) throws Exception {
        if (var0 == null) {
            var0 = C;
        }

        Class var1 = (Class)A.get(var0);
        if (var1 == null) {
            throw new Exception(MessageFormat.format(D.getString("MSG_NO_OBJECT_DESERIALIZER_FOUND_FOR_TYPE"), var0));
        } else {
            return (ObjectDeserializer)var1.newInstance();
        }
    }
}
