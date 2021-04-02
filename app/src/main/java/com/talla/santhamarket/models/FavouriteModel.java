package com.talla.santhamarket.models;

public class FavouriteModel
{
    private String product_id;
    private String userId;
    private String timestamp;
    private String favId;


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    @Override
    public String toString() {
        return "FavouriteModel{" +
                "product_id=" + product_id +
                ", userId='" + userId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", favId='" + favId + '\'' +
                '}';
    }
}
