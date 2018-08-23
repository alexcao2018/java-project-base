package com.project.base.mybatis.base;

import com.project.base.model.IEntity;
import com.project.base.model.PageInfo;
import com.project.base.mybatis.criterion.Criteria;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IBaseMapper<T extends IEntity<T, PK>, PK> {

    /* select operation
    -----------------------------------------------------
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "getById")
    T getById(@Param("id") PK id);

    @SelectProvider(type = BaseSelectProvider.class, method = "listBy")
    List<T> listBy(@Param("criteria") Criteria criteria);

    @SelectProvider(type = BaseSelectProvider.class, method = "countBy")
    Long countBy(@Param("criteria") Criteria criteria);

    @SelectProvider(type = BaseSelectProvider.class, method = "count")
    Long count();

    @SelectProvider(type = BaseSelectProvider.class, method = "paginateListBy")
    List<T> paginateListBy(@Param("criteria") Criteria criteria,@Param("pageInfo") PageInfo pageInfo);


    /* delete operation
    -----------------------------------------------------
     */
    @DeleteProvider(type = BaseSelectProvider.class, method = "deleteById")
    int deleteById(@Param("id") PK id);

    @DeleteProvider(type = BaseSelectProvider.class, method = "deleteBy")
    int deleteBy(@Param("criteria") Criteria criteria);


    /* insert operation
    -----------------------------------------------------
     */
    @InsertProvider(type = BaseSelectProvider.class, method = "insert")
    @Options(keyProperty = "entity.id", useGeneratedKeys = true)
    int insert(@Param("entity") T entity);

    @InsertProvider(type = BaseSelectProvider.class, method = "insertBatch")
    int insertBatch(@Param("entityCollection") T... entityCollection);

    /* update operation
    -----------------------------------------------------
     */
    @UpdateProvider(type = BaseSelectProvider.class, method = "update")
    int update(@Param("entity") T entity);


    @UpdateProvider(type = BaseSelectProvider.class, method = "updateBy")
    int updateBy(@Param("entity") T entity, @Param("criteria") Criteria criteria);

}
