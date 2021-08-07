package com.talla.santhamarket.models;

public class BannerModel
{
    private String banner_image;
    private String timestamp;
    private String catId;
    private String subCatId;
    private String productId;
    private boolean isClickable;


    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    @Override
    public String toString() {
        return "BannerModel{" +
                "banner_image='" + banner_image + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", catId='" + catId + '\'' +
                ", subCatId='" + subCatId + '\'' +
                ", productId='" + productId + '\'' +
                ", isClickable=" + isClickable +
                '}';
    }
}
