package com.talla.santhamarket.utills;

import java.text.SimpleDateFormat;

public class StaticUtills {
    public static int discountPercentage(double selling_price, double max_price) {
        // Calculating discount
        double discount = max_price - selling_price;
        // Calculating discount percentage
        double disPercent = (discount / max_price) * 100;
        return (int) disPercent;
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
    }


    public static String getLocalDeliveryDate() {
        return new SimpleDateFormat(" dd MMMM yyyy").format(new java.util.Date());
    }


    public static int productWeightConversion(int productWeight) {
        if (productWeight < 1 || productWeight <= 2) {
            return 110;
        } else if (productWeight > 2 && productWeight <= 5) {
            return 250;
        } else if (productWeight > 5 && productWeight <= 8) {
            return 350;
        } else if (productWeight > 8 && productWeight <= 10) {
            return 400;
        }
        return productWeight*70;
    }


}
