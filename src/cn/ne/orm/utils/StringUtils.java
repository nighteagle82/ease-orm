package cn.ne.orm.utils;

/**
 * 字符串常用的操作
 * @author NightEagle
 * @version 0.0.1
 */
public class StringUtils {
	/**
	 * 将目标字符串首字母变为大写
	 * @param str 目标字符串
	 * @return 首字母变为大写的字符串
	 */
	public static String firstChar2UpperCase(String str) {
		return str.toUpperCase().substring(0, 1) + str.substring(1);
	}
}
