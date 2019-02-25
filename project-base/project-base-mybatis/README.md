说明：
1、mybatis 扩展项目
2、支持通用mapper
3、支持动态criteria查询
4、支持映射
5、集成PageHelper , 支持分页

目前只支持spring boot 接入
接入方式：
    @SpringBootApplication
    @ComponentScan({"com.project.base.mybatis"})
    public class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
        }
    }
    
一、查询示例
        
        /* 动态查询 */
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
            

            /* insert */
            TbTab tbTab = new TbTab();
            tbTab.setName("name" + (new Date()).toString());
            tbTab.setType(1);
            tbTab.setBeginTime(new Date());
            tbTab.setEndTime(new Date());
            tbTab.setSort(100);
            tbTab.setEffectCity("111");
            tbTab.setEffectPlatform("1110");
            tbTab.setIsEnabled(1);
            tbTab.setIsDeleted(0);
    
            daoTbTab.insert(tbTab);
    
            /* update */
            tbTab.setName("other");
            tbTab.setType(100);
            daoTbTab.update(tbTab);
    
            /* delete  batch */
            Criteria criteria = Criteria.forClass(TbTab.class);
            criteria.add(Restrictions.eq(TbTab._name, "other"));
            int result = daoTbTab.deleteBy(criteria);
    
            /* insert batch */
            TbTab tbTab1 = new TbTab();
            tbTab1.setName("美食铺子111");
            tbTab1.setType(11);
            tbTab1.setBeginTime(new Date());
            tbTab1.setEndTime(new Date());
            tbTab1.setSort(100);
            tbTab1.setEffectCity("111");
            tbTab1.setEffectPlatform("1110");
            tbTab1.setIsEnabled(1);
            tbTab1.setIsDeleted(0);
    
            TbTab tbTab2 = new TbTab();
            tbTab2.setName("美食铺子222");
            tbTab2.setType(1);
            tbTab2.setBeginTime(new Date());
            tbTab2.setEndTime(new Date());
            tbTab2.setSort(100);
            tbTab2.setEffectCity("111");
            tbTab2.setEffectPlatform("1110");
            tbTab2.setIsEnabled(1);
            tbTab2.setIsDeleted(0);
    
            daoTbTab.insertBatch(tbTab1, tbTab2);
    
    
            /* query by page */
            Criteria criteriaPage = Criteria.forClass(TbTab.class);
            criteriaPage.add(Restrictions.gt(TbTab._id, 1)).addOrder(Order.desc(TbTab._id));
            PageInfo pageInfo1 = new PageInfo(0, 5);
            daoTbTab.paginateListBy(criteriaPage, pageInfo1);
            System.out.println(pageInfo1);
    
    
            /* 自定义查询 by criteria */
            Criteria criteriaProjection = Criteria.forClass(TbTab.class);
            criteriaProjection.add(Restrictions.eq(TbTab._name, "美食铺子111"));
            criteriaProjection.setProjection(TbTab._name, TbTab._type, TbTab._beginTime).distinct();
            PageInfo pageInfo2 = new PageInfo(0, 2);
            List<TbTab> tabs = daoTbTab.selectTestByCriteria(2, criteriaProjection, pageInfo2);
            List<TbTab> tabs1 = daoTbTab.selectTestByCriteria(2, criteriaProjection, pageInfo2);
    
    
            Criteria criteriaProjection1 = Criteria.forClass(TbTab.class);
            criteriaProjection1.add(Restrictions.gt(TbTab._id, 5))
                    .add(Restrictions.lt(TbTab._id, 200));
            criteriaProjection1.setProjection(TbTab._name, TbTab._type, TbTab._beginTime).distinct();
            PageInfo pageInfo3 = new PageInfo(0, 2);
            List<TbTab> tabs2 = daoTbTab.selectTestByCriteria(2, criteriaProjection1, pageInfo3);
    
    
            /* 使用pageHelper 进行分页 */
            Page<TbTab> pageResult = PageHelper.startPage(1, 10)
                    .doSelectPage(() -> daoTbTab.selectTestPageHelper(2));
二、事务操作
   1、 在方法上加@Transactional , 通过spring 进行事务管理
   2、 显示code 管理
   
       @Autowired
       private PlatformTransactionManager transactionManager;
       
       事务开始：
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        
        执行逻辑
        
        事务提交
        transactionManager.commit(transactionStatus);
        
        事务回滚
        transactionManager.rollback(transactionStatus);
        
        
        
        
    
