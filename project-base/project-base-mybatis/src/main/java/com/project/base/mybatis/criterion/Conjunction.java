package com.project.base.mybatis.criterion;

public class Conjunction extends Junction {

    private static final long serialVersionUID = 4568982841323005717L;

    protected Conjunction(Criterion... criterion) {
        super(Nature.AND, criterion);
    }
}
