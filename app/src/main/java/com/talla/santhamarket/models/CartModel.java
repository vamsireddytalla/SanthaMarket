package com.talla.santhamarket.models;

public class CartModel
{
    private String cart_doc_id;
    private String cart_product_id;
    private String user_id;

    public String getCart_doc_id() {
        return cart_doc_id;
    }

    public void setCart_doc_id(String cart_doc_id) {
        this.cart_doc_id = cart_doc_id;
    }

    public String getCart_product_id() {
        return cart_product_id;
    }

    public void setCart_product_id(String cart_product_id) {
        this.cart_product_id = cart_product_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "CartModel{" +
                "cart_doc_id='" + cart_doc_id + '\'' +
                ", cart_product_id='" + cart_product_id + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
