package com.project.base.mybatis.criterion;

public class Conjunction extends Junction {

    protected Conjunction( Criterion... criterion) {
        super(Nature.AND, criterion);
    }
}
