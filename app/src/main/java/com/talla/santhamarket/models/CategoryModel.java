package com.talla.santhamarket.models;

import java.util.List;

public class CategoryModel {
    private Long index;
    private String categoryName;
    private String icon;

    public CategoryModel() {
    }


    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public String toString() {
        return "CategoryModel{" +
                "index=" + index +
                ", categoryName='" + categoryName + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
