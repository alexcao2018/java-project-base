package com.project.base.common.enums;

public enum EnumContentType {
    Json("application/json", 0),
    XWwwFormUrlEncoded("application/x-www-form-urlencoded", 1);
    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private EnumContentType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (EnumContentType c : EnumContentType.values()) {
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
