//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.evangelsoft.econnect.db;

import com.evangelsoft.econnect.util.Encrypter;
import com.evangelsoft.econnect.util.Pool;
import com.evangelsoft.econnect.util.PooledObjectCleaner;
import com.evangelsoft.econnect.util.ResourceLocater;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

public class DatabasePool implements ConnectionEventListener {
    public static final String PM_URL = "url";
    public static final String PM_DRIVER = "driver";
    public static final String PM_PROPERTIES = "properties";
    public static final String PM_USER = "user";
    public static final String PM_PASSWORD = "password";
    public static final String PM_HOLDABILITY = "holdability";
    public static final String PM_TRANSACTION_ISOLATION = "transaction_isolation";
    public static final String PM_SCHEMA = "schema";
    public static final String PM_LOGIN_TIMEOUT = "login_timeout";
    public static final String PM_POOL = "pool";
    public static final String PM_LIFE_PERIOD = "life_period";
    public static final String PM_MAX_CONNECTIONS = "max_connections";
    private String databaseName;
    private String dbUrl;
    private Properties dbProperties = new Properties();
    private Pool<Connection> connectionPool;
    private Vector<Connection> activeConnections = new Vector();
    private int maxConnections;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(DatabasePool.class.getPackage().getName() + ".Res");

    public DatabasePool(String dbName) throws SQLException {
        this.databaseName = dbName;
        this.connectionPool = this.initializePool();
    }

    private Pool<Connection> initializePool() throws SQLException {
        Properties properties = new Properties();
        String configPath = this.databaseName + ".ds";
        InputStream configStream = ResourceLocater.loadStream(configPath);
        if (configStream != null) {
            try {
            	properties.load(configStream);
            } catch (Throwable ex) {
                System.out.println(ex.getMessage());
            }

            try {
            	configStream.close();
            } catch (Throwable var19) {
            }

            String var5 = properties.getProperty("pool", "-1");
            if (var5 != null && var5.length() != 0) {
                int var4;
                try {
                    var4 = Integer.parseInt(var5);
                } catch (NumberFormatException var18) {
                    throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_INVALID_PARAMETER"), var5, "pool"));
                }

                var5 = properties.getProperty("life_period", "-1");
                if (var5 != null && var5.length() != 0) {
                    int var6;
                    try {
                        var6 = Integer.parseInt(var5);
                    } catch (NumberFormatException var17) {
                        throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_INVALID_PARAMETER"), var5, "life_period"));
                    }

                    var5 = properties.getProperty("max_connections", "-1");

                    try {
                        this.maxConnections = Integer.parseInt(var5);
                    } catch (NumberFormatException var16) {
                        throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_INVALID_PARAMETER"), var5, "max_connections"));
                    }

                    if (this.maxConnections <= 0) {
                        this.maxConnections = 2147483647;
                    }

                    this.dbUrl = properties.getProperty("url");
                    if (this.dbUrl != null && this.dbUrl.length() != 0) {
                        String var7 = properties.getProperty("driver");
                        if (var7 != null && var7.length() != 0) {
                            try {
                                Class.forName(var7);
                            } catch (ClassNotFoundException var15) {
                                throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_UNKNOWN_DRIVER"), var7));
                            }

                            Driver var8 = DriverManager.getDriver(this.dbUrl);
                            String var9 = properties.getProperty("properties");
                            int var11;
                            String var12;
                            if (var9 != null && var9.trim().length() != 0) {
                                var9 = var9 + ";";

                                for(int var21 = 0; (var11 = var9.indexOf(59, var21)) >= var21; var21 = var11) {
                                    if (var21 < var11) {
                                        var12 = var9.substring(var21, var11).trim();
                                        String var13 = properties.getProperty(var12);
                                        if (var13 != null) {
                                            if (var12.equalsIgnoreCase("password")) {
                                                var13 = Encrypter.decrypt(var13);
                                            }

                                            this.dbProperties.put(var12, var13);
                                        }
                                    }

                                    ++var11;
                                }
                            } else {
                                DriverPropertyInfo[] var10 = var8.getPropertyInfo(this.dbUrl, new Properties());

                                for(var11 = 0; var11 < var10.length; ++var11) {
                                    var12 = properties.getProperty(var10[var11].name);
                                    if (var12 != null) {
                                        if (var10[var11].name.equalsIgnoreCase("password")) {
                                            var12 = Encrypter.decrypt(var12);
                                        }

                                        this.dbProperties.put(var10[var11].name, var12);
                                    }
                                }
                            }

                            var5 = properties.getProperty("schema", "");
                            if (var5.length() > 0) {
                                this.dbProperties.put("schema", var5);
                            }

                            var5 = properties.getProperty("login_timeout", "");

                            try {
                                DriverManager.setLoginTimeout(Integer.parseInt(var5));
                            } catch (NumberFormatException var14) {
                            }

                            var5 = properties.getProperty("transaction_isolation");
                            if (var5 != null && var5.length() > 0) {
                                this.dbProperties.put("transaction_isolation", var5);
                            }

                            var5 = properties.getProperty("holdability");
                            if (var5 != null && var5.length() > 0) {
                                this.dbProperties.put("holdability", var5);
                            }

                            return new Pool(var4, var6, new ConnectionFactory(this.dbUrl, this.dbProperties), new PooledObjectCleaner() {
                                public void clean(Object var1) {
                                    try {
                                        ((Connection)var1).close();
                                    } catch (Throwable var3) {
                                    }

                                }
                            });
                        } else {
                            throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_MISSING_PARAMETER"), "driver"));
                        }
                    } else {
                        throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_MISSING_PARAMETER"), "url"));
                    }
                } else {
                    throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_MISSING_PARAMETER"), "pool"));
                }
            } else {
                throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_MISSING_PARAMETER"), "pool"));
            }
        } else {
            throw new SQLException(MessageFormat.format(resourceBundle.getString("MSG_MISSING_CONFIG_FILE"), configPath));
        }
    }

    protected void finalize() throws Throwable {
        this.connectionPool.close();
        super.finalize();
    }

    public String getName() {
        return this.databaseName;
    }

    public Connection getConnection() throws SQLException {
        if (this.activeConnections.size() >= this.maxConnections) {
            throw new SQLException(resourceBundle.getString("MSG_CONNECTIONS_HAVE_BEEN_EXHAUSTED"));
        } else {
            try {
                Connection var1 = (Connection)this.connectionPool.pop();
                Database var2 = new Database(var1, true, this.dbProperties);
                var2.addConnectionEventListener(this);
                this.activeConnections.add(var1);
                return var2;
            } catch (Throwable var3) {
                throw new SQLException(var3.getMessage());
            }
        }
    }

    public void clear() {
        this.connectionPool.clear();
    }

    public void reset() throws SQLException {
        Pool var1 = this.connectionPool;
        this.connectionPool = this.initializePool();
        synchronized(this.activeConnections) {
            int var3 = 0;

            while(true) {
                if (var3 >= this.activeConnections.size()) {
                    break;
                }

                Object var4 = this.activeConnections.get(var3);
                if (var4 instanceof Database) {
                    ((Database)var4).hasErrorOccurred = false;
                }

                ++var3;
            }
        }

        this.clear();
        var1.close();
    }

    public int getActiveCount() {
        return this.activeConnections.size();
    }

    public int getPooledCount() {
        return this.connectionPool.getSize();
    }

    public void connectionClosed(ConnectionEvent var1) {
        PooledConnection var2 = (PooledConnection)var1.getSource();

        try {
            Connection var3 = var2.getConnection();
            if (var3 == null) {
                return;
            }

            this.activeConnections.remove(var3);
            boolean var4 = false;
            if (var2 instanceof Database) {
                Database var5 = (Database)var2;
                if (var5.hasErrorOccurred && !var3.isClosed()) {
                    var4 = true;
                    var5.connection = null;
                    if (!var3.getAutoCommit()) {
                        var3.rollback();
                    }

                    this.connectionPool.push(var3);
                }
            }

            if (!var4) {
                var3.close();
            }
        } catch (Throwable var6) {
        }

    }

    public void connectionErrorOccurred(ConnectionEvent var1) {
        PooledConnection var2 = (PooledConnection)var1.getSource();

        try {
            if (var2.getConnection() == null) {
                return;
            }

            this.activeConnections.remove(var2.getConnection());
            var2.getConnection().close();
        } catch (Throwable var4) {
        }

    }
}
