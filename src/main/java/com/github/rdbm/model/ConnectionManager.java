package com.github.rdbm.model;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by maxtar.
 */
public class ConnectionManager {

    private String url;
    private String driverName;
    private String username;
    private String password;

    public ConnectionManager(String driverName, String url, String username, String password) {
        this.driverName = driverName;
        this.username = username;
        this.password = password;
        this.url = url;
//        url = "jdbc:" + type + ":file:" + path.substring(1);
    }

    public ConnectionManager(String type, String path, String driverName, String username, String password) {
        this.driverName = driverName;
        this.username = username;
        this.password = password;
        url = "jdbc:" + type + ":file:" + path.substring(1);
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        return DriverManager.getConnection(url, username, password);
    }

    public ComboPooledDataSource getComboPooledDataSource() throws PropertyVetoException {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass(driverName);
        cpds.setJdbcUrl(url);
        cpds.setUser(username);
        cpds.setPassword(password);
        return cpds;
    }

}
