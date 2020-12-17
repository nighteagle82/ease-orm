package cn.ne.dydb;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDB {


    public static void main(String[] args) {
//        addSingle();
        addMultiple();
    }


    /**
     * 添加单个字段
     */
    private static void addSingle() {
        TablePros tablePros = new TablePros();
        tablePros.setDbName("huser");
        tablePros.setTbName("news2");
        tablePros.setColumsName("haha2");

        if (checkColumsSingle(tablePros)) {
            System.out.println("字段已存在");
        } else {
            tablePros.setColumsType("VARCHAR");
            tablePros.setColumsLength(50);
            boolean result = addColumsSingle(tablePros);
            System.out.println(result ? "成功" : "失败");
        }
    }

    private static boolean checkColumsSingle(TablePros tablePros) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE ")
                .append("TABLE_SCHEMA='").append(tablePros.getDbName()).append("' ")
                .append("AND table_name=' ").append(tablePros.getTbName())
                .append("' AND ").append("COLUMN_NAME='").append(tablePros.getColumsName()).append("';");
        System.out.println(sb);
        return executeCheckdSQL(sb.toString()).size() > 0;
    }

    private static boolean addColumsSingle(TablePros tablePros) {
        String notNull = tablePros.isNotNull() ? "NOT NULL" : "NULL"; // true设为not null，false设为null

        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ").append(tablePros.getTbName())
                .append(" ADD COLUMN ").append(tablePros.getColumsName())
                .append(" ").append(tablePros.getColumsType()).append("(")
                .append(tablePros.getColumsLength()).append(") ").append(notNull)
                .append(" ;");

        System.out.println(sb);
        return executeAddSQL(sb.toString());
    }


    /**
     * 添加多个字段
     */
    private static void addMultiple() {
        TablePros tablePros = new TablePros();
        tablePros.setDbName("huser");
        tablePros.setTbName("news2");
        tablePros.setColumsNames(new String[]{"newname", "dcount", "settime"});
        List<String> result = checkColumsMultiple(tablePros);
        if (result.size() > 0) {
            System.out.println("不能添加！数据表【" + tablePros.getTbName() + "】已存在如下字段：" + Arrays.toString(result.toArray()));
        } else {
            tablePros.setColumsTypes(new String[]{"VARCHAR", "INT", "DATETIME"});
            tablePros.setColumslengths(new int[]{50, 12, 0});
            boolean result2 = addColumsMultiple(tablePros);
            System.out.println(result2 ? "成功" : "失败");
        }

    }

    private static List<String> checkColumsMultiple(TablePros tablePros) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE ")
                .append("TABLE_SCHEMA='").append(tablePros.getDbName()).append("' ")
                .append("AND table_name=' ").append(tablePros.getTbName());

        if (tablePros.getColumsNames().length > 0) {
            for (String name : tablePros.getColumsNames()) {
                sb.append("' OR ").append("COLUMN_NAME='").append(name);
            }
        }
        sb.append("';");
        List<String> temp = executeCheckdSQL(sb.toString()); // 获取查询到的字段
        List<String> source = new ArrayList<>(Arrays.asList(tablePros.getColumsNames()));
        source.retainAll(temp); // 从源中排除表中已有的字段。

        System.out.println(sb);
        return source;
    }

    private static boolean addColumsMultiple(TablePros tablePros) {
        String notNull = tablePros.isNotNull() ? "NOT NULL" : "NULL"; // true设为not null，false设为null
        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ").append(tablePros.getTbName());
        if (tablePros.getColumsNames().length > 0) {
            for (int i = 0; i < tablePros.getColumsNames().length; i++) {
                sb.append(" ADD COLUMN ").append(tablePros.getColumsNames()[i])
                        .append(" ").append(tablePros.getColumsTypes().length > 0 ? tablePros.getColumsTypes()[i] : " ");
                if (tablePros.getColumsTypes()[i].equalsIgnoreCase("DATETIME") ||
                        tablePros.getColumsTypes()[i].equalsIgnoreCase("DATE")) {
                    sb.append(" ").append(notNull).append(",");
                } else {
                    sb.append("(").append(tablePros.getColumslengths().length > 0 ? tablePros.getColumslengths()[i] : " ").append(") ")
                            .append(notNull).append(",");
                }
            }
        }

        sb.delete(sb.length() - 1, sb.length()).append(" ;");   // 移聊最后一个”，“

        System.out.println(sb);
        return executeAddSQL(sb.toString());
    }



    // 执行查询
    private static List<String> executeCheckdSQL(String sql) {
        List<String> columnList = new ArrayList<>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = DBConn.getConn().prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                columnList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(pstm, rs);
        }
        return columnList;
    }

    // 执行添加
    private static boolean executeAddSQL(String sql) {
        PreparedStatement pstm = null;
        try {
            pstm = DBConn.getConn().prepareStatement(sql);
            pstm.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(pstm);
        }
    }



    // 关闭连接，重载+1
    private static void close(PreparedStatement pstm) {
        if (pstm != null) {
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBConn.close();
    }

    // 关闭连接，重载+2
    private static void close(PreparedStatement pstm, ResultSet rs) {
        if (pstm != null) {
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBConn.close();
    }


}
