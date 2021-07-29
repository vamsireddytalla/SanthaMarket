package com.talla.santhamarket.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SCModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("entity")
    @Expose
    private String entity;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("amount_paid")
    @Expose
    private Integer amountPaid;
    @SerializedName("amount_due")
    @Expose
    private Integer amountDue;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("receipt")
    @Expose
    private String receipt;
    @SerializedName("offer_id")
    @Expose
    private Object offerId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("attempts")
    @Expose
    private Integer attempts;
    @SerializedName("notes")
    @Expose
    private List<Object> notes = null;
    @SerializedName("created_at")
    @Expose
    private Integer createdAt;
    private String user_name;
    private String timestamp;
    private String phoneNumber;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Integer amountDue) {
        this.amountDue = amountDue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public Object getOfferId() {
        return offerId;
    }

    public void setOfferId(Object offerId) {
        this.offerId = offerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public List<Object> getNotes() {
        return notes;
    }

    public void setNotes(List<Object> notes) {
        this.notes = notes;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "SCModel{" +
                "id='" + id + '\'' +
                ", entity='" + entity + '\'' +
                ", amount=" + amount +
                ", amountPaid=" + amountPaid +
                ", amountDue=" + amountDue +
                ", currency='" + currency + '\'' +
                ", receipt='" + receipt + '\'' +
                ", offerId=" + offerId +
                ", status='" + status + '\'' +
                ", attempts=" + attempts +
                ", notes=" + notes +
                ", createdAt=" + createdAt +
                ", user_name='" + user_name + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
