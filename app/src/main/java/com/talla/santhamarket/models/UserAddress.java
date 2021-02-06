package com.talla.santhamarket.models;

public class UserAddress
{
    private String user_country;
    private String user_state;
    private String user_district;
    private String user_city;
    private String user_pincode;
    private String user_streetAddress;
    private String user_doorNo;
    private String user_nearBy;
    private String user_lat;
    private  String user_long;

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

    public String getUser_district() {
        return user_district;
    }

    public void setUser_district(String user_district) {
        this.user_district = user_district;
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

    public String getUser_doorNo() {
        return user_doorNo;
    }

    public void setUser_doorNo(String user_doorNo) {
        this.user_doorNo = user_doorNo;
    }

    public String getUser_nearBy() {
        return user_nearBy;
    }

    public void setUser_nearBy(String user_nearBy) {
        this.user_nearBy = user_nearBy;
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

    @Override
    public String toString() {
        return "UserAddress{" +
                "user_country='" + user_country + '\'' +
                ", user_state='" + user_state + '\'' +
                ", user_district='" + user_district + '\'' +
                ", user_city='" + user_city + '\'' +
                ", user_pincode='" + user_pincode + '\'' +
                ", user_streetAddress='" + user_streetAddress + '\'' +
                ", user_doorNo='" + user_doorNo + '\'' +
                ", user_nearBy='" + user_nearBy + '\'' +
                ", user_lat='" + user_lat + '\'' +
                ", user_long='" + user_long + '\'' +
                '}';
    }
}
