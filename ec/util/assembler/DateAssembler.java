package com.evangelsoft.econnect.util.assembler;

import com.evangelsoft.econnect.dataformat.FormatException;
import com.evangelsoft.econnect.util.UniversalObjectAssembler;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateAssembler implements UniversalObjectAssembler {
    private static SimpleDateFormat C = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat A = new SimpleDateFormat("HHmmss");
    private static SimpleDateFormat B = new SimpleDateFormat("yyyyMMddHHmmss");

    public DateAssembler() {
    }

    public byte[] marshal(Class<?> var1, Object var2) {
        if (var1 == Date.class) {
            return C.format((java.util.Date)var2).getBytes();
        } else {
            return var1 == Time.class ? A.format((java.util.Date)var2).getBytes() : B.format((java.util.Date)var2).getBytes();
        }
    }

    public Object unmarshal(Class<?> var1, byte[] var2) {
        String var3 = new String(var2);

        try {
            if (var1 == Date.class) {
                return new Date(C.parse(var3).getTime());
            } else if (var1 == Time.class) {
                return new Time(A.parse(var3).getTime());
            } else {
                return var1 == Timestamp.class ? new Timestamp(B.parse(var3).getTime()) : B.parse(var3);
            }
        } catch (ParseException var5) {
            throw new FormatException(var5.getMessage());
        }
    }
}