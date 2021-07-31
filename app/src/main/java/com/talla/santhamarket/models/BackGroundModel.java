package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;

public class BackGroundModel implements Serializable
{
    private List<CartModel> cartModelList;
    private List<ProductModel> productModelList;

    public List<CartModel> getCartModelList() {
        return cartModelList;
    }

    public void setCartModelList(List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
    }

    public List<ProductModel> getProductModelList() {
        return productModelList;
    }

    public void setProductModelList(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
    }

    @Override
    public String toString() {
        return "BackGroundModel{" +
                "cartModelList=" + cartModelList +
                ", productModelList=" + productModelList +
                '}';
    }
}
