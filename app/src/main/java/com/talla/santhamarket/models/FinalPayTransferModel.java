package com.talla.santhamarket.models;

import java.util.List;

public class FinalPayTransferModel
{
    private List<ProductModel> productModelsList;
    private int totalPayment;

    public List<ProductModel> getProductModelsList() {
        return productModelsList;
    }

    public void setProductModelsList(List<ProductModel> productModelsList) {
        this.productModelsList = productModelsList;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }

    @Override
    public String toString() {
        return "FinalPayTransferModel{" +
                "productModelsList=" + productModelsList +
                ", totalPayment='" + totalPayment + '\'' +
                '}';
    }
}
