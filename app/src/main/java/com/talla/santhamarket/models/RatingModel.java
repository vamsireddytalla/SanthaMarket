package com.talla.santhamarket.models;

import java.util.List;

public class RatingModel
{
    private Long userId;
    private Long productId;
    private Long categoryId;
    private String rating_title;
    private String rating_desc;
    private Long rating;
    private List<ProductImageModel> productImageModelList;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getRating_title() {
        return rating_title;
    }

    public void setRating_title(String rating_title) {
        this.rating_title = rating_title;
    }

    public String getRating_desc() {
        return rating_desc;
    }

    public void setRating_desc(String rating_desc) {
        this.rating_desc = rating_desc;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public List<ProductImageModel> getProductImageModelList() {
        return productImageModelList;
    }

    public void setProductImageModelList(List<ProductImageModel> productImageModelList) {
        this.productImageModelList = productImageModelList;
    }

    @Override
    public String toString() {
        return "RatingModel{" +
                "userId=" + userId +
                ", productId=" + productId +
                ", categoryId=" + categoryId +
                ", rating_title='" + rating_title + '\'' +
                ", rating_desc='" + rating_desc + '\'' +
                ", rating=" + rating +
                ", productImageModelList=" + productImageModelList +
                '}';
    }
}
