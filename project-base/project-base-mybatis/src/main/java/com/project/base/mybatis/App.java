package com.project.base.mybatis;

import com.project.base.mybatis.criterion.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        Criteria criteria = Criteria.forClass(App.class);
        criteria.add(Restrictions.eq("p1","zhanghc"));
        criteria.add(Restrictions.ne("p2","10"));
        Criterion conjunction = Restrictions.conjunction(
                Restrictions.eq("p3","zhanghc"),
                Restrictions.disjunction(
                        Restrictions.eq("p4","zhanghc"),
                        Restrictions.eq("p5","zhanghc"),
                        Restrictions.in("p6",1,2,3,4,5)
                ),
                Restrictions.eq("p7","zhanghc")
        );

        criteria.add(conjunction);

        System.out.println(criteria.toSqlString());


        System.out.println( "Hello World!" );
    }
}
