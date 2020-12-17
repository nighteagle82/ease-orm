package cn.ne.orm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ne.orm.bean.ColumnInfo;
import cn.ne.orm.bean.JavaFieldGetSet;
import cn.ne.orm.bean.TableInfo;
import cn.ne.orm.core.DBManager;
import cn.ne.orm.core.MySqlTypeConvertor;
import cn.ne.orm.core.TableContext;
import cn.ne.orm.core.TypeConvertor;

/**
 * 生成Java文件(源代码)常用的操作
 * @author NightEagle
 * @version 0.0.3
 */
public class JavaFileUtils {
	
	/**
	 * 根据字段信息生成java属性信息。如：varchar username-->private String username;以及相应的set和get方法源码
	 * @param column 字段信息
	 * @param convertor 类型转化器
	 * @return java属性和set/get方法源码
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {
		JavaFieldGetSet javaGetSet = new JavaFieldGetSet();
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		javaGetSet.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		
		//生成Get方法的源代码
		//例: public void getUsername(String username){this.username=username;}
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName())+"(){\n");
		getSrc.append("\t\treturn "+column.getName()+";\n");
		getSrc.append("\t}\n");
		javaGetSet.setGetInfo(getSrc.toString());
		
		//生成set方法的源代码
		//例： public void setUsername(String username){this.username=username;}
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstChar2UpperCase(column.getName())+"(");
		setSrc.append(javaFieldType+" "+column.getName()+"){\n");
		setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
		setSrc.append("\t}\n");
		javaGetSet.setSetInfo(setSrc.toString());
		
		return javaGetSet;
	}
	
	/**
	 * 根据表信息生成java类的源代码
	 * @param tableInfo 表信息
	 * @param convertor 数据类型转化器 
	 * @return java类的源代码
	 */
	public static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
		Map<String, ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<>();
		
		//依次设置属性值
		for (ColumnInfo ci : columns.values()) {
			javaFields.add(createFieldGetSetSRC(ci, convertor));
		}
		
		StringBuilder srcBuilder = new StringBuilder();
		//生成package语句
		srcBuilder.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		//生成import语句
		srcBuilder.append("import java.sql.*;\n");
		srcBuilder.append("import java.util.*;\n\n");
		//生成类声明语句
		srcBuilder.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getTname())+" {\n\n");
		
		//生成属性列表
		for (JavaFieldGetSet f : javaFields) {
			srcBuilder.append(f.getFieldInfo());
		}
		srcBuilder.append("\n\n");	//换行
		
		//生成get方法列表
		for (JavaFieldGetSet f : javaFields) {
			srcBuilder.append(f.getGetInfo());
		}
		//生成set方法列表
		for (JavaFieldGetSet f : javaFields) {
			srcBuilder.append(f.getSetInfo());
		}
		
		//结束换行。
		srcBuilder.append("}\n");		
		return srcBuilder.toString();
	}
	
	
	/**
	 * 向工程目录写入生成的POJO文件
	 * @param tableInfo		表信息
	 * @param convertor		类型转换器接口
	 */
	public static void createJavaPOFile(TableInfo tableInfo,TypeConvertor convertor){
		String src = createJavaSrc(tableInfo,convertor);
		
		String srcPath = DBManager.getConf().getSrcPath()+"\\";
		String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "/");
		File f = new File(srcPath+packagePath);
		
		if(!f.exists()){  //如果指定目录不存在，则建立。
			f.mkdirs();
		}
		
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java"));
			bw.write(src);
			System.out.println("建立表"+tableInfo.getTname()+
					"对应的java类："+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if (bw != null) { bw.close(); }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//生成所有POJO对象。【先调这个方法。】
	public static void instancePOJO() {
		Map<String,TableInfo> map = TableContext.tables;
		if (map != null && map.values().size() > 0) {
			for(TableInfo t:map.values()){
				createJavaPOFile(t,new MySqlTypeConvertor());
			}
		}
	}
	
	
	// 测试生成
	public static void main(String[] args) {
		instancePOJO();
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
