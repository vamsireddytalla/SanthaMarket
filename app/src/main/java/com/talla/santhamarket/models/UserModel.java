package com.talla.santhamarket.models;

public class UserModel
{
    private String user_number;
    private String user_alter_no;
    private String user_id;
    private String walletBal;
    private String user_email;
    private String user_name;
    private String image_url;
    private String deviceId;

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getUser_alter_no() {
        return user_alter_no;
    }

    public void setUser_alter_no(String user_alter_no) {
        this.user_alter_no = user_alter_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWalletBal() {
        return walletBal;
    }

    public void setWalletBal(String walletBal) {
        this.walletBal = walletBal;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "user_number='" + user_number + '\'' +
                ", user_alter_no='" + user_alter_no + '\'' +
                ", user_id='" + user_id + '\'' +
                ", walletBal='" + walletBal + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_name='" + user_name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
