package com.project.base.common.enums;

public enum EnumOrder {
    ASC("ASC", 0), DESC("DESC", 1);
    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private EnumOrder(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (EnumOrder c : EnumOrder.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
