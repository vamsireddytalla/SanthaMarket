package com.talla.santhamarket.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FcmResponseData
{
    @SerializedName("message_id")
    @Expose
    private String messageId;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "FcmResponseData{" +
                "messageId='" + messageId + '\'' +
                '}';
    }
}
