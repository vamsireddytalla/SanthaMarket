package com.talla.santhamarket.models;

public class TokenModel
{
    private String userToken;
    private String tokenUpdated_date;
    private String deviceId;


    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getTokenUpdated_date() {
        return tokenUpdated_date;
    }

    public void setTokenUpdated_date(String tokenUpdated_date) {
        this.tokenUpdated_date = tokenUpdated_date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "TokenModel{" +
                "userToken='" + userToken + '\'' +
                ", tokenUpdated_date='" + tokenUpdated_date + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
