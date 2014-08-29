package com.hhhy.db.beans.item;

public enum SrcType {
    news("新闻", 1), bbs("论坛", 2), blog("博客", 3), elePaper("电子报", 4), weibo("微博",
            5);
    private String name;
    private int index;

    private SrcType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (SrcType t : SrcType.values()) {
            if (t.getIndex() == index) {
                return t.getName();
            }
        }
        return null;
    }

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