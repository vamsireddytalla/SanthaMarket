package com.talla.santhamarket.models;

import java.io.Serializable;

public class DeliveryModel implements Serializable
{
    private String deliveryTitle;
    private String deliveryDate;
    private String deliveryMessage;

    public String getDeliveryTitle() {
        return deliveryTitle;
    }

    public void setDeliveryTitle(String deliveryTitle) {
        this.deliveryTitle = deliveryTitle;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryMessage() {
        return deliveryMessage;
    }

    public void setDeliveryMessage(String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }

    @Override
    public String toString() {
        return "DeliveryModel{" +
                "deliveryTitle='" + deliveryTitle + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", deliveryMessage='" + deliveryMessage + '\'' +
                '}';
    }
}
