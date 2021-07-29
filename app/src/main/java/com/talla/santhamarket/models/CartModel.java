package com.talla.santhamarket.models;

import java.io.Serializable;

public class CartModel implements Serializable
{
    private String cart_doc_id;
    private String cart_product_id;
    private String user_id;
    private String size_chart;
    private String selected_color;
    private ItemTypeModel itemTypeModel;
    private String timestamp;

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

    public String getSize_chart() {
        return size_chart;
    }

    public void setSize_chart(String size_chart) {
        this.size_chart = size_chart;
    }

    public String getSelected_color() {
        return selected_color;
    }

    public void setSelected_color(String selected_color) {
        this.selected_color = selected_color;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ItemTypeModel getItemTypeModel() {
        return itemTypeModel;
    }

    public void setItemTypeModel(ItemTypeModel itemTypeModel) {
        this.itemTypeModel = itemTypeModel;
    }


    @Override
    public String toString() {
        return "CartModel{" +
                "cart_doc_id='" + cart_doc_id + '\'' +
                ", cart_product_id='" + cart_product_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", size_chart='" + size_chart + '\'' +
                ", selected_color='" + selected_color + '\'' +
                ", itemTypeModel=" + itemTypeModel +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
