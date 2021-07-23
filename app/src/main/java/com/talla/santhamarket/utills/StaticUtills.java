package com.talla.santhamarket.utills;

import java.text.SimpleDateFormat;

public class StaticUtills
{
    public static int discountPercentage(double selling_price, double max_price)
    {
        // Calculating discount
        double discount = max_price - selling_price;
        // Calculating discount percentage
        double disPercent = (discount / max_price) * 100;
        return (int) disPercent;
    }

    public static String getTimeStamp()
    {
       return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
    }

}
