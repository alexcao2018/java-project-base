package com.project.base.mybatis.plugin;

import com.project.base.mybatis.annotation.ByCriteria;
import com.project.base.mybatis.criterion.Criteria;
import com.project.base.model.PageInfo;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class CriteriaInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String id = mappedStatement.getId();

        Annotation byCriteriaAnnotation = null;
        Method[] methodArray = Class.forName(id.substring(0, id.lastIndexOf("."))).getDeclaredMethods();
        for (int i = 0; i < methodArray.length; i++) {
            Method method = methodArray[i];
            if (method.getName().equals(id.substring(id.lastIndexOf(".") + 1))) {
                byCriteriaAnnotation = method.getAnnotation(ByCriteria.class);
                break;
            }
        }
        /*   处理mapper文件中以byCriteria 结尾的方法
         -------------------------------------------------------------- */
        if (byCriteriaAnnotation != null) {
            BoundSql boundSql = statementHandler.getBoundSql();
            Map<String, Object> params = (Map<String, Object>) boundSql.getParameterObject();
            Criteria criteria = (Criteria) params.get("criteria");
            PageInfo pageInfo = (PageInfo) params.get("pageInfo");
            String sql = boundSql.getSql();

            if (pageInfo != null) {
                criteria.setFirstResult(pageInfo.getPageNum() * pageInfo.getPageSize());
                criteria.setMaxResult(pageInfo.getPageSize());
            }

            String criteriaSql = " select " + criteria.getProjectionSqlString() + " from ( " + sql + ") __t__ " + criteria.repalceParameter2PlaceHolder();
            for (String parameter : criteria.getParameterCollection()) {
                ParameterMapping parameterMapping = new ParameterMapping.Builder(mappedStatement.getConfiguration(), parameter.substring(2, parameter.length() - 1), Object.class).build();
                boundSql.getParameterMappings().add(parameterMapping);
            }
            metaObject.setValue("delegate.boundSql.sql", criteriaSql);

            if (pageInfo != null) {
                String countSql = " select count(*) from ( " + sql + ") __t__ " + criteria.repalceParameter2PlaceHolder();
                pageInfo.setTotalCount(selectCount(invocation, metaObject, countSql));
            }
        }

        if (id.endsWith("paginateListBy")) {
            BoundSql boundSql = statementHandler.getBoundSql();
            Map<String, Object> params = (Map<String, Object>) boundSql.getParameterObject();
            PageInfo pageInfo = (PageInfo) params.get("pageInfo");
            String sql = boundSql.getSql();
            String countSql = sql.replaceFirst("\\*", "count(*)");
            countSql = countSql.substring(0,countSql.indexOf("limit"));
            pageInfo.setTotalCount(selectCount(invocation, metaObject, countSql));
        }

        return invocation.proceed();
    }

    private long selectCount(Invocation invocation, MetaObject metaObject, String sql) throws SQLException {
        long count = 0;
        Connection connection = (Connection) invocation.getArgs()[0];
        PreparedStatement countStatement = connection.prepareStatement(sql);
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
        parameterHandler.setParameters(countStatement);
        ResultSet rs = countStatement.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
            rs.close();
        }
        return count;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String dialect = properties.getProperty("dialect");
        System.out.println("dialect=" + dialect);
    }
}
