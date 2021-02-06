package com.talla.santhamarket.models;

import java.util.List;
import java.util.Map;

public class ProductModel
{
    private Long category_id;
    private Long product_id;
    private String product_name;
    private String seller_name;
    private String description;
    private Long product_price;
    private Long mrp_price;
    private Long max_quantity;
    private Long product_weight;
    private boolean cod_available;
    private Map<String, Object> product_colors;
    private Map<String, Object> product_sizes;
    private List<String> product_images;


    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Long product_price) {
        this.product_price = product_price;
    }

    public Long getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(Long mrp_price) {
        this.mrp_price = mrp_price;
    }

    public Long getMax_quantity() {
        return max_quantity;
    }

    public void setMax_quantity(Long max_quantity) {
        this.max_quantity = max_quantity;
    }

    public Long getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(Long product_weight) {
        this.product_weight = product_weight;
    }

    public boolean isCod_available() {
        return cod_available;
    }

    public void setCod_available(boolean cod_available) {
        this.cod_available = cod_available;
    }

    public Map<String, Object> getProduct_colors() {
        return product_colors;
    }

    public void setProduct_colors(Map<String, Object> product_colors) {
        this.product_colors = product_colors;
    }

    public Map<String, Object> getProduct_sizes() {
        return product_sizes;
    }

    public void setProduct_sizes(Map<String, Object> product_sizes) {
        this.product_sizes = product_sizes;
    }

    public List<String> getProduct_images() {
        return product_images;
    }

    public void setProduct_images(List<String> product_images) {
        this.product_images = product_images;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "category_id=" + category_id +
                ", product_id=" + product_id +
                ", product_name='" + product_name + '\'' +
                ", seller_name='" + seller_name + '\'' +
                ", description='" + description + '\'' +
                ", product_price=" + product_price +
                ", mrp_price=" + mrp_price +
                ", max_quantity=" + max_quantity +
                ", product_weight=" + product_weight +
                ", cod_available=" + cod_available +
                ", product_colors=" + product_colors +
                ", product_sizes=" + product_sizes +
                ", product_images=" + product_images +
                '}';
    }
}
