package com.talla.santhamarket.models;

import java.io.Serializable;

public class ItemTypeModel implements Serializable
{
    private boolean isLocal;
    private String pincode;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @Override
    public String toString() {
        return "ItemTypeModel{" +
                "isLocal=" + isLocal +
                ", pincode='" + pincode + '\'' +
                '}';
    }
}
