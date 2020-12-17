package cn.ne.dydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    private DBConn(){}
    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL = new ThreadLocal<>();

    private static void rebuildConnection() {
        try {
            Class.forName(DBResource.getProperty("datasource.driver"));
            Connection conn = DriverManager.getConnection(
                    DBResource.getProperty("datasource.url"),
                    DBResource.getProperty("datasource.username"),
                    DBResource.getProperty("datasource.password")
            );
            CONNECTION_THREAD_LOCAL.set(conn);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn() {
        Connection conn = CONNECTION_THREAD_LOCAL.get();
        if (conn == null) {
            rebuildConnection();
            conn = CONNECTION_THREAD_LOCAL.get();
        }
        return conn;
    }


    public static void close() {
        Connection conn = CONNECTION_THREAD_LOCAL.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            CONNECTION_THREAD_LOCAL.remove();
        }
    }
}
