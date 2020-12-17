package cn.ne.dydb;

import java.util.Locale;
import java.util.ResourceBundle;

public class DBResource {

    private DBResource() {}

    public static String getProperty(String key){
        // 资源文件存放位置： resources根目录。使用ResourceBundle无需后缀。
        ResourceBundle bundle = ResourceBundle.getBundle("database", Locale.getDefault());
        return bundle.getString(key);
    }

}
