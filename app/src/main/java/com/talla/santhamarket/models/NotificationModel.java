package com.talla.santhamarket.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel
{
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("collapse_key")
    @Expose
    private String collapseKey;
    @SerializedName("data")
    @Expose
    private Data data;


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "to='" + to + '\'' +
                ", collapseKey='" + collapseKey + '\'' +
                ", data=" + data +
                '}';
    }
}
