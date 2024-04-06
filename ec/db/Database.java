package com.evangelsoft.econnect.db;

import com.evangelsoft.econnect.util.ParameterParser;
import com.evangelsoft.econnect.util.SymbolParser;
import com.evangelsoft.econnect.util.TokenParser;
import java.io.IOException;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.Executor;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public class Database implements Connection , PooledConnection {
    public static final String DBMS_DB2 = "DB2";
    public static final String DBMS_INTERBASE = "INTERBASE";
    public static final String DBMS_INFORMIX = "INFORMIX";
    public static final String DBMS_SYBASE = "SYBASE";
    public static final String DBMS_MSSQL = "MSSQL";
    public static final String DBMS_MYSQL = "MYSQL";
    public static final String DBMS_ORACLE = "ORACLE";
    public static final String DBMS_ACCESS = "ACCESS";
    public static final String DBMS_OTHERS = "OTHERS";
    Connection connection;
    boolean hasErrorOccurred = false;
    private String dbmsType;
    private String schema;
    private Vector<ConnectionEventListener> connectionEventListeners = new Vector();
    Vector<Statement> statements = new Vector();
    private boolean isTransactionLocked = false;
    private DataDictionary DataDictionary = new DataDictionary(this);
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(Database.class.getPackage().getName() + ".Res");
    private static Properties dbmsProperties = new Properties();

    static {
        try {
        	dbmsProperties.load(Database.class.getResourceAsStream("DBMS.map"));
        } catch (IOException var1) {
            System.out.println(var1.getMessage());
        }

    }

    public Database(String url, Properties properties) throws SQLException {
        connection = DriverManager.getConnection(url, properties);
        ConnectionFactory.A(connection, properties);
        this.dbmsType = getDbms(connection);
        if (properties != null) {
            this.schema = properties.getProperty("schema");
        }

    }

    Database(Connection conn, boolean hasErrOccurred, Properties properties) throws SQLException {
        connection = conn;
        hasErrorOccurred = hasErrOccurred;
        this.dbmsType = getDbms(connection);
        if (properties != null) {
            this.schema = properties.getProperty("schema");
        }

    }

    public DataDictionary getDictionary() {
        return this.DataDictionary;
    }

    protected void finalize() throws Throwable {
        if (connection != null) {
            try {
                this.closeResources();
            } catch (Throwable var2) {
            }
        }

        super.finalize();
    }

    private void closeResources() throws SQLException {
        if (connection != null) {
            try {
                synchronized(this.statements) {
                    Iterator var3 = this.statements.iterator();

                    while(var3.hasNext()) {
                        Statement var2 = (Statement)var3.next();

                        try {
                            var2.close();
                        } catch (Throwable var14) {
                        }
                    }
                }

                if (this.connectionEventListeners.size() == 0) {
                    try {
                        connection.close();
                    } catch (Throwable var13) {
                    }
                } else {
                    synchronized(this.connectionEventListeners) {
                        for(int var18 = 0; var18 < this.connectionEventListeners.size(); ++var18) {
                            try {
                                ((ConnectionEventListener)this.connectionEventListeners.elementAt(var18)).connectionClosed(new ConnectionEvent(this));//
                            } catch (Throwable var12) {
                            }
                        }
                    }
                }
            } finally {
                this.statements.clear();
                connection = null;
            }

        }
    }

    private void checkIfClosed() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException(resourceBundle.getString("MSG_CONNECTION_HAS_BEEN_CLOSED"));
        }
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public static String getDbms(Connection var0) throws SQLException {
        String var1 = "DBMS_" + var0.getMetaData().getDatabaseProductName().toUpperCase().replace(' ', '_');
        String var2 = dbmsProperties.getProperty(var1);
        if (var2 != null && var2.length() != 0) {
            return var2;
        } else {
            throw new SQLException("Unknown DBMS {" + var1 + "}.");
        }
    }

    public String getDbms() {
        return this.dbmsType;
    }

    public String getSchema() {
        return this.schema;
    }

    public void close() throws SQLException {
        if (this.isTransactionLocked) {
            throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_OPERATION_PROHIBITED_WHEN_TRANSACTION_LOCKED"), "close"));
        } else {
            this.closeResources();
        }
    }

    public void addConnectionEventListener(ConnectionEventListener var1) {
        this.connectionEventListeners.add(var1);
    }

    public void removeConnectionEventListener(ConnectionEventListener var1) {
        this.connectionEventListeners.remove(var1);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {

    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {

    }

    public boolean isClosed() throws SQLException {
        return connection == null || connection.isClosed();
    }

    public void setAutoCommit(boolean var1) throws SQLException {
        if (this.isTransactionLocked) {
            throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_OPERATION_PROHIBITED_WHEN_TRANSACTION_LOCKED"), "setAutoCommit"));
        } else {
            try {
                this.checkIfClosed();
                connection.setAutoCommit(var1);
            } catch (SQLException var3) {
                hasErrorOccurred = false;
                throw var3;
            }
        }
    }

    public void commit() throws SQLException {
        if (this.isTransactionLocked) {
            throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_OPERATION_PROHIBITED_WHEN_TRANSACTION_LOCKED"), "commit"));
        } else {
            try {
                this.checkIfClosed();
                connection.commit();
            } catch (Throwable var2) {
                hasErrorOccurred = false;
                if (var2 instanceof SQLException) {
                    throw (SQLException)var2;
                } else {
                    throw new SQLException(var2.getMessage());
                }
            }
        }
    }

    public void rollback() throws SQLException {
        if (this.isTransactionLocked) {
            throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_OPERATION_PROHIBITED_WHEN_TRANSACTION_LOCKED"), "rollback"));
        } else {
            try {
                this.checkIfClosed();
                boolean var1 = false;
                int var2 = 0;

                while(var2 < 10) {
                    try {
                        connection.rollback();
                        var1 = true;
                        break;
                    } catch (Throwable var11) {
                        try {
                            Thread.sleep((long)(var2 * 100));
                        } catch (InterruptedException var10) {
                        }

                        ++var2;
                    }
                }

                if (!var1) {
                    try {
                        connection.close();
                    } finally {
                        connection = null;
                    }
                }

            } catch (Throwable var12) {
                hasErrorOccurred = false;
                if (var12 instanceof SQLException) {
                    throw (SQLException)var12;
                } else {
                    throw new SQLException(var12.getMessage());
                }
            }
        }
    }

    public void lockTx() throws SQLException {
        if (!this.isTransactionLocked) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException var2) {
                hasErrorOccurred = false;
                throw var2;
            }

            this.isTransactionLocked = true;
        }
    }

    public void unlockTx(boolean var1) throws SQLException {
        if (this.isTransactionLocked) {
            try {
                if (var1) {
                    connection.commit();
                    connection.setAutoCommit(true);
                } else {
                    hasErrorOccurred = false;
                    boolean var2 = false;
                    int var3 = 0;

                    while(var3 < 10) {
                        try {
                            connection.rollback();
                            var2 = true;
                            break;
                        } catch (Throwable var12) {
                            try {
                                Thread.sleep((long)(var3 * 100));
                            } catch (InterruptedException var11) {
                            }

                            ++var3;
                        }
                    }

                    if (!var2) {
                        try {
                            connection.close();
                        } finally {
                            connection = null;
                        }
                    }
                }

                this.isTransactionLocked = false;
            } catch (Throwable var13) {
                hasErrorOccurred = false;
                if (var13 instanceof SQLException) {
                    throw (SQLException)var13;
                } else {
                    throw new SQLException(var13.getMessage());
                }
            }
        }
    }

    public String parseSql(String var1) throws SQLException {
        return parseSql(this.dbmsType, var1);
    }

    public static String parseSql(String var0, String var1) throws SQLException {
        if (var1.length() == 0) {
            return "";
        } else {
            String var2 = "";
            int var3 = 0;

            int var6;
            for(int var4 = 0; var4 < var1.length(); ++var4) {
                char var5 = var1.charAt(var4);
                if (var5 != ' ' && var5 != '\n' && var5 != '\r' && var5 != '\t') {
                    if (var5 != '{') {
                        break;
                    }

                    var6 = var1.indexOf(125);
                    if (var6 >= var4) {
                        var2 = var1.substring(var4 + 1, var6);
                        var1 = var1.substring(var6 + 1);
                    }
                }
            }

            if (var2.length() > 0) {
                String[] var24 = var2.split(";");
                String[] var8 = var24;
                int var7 = var24.length;

                for(var6 = 0; var6 < var7; ++var6) {
                    String var26 = var8[var6];
                    if (var26.length() != 0) {
                        String[] var9 = var26.split("=");
                        if (var9.length >= 2 && var9[0].trim().equalsIgnoreCase("TOP")) {
                            try {
                                var3 = Integer.parseInt(var9[1]);
                            } catch (NumberFormatException var23) {
                            }
                        }
                    }
                }
            }

            StringBuffer var25 = new StringBuffer();
            short var27 = 1000;
            int var10;
            String var11;
            int var14;
            int var15;
            String var33;
            if (var0.equals("ORACLE") && var1.length() > var27 * 3) {
                var6 = 0;
                TokenParser var28 = new TokenParser(var1);
                SymbolParser var31 = new SymbolParser(var1, '(', ')');
                var33 = null;
                var10 = -1;

                while(true) {
                    while(var28.find()) {
                        var11 = var28.get();
                        if (!var11.equalsIgnoreCase("IN")) {
                            var33 = var11;
                            var10 = var28.getBeginIndex();
                        } else {
                            int var12;
                            for(var12 = var28.getEndIndex(); var12 < var1.length() && (var1.charAt(var12) == ' ' || var1.charAt(var12) == '\t' || var1.charAt(var12) == '\r' || var1.charAt(var12) == '\n'); ++var12) {
                            }

                            if (var12 < var1.length() && var1.charAt(var12) == '(') {
                                var31.moveTo(var12);
                                if (var31.find()) {
                                    String var13 = var31.getNet();
                                    var28.moveTo(var31.getEndIndex());

                                    for(var14 = 0; var14 < var13.length() && (var1.charAt(var14) == ' ' || var1.charAt(var14) == '\t' || var1.charAt(var14) == '\r' || var1.charAt(var14) == '\n'); ++var14) {
                                    }

                                    for(var15 = var13.length() - 1; var15 >= 0 && (var1.charAt(var15) == ' ' || var1.charAt(var15) == '\t' || var1.charAt(var15) == '\r' || var1.charAt(var15) == '\n'); --var15) {
                                    }

                                    ++var15;
                                    var13 = var13.substring(var14, var15);
                                    if (var13.length() < 6 || !var13.substring(0, 6).toUpperCase().equals("SELECT")) {
                                        ArrayList var16 = new ArrayList();
                                        ParameterParser var17 = new ParameterParser(var13);

                                        while(var17.find()) {
                                            var16.add(parseSql(var0, var17.get()).trim());
                                        }

                                        if (var16.size() > var27) {
                                            var25.append(var1.substring(var6, var10) + '(');
                                            int var18 = var16.size() / var27 + (var16.size() % var27 == 0 ? 0 : 1);

                                            for(int var19 = 0; var19 < var18; ++var19) {
                                                if (var19 > 0) {
                                                    var25.append(" OR ");
                                                }

                                                var25.append(var33 + " IN (");

                                                for(int var20 = 0; var20 < var27; ++var20) {
                                                    int var21 = var27 * var19 + var20;
                                                    if (var21 >= var16.size()) {
                                                        break;
                                                    }

                                                    if (var20 > 0) {
                                                        var25.append(',');
                                                    }

                                                    var25.append((String)var16.get(var21));
                                                }

                                                var25.append(')');
                                            }

                                            var25.append(')');
                                            var6 = var31.getEndIndex();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    var1 = var25.toString() + var1.substring(var6, var1.length());
                    break;
                }
            }

            SymbolParser var29 = new SymbolParser(var1);
            StringBuffer var30 = new StringBuffer();
            int var32 = 0;
            var25.setLength(0);

            for(; var29.find(); var29.moveTo(var32)) {
                var33 = var29.getNet();
                var25.append(var1.substring(var32, var29.getBeginIndex()));
                var32 = var29.getEndIndex();
                if (var33.equals("+")) {
                    if (!var0.equals("SYBASE") && !var0.equals("MSSQL") && !var0.equals("ACCESS")) {
                        if (!var0.equals("ORACLE") && !var0.equals("DB2")) {
                            if (var0.equals("MYSQL")) {
                                boolean var35 = false;
                                int var36 = 0;
                                char var38 = 0;
                                boolean var40 = false;
                                var14 = 0;

                                for(var15 = var25.length() - 1; var15 >= 0; --var15) {
                                    char var46 = var25.charAt(var15);
                                    if (var35) {
                                        var35 = var46 != var38;
                                        if (!var35 && var36 == 0) {
                                            var14 = var15;
                                            break;
                                        }
                                    } else if (var46 != '\'' && var46 != '"') {
                                        if (var36 > 0) {
                                            if (var46 == '(') {
                                                --var36;
                                                if (var36 == 0) {
                                                    var14 = var15;
                                                    break;
                                                }
                                            } else if (var46 == ')') {
                                                ++var36;
                                            }
                                        } else if (var46 == ')') {
                                            var36 = 1;
                                        } else if ("+-*/%=!~^|#$@;:'\"()[]<>{}, \t\r\n".indexOf(var46) >= 0) {
                                            if (!var35 && var36 == 0 && var40) {
                                                var14 = var15 + 1;
                                                break;
                                            }
                                        } else {
                                            var40 = true;
                                        }
                                    } else {
                                        var35 = true;
                                        var38 = var46;
                                    }
                                }

                                var25.insert(var14, "CONCAT(");
                                var25.append(',');
                                var35 = false;
                                var36 = 0;
                                var38 = 0;
                                var40 = false;
                                var15 = var32;

                                for(int var47 = var32; var47 < var1.length(); ++var47) {
                                    char var48 = var1.charAt(var47);
                                    if (var35) {
                                        var35 = var48 != var38;
                                        if (!var35 && var36 == 0) {
                                            var15 = var47 + 1;
                                            break;
                                        }
                                    } else if (var48 != '\'' && var48 != '"') {
                                        if (var36 > 0) {
                                            if (var48 == ')') {
                                                --var36;
                                                if (var36 == 0) {
                                                    var15 = var47 + 1;
                                                    break;
                                                }
                                            } else if (var48 == '(') {
                                                ++var36;
                                            }
                                        } else if (var48 == '(') {
                                            var36 = 1;
                                        } else if ("+-*/%=!~^|#$@;:'\"()[]<>{}, \t\r\n".indexOf(var48) >= 0 && var48 != '$') {
                                            if (!var35 && var36 == 0 && var40) {
                                                var15 = var47;
                                                break;
                                            }
                                        } else {
                                            var40 = true;
                                        }
                                    } else {
                                        var35 = true;
                                        var38 = var48;
                                    }
                                }

                                var25.append(parseSql(var0, var1.substring(var32, var15)));
                                var25.append(")");
                                var32 = var15;
                            }
                        } else {
                            var25.append("||");
                        }
                    } else {
                        var25.append("+");
                    }
                } else if (var33.length() > 2 && var33.charAt(0) == '@') {
                    SymbolParser var39 = new SymbolParser(var33, '@', '@');
                    if (var39.find()) {
                        var11 = var39.getNet() + ';';
                        if (var11.indexOf(var0 + ';') >= 0) {
                            var25.append(var33.substring(var39.getEndIndex()));
                        }
                    }
                } else {
                    var30.setLength(0);

                    while(var32 < var1.length() && (var1.charAt(var32) == ' ' || var1.charAt(var32) == '\t' || var1.charAt(var32) == '\r' || var1.charAt(var32) == '\n')) {
                        var30.append(var1.charAt(var32));
                        ++var32;
                    }

                    String var37 = "";
                    if (var32 < var1.length() && var1.charAt(var32) == '(') {
                        SymbolParser var41 = new SymbolParser(var1.substring(var32), '(', ')');
                        if (var41.find()) {
                            var37 = var41.getNet();
                            var32 += var41.getEndIndex();
                        }

                        var30.setLength(0);
                    }

                    ArrayList var43 = new ArrayList();
                    if (var37.length() > 0) {
                        ParameterParser var42 = new ParameterParser(var37);

                        while(var42.find()) {
                            var43.add(parseSql(var0, var42.get()).trim());
                        }
                    }

                    if (var33.equalsIgnoreCase("IIF")) {
                        if (var43.size() != 3) {
                            throw new SQLException("Invalid parameter count: $IIF$(Condition, Value1, Value2)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("(CASE WHEN " + (String)var43.get(0) + " THEN " + (String)var43.get(1) + " ELSE " + (String)var43.get(2) + " END)");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("(CASE WHEN " + (String)var43.get(0) + " THEN " + (String)var43.get(1) + " ELSE " + (String)var43.get(2) + " END)");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("iif(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ", " + (String)var43.get(2) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("(CASE WHEN " + (String)var43.get(0) + " THEN " + (String)var43.get(1) + " ELSE " + (String)var43.get(2) + " END)");
                            }
                        } else {
                            var25.append("(CASE WHEN " + (String)var43.get(0) + " THEN " + (String)var43.get(1) + " ELSE " + (String)var43.get(2) + " END)");
                        }
                    } else if (var33.equalsIgnoreCase("DATE")) {
                        if (var0.equals("DB2")) {
                            var25.append("CURRENT DATE");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TRUNC(SYSDATE, 'DD')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("date()");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("CURDATE()");
                            }
                        } else {
                            var25.append("convert(datetime, convert(char(8), getdate(), 112))");
                        }
                    } else if (var33.equalsIgnoreCase("TIME")) {
                        if (var0.equals("DB2")) {
                            var25.append("CURRENT TIME");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_DATE('00010101' || TO_CHAR(SYSDATE, 'HH24MISS'), 'YYYYMMDDHH24MISS')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("time()");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("CURTIME()");
                            }
                        } else {
                            var25.append("convert(datetime, convert(char(8), getdate(), 108))");
                        }
                    } else if (var33.equalsIgnoreCase("NOW")) {
                        if (var0.equals("DB2")) {
                            var25.append("CURRENT TIMESTAMP");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("SYSDATE");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("now()");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("NOW()");
                            }
                        } else {
                            var25.append("getdate()");
                        }
                    } else if (var33.equalsIgnoreCase("DAYDIFF")) {
                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: $DAYDIFF$(Date1, Date2)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("(DAYS(" + (String)var43.get(1) + ") - DAYS(" + (String)var43.get(0) + "))");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("(TRUNC(" + (String)var43.get(1) + ") - TRUNC(" + (String)var43.get(0) + ") + 0)");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("((" + (String)var43.get(1) + " - " + (String)var43.get(0) + ") + 0)");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("DATEDIFF(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            }
                        } else {
                            var25.append("datediff(day, " + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("DAYADD")) {
                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: DAYADD(DateTime, value)");
                        }

                        boolean var44 = false;
                        int var45 = 0;

                        try {
                            var45 = Integer.parseInt((String)var43.get(1));
                        } catch (NumberFormatException var22) {
                        }

                        if (var0.equals("DB2")) {
                            if (var44) {
                                var25.append("(" + (String)var43.get(0) + (var45 >= 0 ? " + " : "-") + Math.abs(var45) + " day" + (Math.abs(var45) > 1 ? "s" : "") + ")");
                            } else {
                                var25.append("(" + (String)var43.get(0) + " + " + (String)var43.get(1) + " day)");
                            }
                        } else if (var0.equals("ORACLE")) {
                            if (var44) {
                                var25.append("(" + (String)var43.get(0) + (var45 >= 0 ? " + " : "-") + Math.abs(var45) + ")");
                            } else {
                                var25.append("(" + (String)var43.get(0) + " + " + (String)var43.get(1) + ")");
                            }
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("dateadd('d', " + (String)var43.get(1) + ", " + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("DATE_ADD(" + (String)var43.get(0) + ", INTERVAL " + (String)var43.get(1) + " DAY)");
                            }
                        } else {
                            var25.append("dateadd(day, " + (String)var43.get(1) + ", " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("DATEVALUE")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $DAYVALUE$(DateTime)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("DATE(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TRUNC(" + (String)var43.get(0) + ", 'DD')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("datevalue(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("DATE(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("convert(smalldatetime, convert(varchar, " + (String)var43.get(0) + ", 101), 101)");
                        }
                    } else if (var33.equalsIgnoreCase("TIMEVALUE")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count : $TIMEVALUE$(DateTime)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("TIME(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_DATE('00010101' || TO_CHAR(" + (String)var43.get(0) + ", 'HH24MISS'), 'YYYYMMDDHH24MISS')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("timevalue(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("MAKETIME(HOUR(" + (String)var43.get(0) + "), " + "MINUTE(" + (String)var43.get(0) + "), " + "SECOND(" + (String)var43.get(0) + "))");
                            }
                        } else {
                            var25.append("convert(datetime, convert(varchar, " + (String)var43.get(0) + ",114), 114)");
                        }
                    } else if (var33.equalsIgnoreCase("STRTODATE")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $STRTODATE$(DateString)");
                        }

                        if (var0.equals("DB2")) {
                            var2 = (String)var43.get(0);
                            if (var2.length() == 10 && var2.charAt(0) == '\'' && var2.charAt(9) == '\'') {
                                var25.append("'" + var2.substring(1, 5) + "-" + var2.substring(5, 7) + "-" + var2.substring(7, 9) + "'");
                            } else {
                                var25.append("DATE(SUBSTR(" + var2 + ", 1, 4) || '-' || SUBSTR(" + var2 + ", 5, 2) || '-' || SUBSTR(" + var2 + ", 7, 2))");
                            }
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_DATE(" + (String)var43.get(0) + ", 'YYYYMMDD')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var2 = (String)var43.get(0);
                                if (var2.length() == 10 && var2.charAt(0) == '\'' && var2.charAt(9) == '\'') {
                                    var25.append("datevalue('" + var2.substring(1, 5) + "-" + var2.substring(5, 7) + "-" + var2.substring(7, 9) + "')");
                                } else {
                                    var25.append("datevalue(mid(" + var2 + ", 1, 4) + '-' + mid(" + var2 + ", 5, 2) + '-' + mid(" + var2 + ", 7, 2))");
                                }
                            } else if (var0.equals("MYSQL")) {
                                var25.append("STR_TO_DATE(" + (String)var43.get(0) + ", '%Y%m%d')");
                            }
                        } else {
                            var2 = (String)var43.get(0);
                            if (var2.length() == 10 && var2.charAt(0) == '\'' && var2.charAt(9) == '\'') {
                                var25.append(var2);
                            } else {
                                var25.append("convert(datetime, " + var2 + ")");
                            }
                        }
                    } else if (var33.equalsIgnoreCase("STRTOTIME")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $STRTOTIME$(TimeStr)");
                        }

                        if (var0.equals("DB2")) {
                            var2 = (String)var43.get(0);
                            if (var2.length() == 8 && var2.charAt(0) == '\'' && var2.charAt(7) == '\'') {
                                var25.append('\'' + var2.substring(1, 3) + ':' + var2.substring(3, 5) + ':' + var2.substring(5, 7) + '\'');
                            } else {
                                var25.append("TIME(SUBSTR(" + var2 + ", 1, 2) || ':' || SUBSTR(" + var2 + ", 3, 2) || ':' || SUBSTR(" + var2 + ", 5, 2))");
                            }
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_DATE('18991231' || " + (String)var43.get(0) + ", 'YYYYMMDDHH24MISS')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var2 = (String)var43.get(0);
                                if (var2.length() == 8 && var2.charAt(0) == '\'' && var2.charAt(7) == '\'') {
                                    var25.append("timevalue('" + var2.substring(1, 3) + ":" + var2.substring(3, 5) + ":" + var2.substring(5, 7) + "')");
                                } else {
                                    var25.append("timevalue(mid(" + var2 + ", 1, 2) + ':' + mid(" + var2 + ", 3, 2) + ':' + mid(" + var2 + ", 5, 2))");
                                }
                            } else if (var0.equals("MYSQL")) {
                                var25.append("STR_TO_DATE(" + (String)var43.get(0) + ", '%H%i%S')");
                            }
                        } else {
                            var2 = (String)var43.get(0);
                            if (var2.length() == 8 && var2.charAt(0) == '\'' && var2.charAt(7) == '\'') {
                                var25.append("convert(datetime, '" + var2.substring(1, 3) + ":" + var2.substring(3, 5) + ":" + var2.substring(5, 7) + "', 108)");
                            } else {
                                var25.append("convert(datetime, " + var2 + ", 108)");
                            }
                        }
                    } else if (var33.equalsIgnoreCase("STRTODATETIME")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $STRTODATETIME$(DateTimeStr)");
                        }

                        if (var0.equals("DB2")) {
                            var2 = (String)var43.get(0);
                            if (var2.length() == 16 && var2.charAt(0) == '\'' && var2.charAt(15) == '\'') {
                                var25.append("'" + var2.substring(1, 5) + "-" + var2.substring(5, 7) + "-" + var2.substring(7, 9) + "-" + var2.substring(9, 11) + "." + var2.substring(11, 13) + "." + var2.substring(13, 15) + "'");
                            } else {
                                var25.append("TIMESTAMP(" + var2 + ")");
                            }
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_DATE(" + (String)var43.get(0) + ", 'YYYYMMDDHH24MISS')");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var2 = (String)var43.get(0);
                                if (var2.length() == 16 && var2.charAt(0) == '\'' && var2.charAt(15) == '\'') {
                                    var25.append("datevalue('" + var2.substring(1, 5) + "-" + var2.substring(5, 7) + "-" + var2.substring(7, 9) + "') + " + "timevalue('" + var2.substring(9, 11) + ":" + var2.substring(11, 13) + ":" + var2.substring(13, 15) + "')");
                                } else {
                                    var25.append("datevalue(mid(" + var2 + ", 1, 4) + '-' + mid(" + var2 + ", 5, 2) + '-' + mid(" + var2 + ", 7, 2)) + " + "timevalue(mid(" + var2 + ", 9, 2) + ':' + mid(" + var2 + ", 11, 2) + ':' + mid(" + var2 + ", 13, 2))");
                                }
                            } else if (var0.equals("MYSQL")) {
                                var25.append("STR_TO_DATE(" + (String)var43.get(0) + ", '%Y%m%d%H%i%S')");
                            }
                        } else {
                            var2 = (String)var43.get(0);
                            if (var2.length() == 16 && var2.charAt(0) == '\'' && var2.charAt(15) == '\'') {
                                var25.append('\'' + var2.substring(1, 9) + " " + var2.substring(9, 11) + ":" + var2.substring(11, 13) + ":" + var2.substring(13, 15) + '\'');
                            } else {
                                var25.append("convert(datetime, " + var2 + ")");
                            }
                        }
                    } else if (var33.equalsIgnoreCase("STRTONUMBER")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $STRTONUMBER$(Number)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("DECIMAL(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(" + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("(" + (String)var43.get(0) + " + 0)");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("CAST(" + (String)var43.get(0) + " AS DECIMAL)");
                            }
                        } else {
                            var25.append("convert(float, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("NUMBERTOSTR")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $NUMBERTOSTR$(Number)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("CHAR(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_CHAR(" + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("trim(str(" + (String)var43.get(0) + "))");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("CAST(" + (String)var43.get(0) + " AS CHAR)");
                            }
                        } else {
                            var25.append("convert(varchar, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("YEAR")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $YEAR$(Date)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("YEAR(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("EXTRACT(YEAR FROM " + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("year(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("YEAR(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(year, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("MONTH")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $MONTH$(Date)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("MONTH(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("EXTRACT(MONTH FROM " + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("month(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("MONTH(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(month, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("DAY")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $DAY$(Date)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("DAY(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("EXTRACT(DAY FROM " + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("day(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("DAY(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(day, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("DAYOFWEEK")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $DAYOFWEEK$(Date)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("DAYOFWEEK(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(TO_CHAR(" + (String)var43.get(0) + ", 'D'))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("weekday(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("DAYOFWEEK(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(weekday, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("WEEKOFYEAR")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $WEEKOFYEAR$(Date)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("WEEK(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(TO_CHAR(" + (String)var43.get(0) + ", 'WW'))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("datepart('ww', " + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("WEEKOFYEAR(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(week, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("DAYOFYEAR")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $DAYOFYEAR$(Date)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("DAYOFYEAR(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(TO_CHAR(" + (String)var43.get(0) + ", 'DDD'))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("datepart('y', " + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("DAYOFYEAR(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(dayofyear, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("HOUR")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $HOUR$(Time)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("HOUR(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(TO_CHAR(" + (String)var43.get(0) + ", 'HH24'))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("hour(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("HOUR(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(hour, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("MINUTE")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $HOUR$(Time)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("MINUTE(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(TO_CHAR(" + (String)var43.get(0) + ", 'MI'))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("minute(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("MINUTE(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(minute, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("SECOND")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $HOUR$(Time)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("SECOND(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("TO_NUMBER(TO_CHAR(" + (String)var43.get(0) + ", 'MI'))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("second(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("SECOND(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("datepart(second, " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("STRLEN")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $STRLEN(String)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("LENGTH(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("LENGTH(" + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("len(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("LENGTH(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("len(" + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("SUBSTR")) {
                        if (var43.size() != 3) {
                            throw new SQLException("Invalid parameter count: $SUBSTR$(String, FromIndex, Count)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("SUBSTR(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ", " + (String)var43.get(2) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("SUBSTR(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ", " + (String)var43.get(2) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("mid(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ", " + (String)var43.get(2) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("SUBSTRING(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ", " + (String)var43.get(2) + ")");
                            }
                        } else {
                            var25.append("substring(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ", " + (String)var43.get(2) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("LEFT")) {
                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: $LEFT$(String, Count)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("LEFT(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("SUBSTR(" + (String)var43.get(0) + ", 1, " + (String)var43.get(1) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("left(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("LEFT(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            }
                        } else {
                            var25.append("substring(" + (String)var43.get(0) + ", 1, " + (String)var43.get(1) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("RIGHT")) {
                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: $RIGHT$(String, Count)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("RIGHT(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("SUBSTR(" + (String)var43.get(0) + ", - " + (String)var43.get(1) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("right(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("RIGHT(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            }
                        } else {
                            var25.append("right(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("TRIM")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $TRIM$(String)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("TRIM(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("LTRIM(RTRIM(" + (String)var43.get(0) + "))");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("trim(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("TRIM(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("ltrim(rtrim(" + (String)var43.get(0) + "))");
                        }
                    } else if (var33.equalsIgnoreCase("POS")) {
                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: $POS$(String, Keyword)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("POSSTR(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("INSTR(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("instr(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("INSTR(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            }
                        } else {
                            var25.append("charindex(" + (String)var43.get(1) + ", " + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("ROUND")) {
                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: $ROUND$(Value, Precision)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("ROUND(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("ROUND(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("round(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("ROUND(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            }
                        } else {
                            var25.append("round(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("FLOOR")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $FLOOR$(Value)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("FLOOR(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("FLOOR(" + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("int(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("FLOOR(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("floor(" + (String)var43.get(0) + ")");
                        }
                    } else if (var33.equalsIgnoreCase("ABS")) {
                        if (var43.size() != 1) {
                            throw new SQLException("Invalid parameter count: $ABS$(Value)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("ABS(" + (String)var43.get(0) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("ABS(" + (String)var43.get(0) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("abs(" + (String)var43.get(0) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("ABS(" + (String)var43.get(0) + ")");
                            }
                        } else {
                            var25.append("abs(" + (String)var43.get(0) + ")");
                        }
                    } else {
                        if (!var33.equalsIgnoreCase("MOD")) {
                            throw new SQLException("Unknown method '" + var33 + "'.");
                        }

                        if (var43.size() != 2) {
                            throw new SQLException("Invalid parameter count: $MOD$(Dividend, Divisor)");
                        }

                        if (var0.equals("DB2")) {
                            var25.append("MOD(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (var0.equals("ORACLE")) {
                            var25.append("MOD(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                        } else if (!var0.equals("SYBASE") && !var0.equals("MSSQL")) {
                            if (var0.equals("ACCESS")) {
                                var25.append("(" + (String)var43.get(0) + " mod " + (String)var43.get(1) + ")");
                            } else if (var0.equals("MYSQL")) {
                                var25.append("MOD(" + (String)var43.get(0) + ", " + (String)var43.get(1) + ")");
                            }
                        } else {
                            var25.append("(" + (String)var43.get(0) + " % " + (String)var43.get(1) + ")");
                        }
                    }

                    if (var30.length() > 0) {
                        var25.append(var30.toString());
                    }
                }
            }

            if (var32 < var1.length()) {
                var25.append(var1.substring(var32));
            }

            var1 = var25.toString();
            if (var3 > 0 && (var0.equals("MSSQL") || var0.equals("ACCESS") || var0.equals("ORACLE") || var0.equals("DB2") || var0.equals("SYBASE") || var0.equals("MYSQL"))) {
                if (!var0.equals("MSSQL") && !var0.equals("ACCESS")) {
                    if (var0.equals("ORACLE")) {
                        var1 = "SELECT * FROM (" + var1 + ") WHERE ROWNUM <= " + Integer.toString(var3);
                    } else if (var0.equals("DB2")) {
                        var1 = var1 + " FETCH FIRST " + Integer.toString(var3) + " ROWS ONLY";
                    } else if (var0.equals("SYBASE")) {
                        var1 = "SET ROWCOUNT " + Integer.toString(var3) + " " + var1 + " SET ROWCOUNT 0";
                    } else if (var0.equals("MYSQL")) {
                        var1 = var1 + " LIMIT " + Integer.toString(var3);
                    }
                } else {
                    TokenParser var34 = new TokenParser(var1);
                    var10 = -1;
                    if (var34.find() && var34.get().equalsIgnoreCase("SELECT")) {
                        var10 = var34.getEndIndex();
                        if (var34.find() && var34.get().equalsIgnoreCase("DISTINCT")) {
                            var10 = var34.getEndIndex();
                        }
                    }

                    if (var10 > 0) {
                        var1 = var1.substring(0, var10) + " TOP " + Integer.toString(var3) + var1.substring(var10);
                    }
                }
            }

            return var1;
        }
    }

    public Statement createStatement() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createStatement();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public PreparedStatement prepareStatement(String var1) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareStatement(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public CallableStatement prepareCall(String var1) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareCall(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public String nativeSQL(String var1) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.nativeSQL(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getAutoCommit();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getMetaData();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public void setReadOnly(boolean var1) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setReadOnly(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public boolean isReadOnly() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.isReadOnly();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public void setCatalog(String var1) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setCatalog(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public String getCatalog() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getCatalog();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public void setTransactionIsolation(int var1) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setTransactionIsolation(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public int getTransactionIsolation() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getTransactionIsolation();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getWarnings();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            this.checkIfClosed();
            connection.clearWarnings();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public Statement createStatement(int var1, int var2) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createStatement(var1, var2);
        } catch (SQLException var4) {
            hasErrorOccurred = false;
            throw var4;
        }
    }

    public PreparedStatement prepareStatement(String var1, int var2, int var3) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareStatement(var1, var2, var3);
        } catch (SQLException var5) {
            hasErrorOccurred = false;
            throw var5;
        }
    }

    public CallableStatement prepareCall(String var1, int var2, int var3) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareCall(var1, var2, var3);
        } catch (SQLException var5) {
            hasErrorOccurred = false;
            throw var5;
        }
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getTypeMap();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public void setTypeMap(Map<String, Class<?>> var1) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setTypeMap(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public void setHoldability(int var1) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setHoldability(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public int getHoldability() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getHoldability();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public Statement createStatement(int var1, int var2, int var3) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createStatement(var1, var2, var3);
        } catch (SQLException var5) {
            hasErrorOccurred = false;
            throw var5;
        }
    }

    public PreparedStatement prepareStatement(String var1, int var2, int var3, int var4) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareCall(var1, var2, var3, var4);
        } catch (SQLException var6) {
            hasErrorOccurred = false;
            throw var6;
        }
    }

    public CallableStatement prepareCall(String var1, int var2, int var3, int var4) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareCall(var1, var2, var3, var4);
        } catch (SQLException var6) {
            hasErrorOccurred = false;
            throw var6;
        }
    }

    public PreparedStatement prepareStatement(String var1, int var2) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareStatement(var1, var2);
        } catch (SQLException var4) {
            hasErrorOccurred = false;
            throw var4;
        }
    }

    public PreparedStatement prepareStatement(String var1, int[] var2) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareStatement(var1, var2);
        } catch (SQLException var4) {
            hasErrorOccurred = false;
            throw var4;
        }
    }

    public PreparedStatement prepareStatement(String var1, String[] var2) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.prepareStatement(var1, var2);
        } catch (SQLException var4) {
            hasErrorOccurred = false;
            throw var4;
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.setSavepoint();
        } catch (SQLException var2) {
            hasErrorOccurred = false;
            throw var2;
        }
    }

    public Savepoint setSavepoint(String var1) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.setSavepoint(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {
        try {
            this.checkIfClosed();
            return connection.isWrapperFor(iface);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
        try {
            this.checkIfClosed();
            return connection.unwrap(iface);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setNetworkTimeout(executor, milliseconds);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public int getNetworkTimeout() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getNetworkTimeout();
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public void abort(Executor executor) throws SQLException {
        try {
            this.checkIfClosed();
            connection.abort(executor);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public void  setSchema(String schema) throws SQLException {
        try {
            this.checkIfClosed();
            connection.setSchema(schema);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createStruct(typeName, attributes);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createArrayOf(typeName, elements);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public boolean isValid(int timeout) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.isValid(timeout);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public String getClientInfo(String name) throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getClientInfo(name);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public SQLXML createSQLXML() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createSQLXML();
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public NClob createNClob() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createNClob();
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public Blob createBlob() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createBlob();
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public Clob createClob() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.createClob();
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public Properties getClientInfo() throws SQLException {
        try {
            this.checkIfClosed();
            return connection.getClientInfo();
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            this.checkIfClosed();
            connection.setClientInfo(name, value);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            //throw var3;
        }
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            this.checkIfClosed();
            connection.setClientInfo(properties);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            //throw var3;
        }
    }

    public void rollback(Savepoint var1) throws SQLException {
        try {
            this.checkIfClosed();
            boolean var2 = false;
            int var3 = 0;

            while(var3 < 10) {
                try {
                    connection.rollback(var1);
                    var2 = true;
                    break;
                } catch (Throwable var12) {
                    try {
                        Thread.sleep((long)(var3 * 100));
                    } catch (InterruptedException var11) {
                    }

                    ++var3;
                }
            }

            if (!var2) {
                try {
                    connection.close();
                } finally {
                    connection = null;
                }
            }

        } catch (Throwable var13) {
            hasErrorOccurred = false;
            if (var13 instanceof SQLException) {
                throw (SQLException)var13;
            } else {
                throw new SQLException(var13.getMessage());
            }
        }
    }

    public void releaseSavepoint(Savepoint var1) throws SQLException {
        try {
            this.checkIfClosed();
            connection.releaseSavepoint(var1);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            throw var3;
        }
    }
/*
    public void removeStatementEventListener(StatementEventListener listener) {
        try {
            this.checkIfClosed();
           // connection.removeStatementEventListener(listener);
        } catch (SQLException var3) {
            hasErrorOccurred = false;
            //throw var3
        }
    }
*/
}
