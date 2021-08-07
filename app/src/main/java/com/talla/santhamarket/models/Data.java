package com.talla.santhamarket.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data
{
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("openScreen")
    @Expose
    private String openScreen;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOpenScreen() {
        return openScreen;
    }

    public void setOpenScreen(String openScreen) {
        this.openScreen = openScreen;
    }

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", openScreen='" + openScreen + '\'' +
                '}';
    }
}
