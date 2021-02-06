package com.talla.santhamarket.utills;

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
}
