package cn.ne.dydb;

import java.io.Serializable;
import java.util.Arrays;

public class TablePros implements Serializable {
    private String dbName;
    private String tbName;
    private String columsName;
    private String[] columsNames;
    private String columsType;
    private String[] columsTypes;
    private int columsLength;
    private int[] columslengths;
    private boolean isNotNull;
    private boolean[] isNotNulls;

    public TablePros() {
    }

    public TablePros(String dbName, String tbName, String columsName, String columsType,
                     int columsLength, boolean isNotNull) {
        this.dbName = dbName;
        this.tbName = tbName;
        this.columsName = columsName;
        this.columsType = columsType;
        this.columsLength = columsLength;
        this.isNotNull = isNotNull;
    }

    public TablePros(String dbName, String tbName, String columsName, String[] columsNames, String columsType,
                     String[] columsTypes, int columsLength, int[] columslengths,
                     boolean isNotNull, boolean[] isNotNulls) {
        this.dbName = dbName;
        this.tbName = tbName;
        this.columsName = columsName;
        this.columsNames = columsNames;
        this.columsType = columsType;
        this.columsTypes = columsTypes;
        this.columsLength = columsLength;
        this.columslengths = columslengths;
        this.isNotNull = isNotNull;
        this.isNotNulls = isNotNulls;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTbName() {
        return tbName;
    }

    public void setTbName(String tbName) {
        this.tbName = tbName;
    }

    public String getColumsName() {
        return columsName;
    }

    public void setColumsName(String columsName) {
        this.columsName = columsName;
    }

    public String[] getColumsNames() {
        return columsNames;
    }

    public void setColumsNames(String[] columsNames) {
        this.columsNames = columsNames;
    }

    public String getColumsType() {
        return columsType;
    }

    public void setColumsType(String columsType) {
        this.columsType = columsType;
    }

    public String[] getColumsTypes() {
        return columsTypes;
    }

    public void setColumsTypes(String[] columsTypes) {
        this.columsTypes = columsTypes;
    }

    public int getColumsLength() {
        return columsLength;
    }

    public void setColumsLength(int columsLength) {
        this.columsLength = columsLength;
    }

    public int[] getColumslengths() {
        return columslengths;
    }

    public void setColumslengths(int[] columslengths) {
        this.columslengths = columslengths;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    public boolean[] getIsNotNulls() {
        return isNotNulls;
    }

    public void setIsNotNulls(boolean[] isNotNulls) {
        this.isNotNulls = isNotNulls;
    }

    @Override
    public String toString() {
        return "TablePros{" +
                "dbName='" + dbName + '\'' +
                ", tbName='" + tbName + '\'' +
                ", columsName='" + columsName + '\'' +
                ", columsNames=" + Arrays.toString(columsNames) +
                ", columsType='" + columsType + '\'' +
                ", columsTypes=" + Arrays.toString(columsTypes) +
                ", columsLength=" + columsLength +
                ", columslengths=" + Arrays.toString(columslengths) +
                ", isNotNull=" + isNotNull +
                ", isNotNulls=" + Arrays.toString(isNotNulls) +
                '}';
    }
}
