package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;

public class RatingModel implements Serializable
{
    private String ratingMessage;
    private double rating;
    private List<String> ratingImages;

    public RatingModel() {
    }

    public RatingModel(String ratingMessage, double rating) {
        this.ratingMessage = ratingMessage;
        this.rating = rating;
    }

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

    public List<String> getRatingImages() {
        return ratingImages;
    }

    public void setRatingImages(List<String> ratingImages) {
        this.ratingImages = ratingImages;
    }


    @Override
    public String toString() {
        return "RatingModel{" +
                ", ratingMessage='" + ratingMessage + '\'' +
                ", rating=" + rating +
                ", ratingImages=" + ratingImages +
                '}';
    }
}
