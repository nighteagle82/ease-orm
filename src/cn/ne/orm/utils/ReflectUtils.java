package cn.ne.orm.utils;

import java.lang.reflect.Method;

/**
 * 反射常用的操作
 * 
 * @author NightEagle
 * @version 0.0.1
 */
public class ReflectUtils {
	
	/**
	 * 调用对应属性fieldName的get方法
	 * @param fieldName	属性名称
	 * @param obj		属性对象
	 * @return			属性get方法中的值
	 * @exception		反射异常
	 */
	public static Object invokeGet(String fieldName, Object obj) {
		try {
			Class<?> clazz = obj.getClass();
			Method method = clazz.getDeclaredMethod("get" + StringUtils.firstChar2UpperCase(fieldName));
			return method.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 调用对应属性fieldName的set方法
	 * @param object		属性对象
	 * @param columName		属性名称
	 * @param columnValue	属性内容
	 */
	public static void invokeSet(Object object, String columnName, Object columnValue) {
		try {
			Method method = object.getClass().getDeclaredMethod("set"+StringUtils.firstChar2UpperCase(columnName), 
					columnValue.getClass());
			method.invoke(object, columnValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
