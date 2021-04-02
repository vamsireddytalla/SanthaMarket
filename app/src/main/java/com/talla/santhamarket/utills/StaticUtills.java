package com.talla.santhamarket.utills;

import java.text.SimpleDateFormat;

public class StaticUtills
{
    public static float discountPercentage(float selling_price, float max_price)
    {
        // Calculating discount
        float discount = max_price - selling_price;
        // Calculating discount percentage
        float disPercent = (discount / max_price) * 100;
        return disPercent;
    }

    public static String getTimeStamp()
    {
       return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
    }

}
