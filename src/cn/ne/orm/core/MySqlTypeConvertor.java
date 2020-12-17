package cn.ne.orm.core;

/**
 * mysql数据类型和java数据类型的转换
 * @author NightEagle
 * @version 0.0.1
 */
public class MySqlTypeConvertor implements TypeConvertor{

	/***
	 * 将varchar转String
	 * @param columnType	要转换的名称
	 */
	@Override
	public String databaseType2JavaType(String columnType) {
		if("varchar".equalsIgnoreCase(columnType)||"char".equalsIgnoreCase(columnType)){
			return "String";
		}else if("int".equalsIgnoreCase(columnType)
				||"tinyint".equalsIgnoreCase(columnType)
				||"smallint".equalsIgnoreCase(columnType)
				||"integer".equalsIgnoreCase(columnType)
				){
			return "Integer";
		}else if("bigint".equalsIgnoreCase(columnType)){
			return "Long";
		}else if("double".equalsIgnoreCase(columnType)||"float".equalsIgnoreCase(columnType)){
			return "Double";
		}else if("clob".equalsIgnoreCase(columnType)){
			return "java.sql.CLob";
		}else if("blob".equalsIgnoreCase(columnType)){
			return "java.sql.BLob";
		}else if("date".equalsIgnoreCase(columnType)){
			return "java.sql.Date";
		}else if("time".equalsIgnoreCase(columnType)){
			return "java.sql.Time";
		}else if("timestamp".equalsIgnoreCase(columnType)){
			return "java.sql.Timestamp";
		}
		
		return null;
	}

	/**
	 * 暂不实现
	 */
	@Override
	public String javaType2DatabaseType(String javaDataType) {
		// TODO Auto-generated method stub
		return null;
	}

}
