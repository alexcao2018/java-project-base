package com.project.base.dotnet.rpc;


/**
 *
 */
public abstract class AbstractQuery<T extends AbstractModel> implements IQuery {

    /**
     * 默认主键（Id）的 查询 （数组相当于 数据库查询  “ in ” 的 操作)。
     */
    private int[] Ids;

    /**
     * 单次获取多少条信息。 （分页相当于 PageSize）。
     */
    private Integer take;

    /**
     * 跳过多少条数据开始。（分页 pageNum * pageSize + 1 类似）。
     */
    private Integer skip;

    /**
     *  分页时候返回的根据条件查询的总数。
     */
    private Integer count;

    /**
     * 手动指定排序数据库字段（字符串，必须和数据库字段名字一样，大小写一直）。
     */
    private String orderFields;

    /**
     * 指定排序的规则（一个枚举 如下，默认ASC）。
     */
    private OrderDirection orderDirection = OrderDirection.Asc;

    /**
     * 指定当前Query查询是否需要开启读写分离功能（默认是false--兼容现有版本场景）。
     */
    private Boolean readOnly;

    public int[] getIds() {
        return Ids;
    }

    public void setIds(int[] ids) {
        Ids = ids;
    }

    public Integer getTake() {
        return take;
    }

    public void setTake(Integer take) {
        this.take = take;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(String orderFields) {
        this.orderFields = orderFields;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}
