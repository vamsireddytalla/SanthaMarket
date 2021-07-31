package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable
{
    private String product_id;
    private String product_name;
    private String product_image;
    private String selectedColor;
    private String selectedSize;
    private int selected_quantity;
    private double product_price;
    private double mrp_price;
    private double totalProductPrice;
    private String userId;
    private String seller_name;
    private String seller_id;
    private String order_id;
    private String ordered_date;
    private String payment_method;
    private int deliveryCharges;
    private UserAddress delvery_address;
    private String transaction_id;
    private String delivered_date;
    private boolean isDelivered = false;
    private String webUrl;
    private boolean paidOrNot = false;
    private boolean isLocal;
    private String payment_status_doc;
    private String order_doc_id;
    private List<DeliveryModel> deliveryModelList;
    private RatingModel ratingModel;


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }


    public int getSelected_quantity() {
        return selected_quantity;
    }

    public void setSelected_quantity(int selected_quantity) {
        this.selected_quantity = selected_quantity;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public double getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(double mrp_price) {
        this.mrp_price = mrp_price;
    }

    public double getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(double totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrdered_date() {
        return ordered_date;
    }

    public void setOrdered_date(String ordered_date) {
        this.ordered_date = ordered_date;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public UserAddress getDelvery_address() {
        return delvery_address;
    }

    public void setDelvery_address(UserAddress delvery_address) {
        this.delvery_address = delvery_address;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getDelivered_date() {
        return delivered_date;
    }

    public void setDelivered_date(String delivered_date) {
        this.delivered_date = delivered_date;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public boolean isPaidOrNot() {
        return paidOrNot;
    }

    public void setPaidOrNot(boolean paidOrNot) {
        this.paidOrNot = paidOrNot;
    }


    public List<DeliveryModel> getDeliveryModelList() {
        return deliveryModelList;
    }

    public void setDeliveryModelList(List<DeliveryModel> deliveryModelList) {
        this.deliveryModelList = deliveryModelList;
    }


    public int getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(int deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }


    public String getPayment_status_doc() {
        return payment_status_doc;
    }

    public void setPayment_status_doc(String payment_status_doc) {
        this.payment_status_doc = payment_status_doc;
    }


    public String getOrder_doc_id() {
        return order_doc_id;
    }

    public void setOrder_doc_id(String order_doc_id) {
        this.order_doc_id = order_doc_id;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public RatingModel getRatingModel() {
        return ratingModel;
    }

    public void setRatingModel(RatingModel ratingModel) {
        this.ratingModel = ratingModel;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "product_id='" + product_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_image='" + product_image + '\'' +
                ", selectedColor='" + selectedColor + '\'' +
                ", selectedSize='" + selectedSize + '\'' +
                ", selected_quantity=" + selected_quantity +
                ", product_price=" + product_price +
                ", mrp_price=" + mrp_price +
                ", totalProductPrice=" + totalProductPrice +
                ", userId='" + userId + '\'' +
                ", seller_name='" + seller_name + '\'' +
                ", seller_id='" + seller_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", ordered_date='" + ordered_date + '\'' +
                ", payment_method='" + payment_method + '\'' +
                ", deliveryCharges=" + deliveryCharges +
                ", delvery_address=" + delvery_address +
                ", transaction_id='" + transaction_id + '\'' +
                ", delivered_date='" + delivered_date + '\'' +
                ", isDelivered=" + isDelivered +
                ", webUrl='" + webUrl + '\'' +
                ", paidOrNot=" + paidOrNot +
                ", isLocal=" + isLocal +
                ", payment_status_doc='" + payment_status_doc + '\'' +
                ", order_doc_id='" + order_doc_id + '\'' +
                ", deliveryModelList=" + deliveryModelList +
                ", ratingModel=" + ratingModel +
                '}';
    }
}
