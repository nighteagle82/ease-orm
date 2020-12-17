package cn.ne.orm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.ne.orm.bean.ColumnInfo;
import cn.ne.orm.bean.TableInfo;
import cn.ne.orm.utils.JDBCUtils;
import cn.ne.orm.utils.ReflectUtils;
import cn.ne.pojo.Emp;

/**
 * 使用该类进行Mysql数据库的查询
 * 
 * @author NightEagle
 * @version 0.0.3
 */
public class MySqlQuery implements Query {

	public MySqlQuery() {
		// 执行MYSQL操作时加载pojo包下面所有的类对象实例。
		TableContext.loadPOTables();
	}

	@Override
	public int executeDML(String sql, Object[] params) {
		int falg = 0;
		Connection conn = DBManager.getConn();
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(sql);
			JDBCUtils.handleParams(pstm, params); // 设置参数
			System.out.println("SQL>>>>>>>>> " + pstm); // 观察pstm对象中执行的SQL
			falg = pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBManager.close(pstm, conn);
		}
		return falg;
	}

	@Override
	public int insert(Object obj) {
		Class<?> clazz = obj.getClass();
		List<Object> params = new ArrayList<>();
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableInfo.getTname() + "(");
		int countNotNullField = 0; // 不为null的属性数量
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				String fieldName = field.getName();
				Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
				if (fieldValue != null) {
					countNotNullField++;
					sqlBuilder.append(fieldName + ",");
					params.add(fieldValue);
				}
			}
		}
		sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')'); // 将最后一个","换成")"，并且char型只能用单引号
		sqlBuilder.append(" VALUES (");
		if (countNotNullField > 0) {
			for (int i = 0; i < countNotNullField; i++) {
				sqlBuilder.append("?,");
			}
		}
		sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')'); // 将最后一个","换成")"，并且char型只能用单引号
		sqlBuilder.append(";");	// 在SQL结尾追加分号结束
		return executeDML(sqlBuilder.toString(), params.toArray()); // 执行SQL
	}

//	private int delete(Class<?> clazz, Object id) {
//		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);	// 取得表信息
//		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();	// 取得主键字段信息
//		String sql = "DELETE FROM " + tableInfo.getTname() + " WHERE " + onlyPriKey.getName() + " = ?";
//		// 执行sql【SQL与主键属性内容】
//		executeDML(sql, new Object[] {id});
//	}

	@Override
	public int delete(Object obj) {
		// 通过反射获取对象中的属性内容，并调用重载的删除方法执行删除。
		TableInfo tableInfo = TableContext.poClassTableMap.get(obj.getClass());
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey(); // 取得主键字段信息
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj); // 取得主键属性内容
//		delete(obj.getClass(), priKeyValue);
		String sql = "DELETE FROM " + tableInfo.getTname() + " WHERE " + onlyPriKey.getName() + " = ? ;";
		// 执行sql【SQL与主键属性内容】
		return executeDML(sql, new Object[] { priKeyValue });

	}

	@Override
	public int update(Object obj, String[] fieldNames) {
		Class<?> clazz = obj.getClass();
		List<Object> params = new ArrayList<>();
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo priKey = tableInfo.getOnlyPriKey();	// 取得主键
		StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableInfo.getTname() + " SET ");
		if (fieldNames != null && fieldNames.length > 0) {
			for (String fieldName : fieldNames) {
				Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
				params.add(fieldValue);
				sqlBuilder.append(fieldName + "=?,");
			}
		}
		sqlBuilder.setCharAt(sqlBuilder.length()-1, ' ');
		sqlBuilder.append(" WHERE ");
		sqlBuilder.append(priKey.getName()+"=? ");
		sqlBuilder.append(";");	// 在SQL结尾追加分号结束
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));	// 封装主键
		return executeDML(sqlBuilder.toString(), params.toArray());
	}

	@Override
	public List<Object> queryRows(String sql, Class<?> clazz, Object[] params) {
		List<Object> list = null;
		Connection conn = DBManager.getConn();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			pstm = conn.prepareStatement(sql);
			JDBCUtils.handleParams(pstm, params); 									// 为JDBC操作设置
			System.out.println(pstm); 												// 观察SQL输出
			rs = pstm.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData(); 							// 取得RS中的所有列名与类型信息。
			list = new ArrayList<>();												// 如果List为空则创建对象
			
			while (rs.next()) {
				Object rowObject = clazz.newInstance(); 							// 调用无参构造实例化行对象
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					String columnName = metaData.getColumnLabel(i + 1); 			// 依次取表名。从1开始
					Object columnValue = rs.getObject(i + 1); 			// 依次取需要的表名中的数据。从1开始
					ReflectUtils.invokeSet(rowObject, columnName, columnValue); 	// 反射设置表名与内容
				}
				list.add(rowObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(rs, pstm, conn);
		}
		return list;
	}

	@Override
	public Object queryUniqueRow(String sql, Class<?> clazz, Object[] params) {
		List<Object> list =  queryRows(sql, clazz, params);
		return (list != null && list.size() > 0) ? null : list.get(0);
	}

	@Override
	public Object queryValue(String sql, Object[] params) {
		Connection conn = DBManager.getConn();
		Object value = null; // 取得查询结果的对象
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		try {
			pstm = conn.prepareStatement(sql);
			JDBCUtils.handleParams(pstm, params);
			System.out.println(pstm); 		// 观察SQL输出
			rs = pstm.executeQuery();
			while (rs.next()) {
				value = rs.getObject(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(rs, pstm, conn);
		}
		
		return value;
	}

	@Override
	public Number queryNumber(String sql, Object[] params) {
		return (Number) queryValue(sql, params);
	}

	
	
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	// 测试查询结果
	public static void main(String[] args) throws Exception {
		int rows = 0;
		
//		rows = testDelete();		// 测试删除
//		rows = testInster();		// 测试添加
//		rows = testUpdate();		// 测试修改
//		System.out.println(rows > 0 ? "Success" : "Failure");

		// 测试查询[多行]
		@SuppressWarnings("unchecked")
		List<Emp> emps = (List<Emp>) testQueryMultiple();
		printConllecton(emps);
		
	}
	
	private static int testadd(Emp emp){
		int rows = 0;

		try {
			Query query = new MySqlQuery();
			rows = query.insert(emp);
			System.out.println(rows > 0 ? "Success" : "Failure");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}

	private static void printConllecton(List<Emp> list) {
		list.forEach(emp -> {
			System.out.println(
					emp.getId() + " - " +
					emp.getEmpname() + " - " +
					emp.getAge() + " - " +
					emp.getBonus() + " - " +
					emp.getDeptid() + " - " +
					emp.getSalary() + " - " +
					emp.getBirthday()
			);
		});
	}
	
	private static int testDelete() {
		Emp emp = new Emp();
		emp.setId(1L);
		Query query = new MySqlQuery();
		return query.delete(emp);
	}
	
	
	private static int testInster() {
		int rows = 0;
		Emp emp = null;
		long datetimelong;
		try {
			emp = new Emp();
			emp.setEmpname("fucker2");
			emp.setAge(25);
			datetimelong = new SimpleDateFormat("yyyy-MM-dd").parse("1994-02-05").getTime();
			emp.setBirthday(new java.sql.Date(datetimelong));
			emp.setBonus(100.00);
			emp.setDeptid(1L);
			emp.setSalary(1000.00);
			Query query = new MySqlQuery();
			rows = query.insert(emp);
			System.out.println(rows > 0 ? "Success" : "Failure");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}
	
	private static int testUpdate() {
		Emp emp = new Emp();
		emp.setEmpname("fuckyou");
		emp.setBonus(500.25);
		emp.setId(3L);
		Query query = new MySqlQuery();
		return query.update(emp, new String[] {"empname","bonus"});
	}
	
	private static List<?> testQueryMultiple() {
		String sql1 = "SELECT id, empname, salary, birthday, age, deptid, bonus FROM emp";
		return new MySqlQuery().queryRows(sql1, Emp.class, null);
	}

}
