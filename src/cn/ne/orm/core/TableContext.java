package cn.ne.orm.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.ne.orm.bean.ColumnInfo;
import cn.ne.orm.bean.TableInfo;
import cn.ne.orm.utils.JavaFileUtils;
import cn.ne.orm.utils.StringUtils;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构。
 * 
 * @author NightEagle
 * @version 0.0.3
 */
public class TableContext {

	// 表名为key，表信息对象为value
	public static Map<String, TableInfo> tables = new HashMap<>();

	// 将pojo的class对象和表信息对象关联起来，便于重用！
	public static Map<Class<?>, TableInfo> poClassTableMap = new HashMap<>();

	// 构造私有化，禁止实例化
	private TableContext() {
	}

	static {
		try {
			// 初始化获得表的信息
			Connection conn = DBManager.getConn();
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet tableResultSet = metaData.getTables(null, "%", "%", new String[] { "TABLE" }); // 创建返回的数据表结果集

			while (tableResultSet.next()) {
				String tableName = (String) tableResultSet.getObject("TABLE_NAME"); // 取得表名
				TableInfo tableInfo = new TableInfo(tableName, new ArrayList<ColumnInfo>(),
						new HashMap<String, ColumnInfo>());
				tables.put(tableName, tableInfo); // 设置表信息【虚拟表】

				ResultSet rs1 = metaData.getColumns(null, "%", tableName, "%"); // 取得表中所有字段
				while (rs1.next()) {
					ColumnInfo columnInfo = new ColumnInfo(rs1.getString("COLUMN_NAME"), rs1.getString("TYPE_NAME"), 0);
					tableInfo.getColumns().put(rs1.getString("COLUMN_NAME"), columnInfo);
				}

				ResultSet rs2 = metaData.getPrimaryKeys(null, "%", tableName); // 取得表中主键
				while (rs2.next()) {
					ColumnInfo columnInfo2 = (ColumnInfo) tableInfo.getColumns().get(rs2.getObject("COLUMN_NAME"));
					columnInfo2.setKeyType(1); // 设置主键类型
					tableInfo.getPriKeys().add(columnInfo2);
				}

				if (tableInfo.getPriKeys().size() > 0) { // 取唯一主键。如果是联合主键。则为空。
					tableInfo.setOnlyPriKey(tableInfo.getPriKeys().get(0)); // 从第0个位置找到主键。
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 更新类结构
		updateJavaPOFile();

		// 加载pojo包下面所有的类。
		//loadPOTables();
	}

	// 根据表结构，更新配置的po包下面的java类 实现了从表结构转化到类结构
	private static void updateJavaPOFile() {
		Map<String, TableInfo> map = new HashMap<>();
		for (TableInfo info : map.values()) {
			JavaFileUtils.createJavaPOFile(info, new MySqlTypeConvertor());
		}
	}

	// 加载po包下面的类
	public static void loadPOTables() {
		if (tables != null && tables.values().size() > 0) {
			for (TableInfo info : tables.values()) {
				String classPath = DBManager.getConf().getPoPackage() + "."
						+ StringUtils.firstChar2UpperCase(info.getTname());
				try {
					Class<?> clazz = Class.forName(classPath);
					System.out.println(clazz);	// 观察类实例是否生成。
					poClassTableMap.put(clazz, info);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		}
	}


	// 测试
//	public static void main(String[] args) {
//		 Map<String,TableInfo>  tables = TableContext.tables;
//		 if (tables != null && tables.values().size() > 0) {
//			 System.out.println(tables);
//		}
//	}

}
