package cn.ne.orm.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import cn.ne.orm.bean.Configuration;

/**
 * 根据配置信息，维持连接对象的管理(增加连接池功能)
 * 
 * @author NightEagle
 * @version 0.0.1
 */
public class DBManager {
	private static Configuration conf;

	// 加载资源文件，初始化DB连接信息参数
	static {
		Properties pros = new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		conf = new Configuration();
		conf.setDriver(pros.getProperty("driver"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setUsingDB(pros.getProperty("usingDB"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setPoPackage(pros.getProperty("poPackage"));

	}

	// 创建Connection连接对象
	public static Connection getConn() {
		try {
			Class.forName(conf.getDriver());
			return DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 返回Configuration对象
	public static Configuration getConf() {
		return conf;
	}

	// 关闭数据库连接，多次重载。

	public static void close(ResultSet rs, Statement ps, Connection conn) {
		try {
			if (rs != null) { rs.close(); }
			if (ps != null) { ps.close(); }
			if (conn != null) { conn.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(Statement ps, Connection conn) {
		try {
			if (ps != null) { ps.close();}
			if (conn != null) { conn.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null) { conn.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
