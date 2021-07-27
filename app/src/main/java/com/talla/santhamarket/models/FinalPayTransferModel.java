package com.talla.santhamarket.models;

import java.util.List;

public class FinalPayTransferModel
{
    private List<ProductModel> productModelsList;
    private String totalPayment;

    public List<ProductModel> getProductModelsList() {
        return productModelsList;
    }

    public void setProductModelsList(List<ProductModel> productModelsList) {
        this.productModelsList = productModelsList;
    }

    public String getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
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
