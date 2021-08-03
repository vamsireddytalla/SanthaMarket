package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.Map;

public class RegisterModel implements Serializable
{
    private String name;
    private String email;
    private String number;
    private String image_url;
    private boolean shopOpenedOrNot;
    private boolean isVerified;
    private String shop_id;
    private ShopAddress shopAddress;
    private Map<String, Object> categoriesList;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }


    public ShopAddress getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(ShopAddress shopAddress) {
        this.shopAddress = shopAddress;
    }

    public boolean isShopOpenedOrNot() {
        return shopOpenedOrNot;
    }

    public void setShopOpenedOrNot(boolean shopOpenedOrNot) {
        this.shopOpenedOrNot = shopOpenedOrNot;
    }

    public Map<String, Object> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(Map<String, Object> categoriesList) {
        this.categoriesList = categoriesList;
    }

    @Override
    public String toString() {
        return "RegisterModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", number='" + number + '\'' +
                ", image_url='" + image_url + '\'' +
                ", shopOpenedOrNot=" + shopOpenedOrNot +
                ", isVerified=" + isVerified +
                ", shop_id='" + shop_id + '\'' +
                ", shopAddress=" + shopAddress +
                ", categoriesList=" + categoriesList +
                '}';
    }
}
