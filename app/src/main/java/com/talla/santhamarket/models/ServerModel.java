package com.talla.santhamarket.models;

public class ServerModel {
    private String payment_key;
    private String payment_secret_key;
    private String app_version;
    private String tags_url;
    private String fcm_server_token;

    public String getPayment_key() {
        return payment_key;
    }

    public void setPayment_key(String payment_key) {
        this.payment_key = payment_key;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getTags_url() {
        return tags_url;
    }

    public void setTags_url(String tags_url) {
        this.tags_url = tags_url;
    }

    public String getFcm_server_token() {
        return fcm_server_token;
    }

    public void setFcm_server_token(String fcm_server_token) {
        this.fcm_server_token = fcm_server_token;
    }

    public String getPayment_secret_key() {
        return payment_secret_key;
    }

    public void setPayment_secret_key(String payment_secret_key) {
        this.payment_secret_key = payment_secret_key;
    }

    @Override
    public String toString() {
        return "ServerModel{" +
                "payment_key='" + payment_key + '\'' +
                ", payment_secret_key='" + payment_secret_key + '\'' +
                ", app_version='" + app_version + '\'' +
                ", tags_url='" + tags_url + '\'' +
                ", fcm_server_token='" + fcm_server_token + '\'' +
                '}';
    }
}
