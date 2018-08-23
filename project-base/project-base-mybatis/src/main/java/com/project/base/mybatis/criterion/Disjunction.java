package com.project.base.mybatis.criterion;

public class Disjunction extends Junction {
    protected Disjunction(Criterion... conditions) {
        super(Nature.OR, conditions);
    }
}
