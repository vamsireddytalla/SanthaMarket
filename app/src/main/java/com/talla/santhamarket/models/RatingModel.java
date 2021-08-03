package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;

public class RatingModel implements Serializable
{
    private String ratingMessage;
    private double rating;
    private String timestamp;
    private String userName;
    private List<String> ratingImages;

    public String getRatingMessage() {
        return ratingMessage;
    }

    public void setRatingMessage(String ratingMessage) {
        this.ratingMessage = ratingMessage;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getRatingImages() {
        return ratingImages;
    }

    public void setRatingImages(List<String> ratingImages) {
        this.ratingImages = ratingImages;
    }

    @Override
    public String toString() {
        return "RatingModel{" +
                "ratingMessage='" + ratingMessage + '\'' +
                ", rating=" + rating +
                ", timestamp='" + timestamp + '\'' +
                ", userName='" + userName + '\'' +
                ", ratingImages=" + ratingImages +
                '}';
    }
}
