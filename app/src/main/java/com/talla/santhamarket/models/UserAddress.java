package com.talla.santhamarket.models;

public class UserAddress
{
    private String userId;
    private String user_name;
    private String user_phone;
    private String user_alter_phone;
    private String user_country;
    private String user_state;
    private String user_city;
    private String user_pincode;
    private String user_streetAddress;
    private String user_lat;
    private String user_long;
    private boolean defaultAddress=true;
    private String docID;

    public String getUser_country() {
        return user_country;
    }

    public void setUser_country(String user_country) {
        this.user_country = user_country;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }


    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_pincode() {
        return user_pincode;
    }

    public void setUser_pincode(String user_pincode) {
        this.user_pincode = user_pincode;
    }

    public String getUser_streetAddress() {
        return user_streetAddress;
    }

    public void setUser_streetAddress(String user_streetAddress) {
        this.user_streetAddress = user_streetAddress;
    }


    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_long() {
        return user_long;
    }

    public void setUser_long(String user_long) {
        this.user_long = user_long;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_alter_phone() {
        return user_alter_phone;
    }

    public void setUser_alter_phone(String user_alter_phone) {
        this.user_alter_phone = user_alter_phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    @Override
    public String toString() {
        return "UserAddress{" +
                "userId='" + userId + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_phone='" + user_phone + '\'' +
                ", user_alter_phone='" + user_alter_phone + '\'' +
                ", user_country='" + user_country + '\'' +
                ", user_state='" + user_state + '\'' +
                ", user_city='" + user_city + '\'' +
                ", user_pincode='" + user_pincode + '\'' +
                ", user_streetAddress='" + user_streetAddress + '\'' +
                ", user_lat='" + user_lat + '\'' +
                ", user_long='" + user_long + '\'' +
                ", defaultAddress=" + defaultAddress +
                ", docID='" + docID + '\'' +
                '}';
    }
}
