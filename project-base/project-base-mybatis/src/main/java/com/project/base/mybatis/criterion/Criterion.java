package com.project.base.mybatis.criterion;

import java.io.Serializable;
import java.util.List;

public interface Criterion extends Serializable {
    String toSqlString(String ognlPrefix, List<String> parameterCollection);
}
