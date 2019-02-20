package com.project.base.mybatis.base;

import com.project.base.mybatis.criterion.Criteria;
import com.project.base.mybatis.mapping.ColumnInfo;
import com.project.base.mybatis.mapping.DBInfoHelper;
import com.project.base.mybatis.mapping.TableInfo;
import com.project.base.model.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Map;

public class BaseSelectProvider {

    /* select operation
    ------------------------------------------------------------------------
     */
    public static String getById(MappedStatement mappedStatement, Class<?> mapperClass, Class<?> modelClass, Class<?> primaryFieldClass, ResultMap resultMap) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "select * from {0} where {1}=#'{id}'";
        TableInfo tableInfo = DBInfoHelper.getTableInfo(modelClass);
        return MessageFormat.format(sqlFormat, tableInfo.getName(), tableInfo.getPrimaryKeyColumn().getName());
    }

    public String getFirst(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "select {0} from {1} {2} limit 1";
        Criteria criteria = (Criteria) map.get("criteria");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(criteria.getEntityClazz());
        return MessageFormat.format(sqlFormat, criteria.getProjectionSqlString(), tableInfo.getName(), criteria.toSqlString());

    }

    public String listBy(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "select {0} from {1} {2}";
        Criteria criteria = (Criteria) map.get("criteria");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(criteria.getEntityClazz());
        return MessageFormat.format(sqlFormat, criteria.getProjectionSqlString(), tableInfo.getName(), criteria.toSqlString());
    }

    public String paginateListBy(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "select {0} from {1} {2}";
        Criteria criteria = (Criteria) map.get("criteria");
        PageInfo pageInfo = (PageInfo) map.get("pageInfo");
        criteria.setFirstResult(pageInfo.getPageNum() * pageInfo.getPageSize());
        criteria.setMaxResult(pageInfo.getPageSize());

        TableInfo tableInfo = DBInfoHelper.getTableInfo(criteria.getEntityClazz());
        return MessageFormat.format(sqlFormat, criteria.getProjectionSqlString(), tableInfo.getName(), criteria.toSqlString());
    }

    public String countBy(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "select count(*) from {0} {1}";
        Criteria criteria = (Criteria) map.get("criteria");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(criteria.getEntityClazz());

        return MessageFormat.format(sqlFormat, tableInfo.getName(), criteria.toSqlString());
    }

    public static String count(MappedStatement mappedStatement, Class<?> mapperClass, Class<?> modelClass, Class<?> primaryFieldClass, ResultMap resultMap) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "select count(*) from {0}";
        TableInfo tableInfo = DBInfoHelper.getTableInfo(modelClass);
        return MessageFormat.format(sqlFormat, tableInfo.getName());
    }

    /* delete operation
    ------------------------------------------------------------------------
     */

    public static String deleteById(MappedStatement mappedStatement, Class<?> mapperClass, Class<?> modelClass, Class<?> primaryFieldClass, ResultMap resultMap) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "delete from {0} where {1}=#'{id}'";
        TableInfo tableInfo = DBInfoHelper.getTableInfo(modelClass);
        return MessageFormat.format(sqlFormat, tableInfo.getName(), tableInfo.getPrimaryKeyColumn().getName());
    }

    public static String deleteBy(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "delete from {0} {1}";
        Criteria criteria = (Criteria) map.get("criteria");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(criteria.getEntityClazz());

        return MessageFormat.format(sqlFormat, tableInfo.getName(), criteria.toSqlString());
    }

    /* insert operation
   ------------------------------------------------------------------------
    */
    public String insert(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "insert into {0} ({1}) values ({2}) ";
        Object entity = map.get("entity");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(entity.getClass());
        StringBuilder sbColumn = new StringBuilder();
        StringBuilder sbColumnValue = new StringBuilder();
        for (int i = 0; i < tableInfo.getColumnCollection().size(); i++) {
            ColumnInfo columnInfo = tableInfo.getColumnCollection().get(i);
            if (columnInfo.isGeneratedValue())
                continue;

            sbColumn.append(columnInfo.getName() + ",");
            sbColumnValue.append("#{entity." + columnInfo.getMappedName() + "},");
        }

        return MessageFormat.format(sqlFormat, tableInfo.getName(), StringUtils.chop(sbColumn.toString()), StringUtils.chop(sbColumnValue.toString()));
    }

    public String insertBatch(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "insert into {0} ({1}) values {2} ";
        Object[] entityCollection = (Object[]) map.get("entityCollection");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(entityCollection[0].getClass());
        StringBuilder sbColumn = new StringBuilder();
        StringBuilder sbColumnValueCollection = new StringBuilder();
        for (int i = 0; i < tableInfo.getColumnCollection().size(); i++) {
            ColumnInfo columnInfo = tableInfo.getColumnCollection().get(i);
            if (columnInfo.isGeneratedValue())
                continue;
            sbColumn.append(columnInfo.getName() + ",");
        }

        for (int i = 0; i < entityCollection.length; i++) {
            StringBuilder sbColumnValue = new StringBuilder();
            for (int j = 0; j < tableInfo.getColumnCollection().size(); j++) {
                ColumnInfo columnInfo = tableInfo.getColumnCollection().get(j);
                if (columnInfo.isGeneratedValue())
                    continue;
                sbColumnValue.append("#{entityCollection[" + i + "]." + columnInfo.getMappedName() + "},");
            }
            sbColumnValueCollection.append("(" + StringUtils.chop(sbColumnValue.toString()) + "),");
        }

        return MessageFormat.format(sqlFormat, tableInfo.getName(), StringUtils.chop(sbColumn.toString()), StringUtils.chop(sbColumnValueCollection.toString()));
    }

    /* update operation
  ------------------------------------------------------------------------
   */
    public String update(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "update {0} set {1} where {2} = {3} ";
        Object entity = map.get("entity");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(entity.getClass());
        StringBuilder sbColumn = new StringBuilder();
        for (int i = 0; i < tableInfo.getColumnCollection().size(); i++) {
            ColumnInfo columnInfo = tableInfo.getColumnCollection().get(i);
            if (columnInfo.isPrimaryKey())
                continue;
            sbColumn.append(columnInfo.getName() + " = " + "#{entity." + columnInfo.getMappedName() + "},");
        }

        return MessageFormat.format(sqlFormat, tableInfo.getName(), StringUtils.chop(sbColumn.toString()), tableInfo.getPrimaryKeyColumn().getName(), "#{entity." + tableInfo.getPrimaryKeyColumn().getMappedName() + "}");
    }

    public String updateBy(Map<String, Object> map) throws NoSuchFieldException, IllegalAccessException {
        String sqlFormat = "update {0} set {1} {2} ";
        Object entity = map.get("entity");
        Criteria criteria = (Criteria) map.get("criteria");
        TableInfo tableInfo = DBInfoHelper.getTableInfo(entity.getClass());
        StringBuilder sbColumn = new StringBuilder();
        for (int i = 0; i < tableInfo.getColumnCollection().size(); i++) {
            ColumnInfo columnInfo = tableInfo.getColumnCollection().get(i);
            if (columnInfo.isPrimaryKey())
                continue;
            Field field = entity.getClass().getDeclaredField(columnInfo.getMappedName());
            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            if (fieldValue == null)
                continue;
            sbColumn.append(columnInfo.getName() + " = " + "#{entity." + columnInfo.getMappedName() + "},");
        }

        return MessageFormat.format(sqlFormat, tableInfo.getName(), StringUtils.chop(sbColumn.toString()), criteria.toSqlString());
    }

}
