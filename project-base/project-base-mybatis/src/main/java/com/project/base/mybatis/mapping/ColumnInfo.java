package com.project.base.mybatis.mapping;

public class ColumnInfo {
    private String name;
    private String mappedName;
    private boolean isGeneratedValue;
    private boolean isPrimaryKey;
    private String dbType;
    private String javaType;

    public ColumnInfo(String name, String mappedName) {
        this.name = name;
        this.mappedName = mappedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMappedName() {
        return mappedName;
    }

    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    public boolean isGeneratedValue() {
        return isGeneratedValue;
    }

    public void setGeneratedValue(boolean generatedValue) {
        isGeneratedValue = generatedValue;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
}
