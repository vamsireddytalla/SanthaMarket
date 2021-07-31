package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProductModel implements Serializable
{
    private String product_name;
    private String seller_name;
    private String product_id;
    private String description;
    private double product_price;
    private double mrp_price;
    private int max_quantity;
    private double product_weight;
    private boolean cod_available;
    private String category_id;
    private String sub_cat_id;
    private List<SubProductModel> subProductModelList;
    private boolean out_of_stock;
    private int selled_items;
    private String product_date;
    private String sellerId;
    private ItemTypeModel itemTypeModel;
    private List<String> tagsList;
    private double total_ratings=0;
    private double avgRatings=0;
    private String video_link;
    private int totalStock;
    private String selectedColor;
    private String selectedSize;
    private int temp_qty;
    private boolean temp_favourite=false;
    private String temp_favouriteId;
    private String extra_field;
    private double delivery_charges;
    private SpecificationModel specificationModel;


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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public double getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(double mrp_price) {
        this.mrp_price = mrp_price;
    }

    public double getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(double product_weight) {
        this.product_weight = product_weight;
    }

    public boolean isCod_available() {
        return cod_available;
    }

    public void setCod_available(boolean cod_available) {
        this.cod_available = cod_available;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public List<SubProductModel> getSubProductModelList() {
        return subProductModelList;
    }

    public void setSubProductModelList(List<SubProductModel> subProductModelList) {
        this.subProductModelList = subProductModelList;
    }

    public boolean isOut_of_stock() {
        return out_of_stock;
    }

    public void setOut_of_stock(boolean out_of_stock) {
        this.out_of_stock = out_of_stock;
    }


    public String getProduct_date() {
        return product_date;
    }

    public void setProduct_date(String product_date) {
        this.product_date = product_date;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public ItemTypeModel getItemTypeModel() {
        return itemTypeModel;
    }

    public void setItemTypeModel(ItemTypeModel itemTypeModel) {
        this.itemTypeModel = itemTypeModel;
    }

    public List<String> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<String> tagsList) {
        this.tagsList = tagsList;
    }

    public double getTotal_ratings() {
        return total_ratings;
    }

    public void setTotal_ratings(double total_ratings) {
        this.total_ratings = total_ratings;
    }

    public double getAvgRatings() {
        return avgRatings;
    }

    public void setAvgRatings(double avgRatings) {
        this.avgRatings = avgRatings;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }


    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }


    public int getTemp_qty() {
        return temp_qty;
    }

    public void setTemp_qty(int temp_qty) {
        this.temp_qty = temp_qty;
    }

    public boolean isTemp_favourite() {
        return temp_favourite;
    }

    public void setTemp_favourite(boolean temp_favourite) {
        this.temp_favourite = temp_favourite;
    }

    public String getTemp_favouriteId() {
        return temp_favouriteId;
    }

    public void setTemp_favouriteId(String temp_favouriteId) {
        this.temp_favouriteId = temp_favouriteId;
    }

    public String getExtra_field() {
        return extra_field;
    }

    public void setExtra_field(String extra_field) {
        this.extra_field = extra_field;
    }

    public SpecificationModel getSpecificationModel() {
        return specificationModel;
    }

    public void setSpecificationModel(SpecificationModel specificationModel) {
        this.specificationModel = specificationModel;
    }


    public double getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(double delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public int getMax_quantity() {
        return max_quantity;
    }

    public void setMax_quantity(int max_quantity) {
        this.max_quantity = max_quantity;
    }

    public int getSelled_items() {
        return selled_items;
    }

    public void setSelled_items(int selled_items) {
        this.selled_items = selled_items;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "product_name='" + product_name + '\'' +
                ", seller_name='" + seller_name + '\'' +
                ", product_id='" + product_id + '\'' +
                ", description='" + description + '\'' +
                ", product_price=" + product_price +
                ", mrp_price=" + mrp_price +
                ", max_quantity=" + max_quantity +
                ", product_weight=" + product_weight +
                ", cod_available=" + cod_available +
                ", category_id='" + category_id + '\'' +
                ", sub_cat_id='" + sub_cat_id + '\'' +
                ", subProductModelList=" + subProductModelList +
                ", out_of_stock=" + out_of_stock +
                ", selled_items=" + selled_items +
                ", product_date='" + product_date + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", itemTypeModel=" + itemTypeModel +
                ", tagsList=" + tagsList +
                ", total_ratings=" + total_ratings +
                ", avgRatings=" + avgRatings +
                ", video_link='" + video_link + '\'' +
                ", totalStock=" + totalStock +
                ", selectedColor='" + selectedColor + '\'' +
                ", selectedSize='" + selectedSize + '\'' +
                ", temp_qty=" + temp_qty +
                ", temp_favourite=" + temp_favourite +
                ", temp_favouriteId='" + temp_favouriteId + '\'' +
                ", extra_field='" + extra_field + '\'' +
                ", delivery_charges='" + delivery_charges + '\'' +
                ", specificationModel=" + specificationModel +
                '}';
    }
}
