package cn.ne.orm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * JDBC查询常用的操作
 * @author NightEagle
 * @version 0.0.1
 */
public class JDBCUtils {
	
	/**
	 * 设置sql语句中的参数
	 * @param pstm		预编译sql语句对象
	 * @param params	参数
	 * @exception		SQL异常
	 */
	public static void handleParams (PreparedStatement pstm ,Object[] params) {
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				try {
					pstm.setObject(i+1, params[i]);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
