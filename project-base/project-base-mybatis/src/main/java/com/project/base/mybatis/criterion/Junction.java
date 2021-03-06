package com.project.base.mybatis.criterion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Junction implements Criterion {

    private static final long serialVersionUID = 3764811040014923427L;
    private Nature nature;

    private List<Criterion> conditions = new ArrayList<>();

    protected Junction(Nature nature, Criterion... criterion) {

        for (Criterion criterionItem : criterion) {
            if(criterionItem==null)
                continue;
            conditions.add(criterionItem);
        }

        this.nature = nature;
    }

    public Junction add(Criterion criterion) {
        if(criterion==null)
            return this;

        conditions.add(criterion);
        return this;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature(Nature nature) {
        this.nature = nature;
    }


    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < conditions.size(); i++) {
            Criterion criterionEntry = conditions.get(i);
            sql.append("(");
            sql.append(criterionEntry.toSqlString(ognlPrefix + ".conditions[" + i + "]", parameterCollection));
            if (i != conditions.size() - 1)
                sql.append(")" + nature.getOperator());
            else
                sql.append(")");
        }
        return sql.toString();
    }

    public List<Criterion> getConditions() {
        return conditions;
    }

    public static enum Nature {
        /**
         * An AND
         */
        AND,
        /**
         * An OR
         */
        OR;

        /**
         * The corresponding SQL operator
         *
         * @return SQL operator
         */
        public String getOperator() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
