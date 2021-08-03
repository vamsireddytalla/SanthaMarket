package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;

public class CategoryModel implements Serializable {

    private String id;
    private String cat_name;
    private String cat_logo;
    private String timestamp;
    private String shopId;

    public CategoryModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_logo() {
        return cat_logo;
    }

    public void setCat_logo(String cat_logo) {
        this.cat_logo = cat_logo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "id='" + id + '\'' +
                ", cat_name='" + cat_name + '\'' +
                ", cat_logo='" + cat_logo + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", shopId='" + shopId + '\'' +
                '}';
    }
}
