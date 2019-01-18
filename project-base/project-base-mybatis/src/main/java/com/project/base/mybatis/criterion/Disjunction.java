package com.project.base.mybatis.criterion;

public class Disjunction extends Junction {
    private static final long serialVersionUID = 6362017530954973810L;

    protected Disjunction(Criterion... conditions) {
        super(Nature.OR, conditions);
    }
}
