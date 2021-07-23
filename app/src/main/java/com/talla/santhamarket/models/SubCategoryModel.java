package com.talla.santhamarket.models;

public class SubCategoryModel
{
    private String id;
    private String sub_cat_name;
    private String sub_cat_logo;
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }

    public String getSub_cat_logo() {
        return sub_cat_logo;
    }

    public void setSub_cat_logo(String sub_cat_logo) {
        this.sub_cat_logo = sub_cat_logo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SubCategoryModel{" +
                "id='" + id + '\'' +
                ", sub_cat_name='" + sub_cat_name + '\'' +
                ", sub_cat_logo='" + sub_cat_logo + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
