package com.wingplus.coomohome.util;

import java.text.DecimalFormat;

/**
 * @author leaffun.
 *         Create on 2017/9/29.
 */
public class PriceFixUtil {

    public static String format(double price) {
        if (price == 0) {
            return "0.00";
        }


        double y = price * 100 % 100;
        DecimalFormat format;
        if (y > 9) {
            if (y % 10 == 0) {
                format = new DecimalFormat("0.0");
            } else {
                format = new DecimalFormat("0.00");
            }
        } else if (y > 0) {
            format = new DecimalFormat("0.00");
        } else {
            format = new DecimalFormat("#");
        }


        return format.format(price);
    }

    public static String formatPay(double pay) {
//        pay = pay * 100D / 100D;
        return String.format("%.2f", pay);
    }

    /**
     * true 需要原价划线
     * @param price
     * @param OriginalPrice
     * @return
     */
    public static boolean checkNeedScribing(double price, double OriginalPrice) {
        return !(OriginalPrice == 0 || price == OriginalPrice);
    }
}
