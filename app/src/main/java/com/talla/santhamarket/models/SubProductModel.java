package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;

public class SubProductModel implements Serializable
{
    private List<ProductImageModel> product_images;
    private List<String> product_sizes;
    private String product_color;


    public List<ProductImageModel> getProduct_images() {
        return product_images;
    }

    public void setProduct_images(List<ProductImageModel> product_images) {
        this.product_images = product_images;
    }

    public List<String> getProduct_sizes() {
        return product_sizes;
    }

    public void setProduct_sizes(List<String> product_sizes) {
        this.product_sizes = product_sizes;
    }


    public String getProduct_color() {
        return product_color;
    }

    public void setProduct_color(String product_color) {
        this.product_color = product_color;
    }

    @Override
    public String toString() {
        return "SubProductModel{" +
                "product_images=" + product_images +
                ", product_sizes=" + product_sizes +
                ", product_color='" + product_color + '\'' +
                '}';
    }
}
