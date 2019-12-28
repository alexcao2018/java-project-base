package com.project.base.mybatis.mapping;

import com.google.common.base.CaseFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TableInfo {
    private String name;
    private Class mappedClazz;
    private List<ColumnInfo> columnCollection;
    private ColumnInfo primaryKeyColumn;


    public TableInfo(Class clazz) throws IllegalAccessException {
        this.mappedClazz = clazz;
        this.columnCollection = new ArrayList<>();
        this.init();
    }

    private void init() throws IllegalAccessException {

        /* 构建table 信息
        --------------------------------------------------
        * */
        Table tableAnnotation = (Table) this.mappedClazz.getAnnotation(Table.class);
        this.name = tableAnnotation.name();

        /* 构建column 信息
        --------------------------------------------------
        * */
        Field[] declaredFieldCollection = this.mappedClazz.getDeclaredFields();
        for (int i = 0; i < declaredFieldCollection.length; i++) {
            Field field = declaredFieldCollection[i];
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {

                String propertyName = field.getName();
                String columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propertyName);
                ColumnInfo columnInfo = new ColumnInfo(columnName, propertyName);

                Column columnAnnotation = field.getAnnotation(Column.class);
                columnInfo.setName(columnAnnotation == null ? columnName : columnAnnotation.name());

                Id idAnnotation = field.getAnnotation(Id.class);
                if (idAnnotation != null) {
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    columnInfo.setPrimaryKey(true);
                    columnInfo.setGeneratedValue(generatedValue != null);
                    this.primaryKeyColumn = columnInfo;
                }
                this.columnCollection.add(columnInfo);
            }
        }
    }

    public List<ColumnInfo> getColumnCollection() {
        return columnCollection;
    }

    public ColumnInfo getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public String getName() {
        return name;
    }
}
