package com.talla.santhamarket.models;

public class ProductImageModel
{
    private String product_image;
    private String product_bg_color;

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_bg_color() {
        return product_bg_color;
    }

    public void setProduct_bg_color(String product_bg_color) {
        this.product_bg_color = product_bg_color;
    }

    @Override
    public String toString() {
        return "ProductImageModel{" +
                "product_image='" + product_image + '\'' +
                ", product_bg_color='" + product_bg_color + '\'' +
                '}';
    }
}
