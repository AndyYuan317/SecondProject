package com.wingplus.coomohome.util;

import com.wingplus.coomohome.config.LogUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author leaffun.
 *         Create on 2017/9/26.
 */
public class TimeUtil {

    /**
     * 毫秒转视频的时分秒
     *
     * @param mes
     * @return
     */
    public static String mesToRx(int mes) {
        int sec = Math.round(mes / 1000f);
        int min = sec / 60;
        int hour = min / 60;

        sec = sec % 60;
        min = min % 60;

        String h = hour == 0 ? "" : hour + ":";
        String m = min > 9 ? (min + ":") : ("0" + min + ":");
        String s = sec > 9 ? ("" + sec) : ("0" + sec);

        LogUtil.i("TimeUtil", mes + "->" + sec);

        return h + m + s;
    }

    /**
     * 转换时间为yyyy.MM.dd hh:mm:ss
     *
     * @param mes 毫秒
     * @return
     */
    public static String mesToDateYMDHMS(long mes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mes);

        String s = calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.DATE)
                + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":";

        int m = calendar.get(Calendar.MINUTE);
        s = s + (m > 9 ? m : "0" + m) + ":";

        int sec = calendar.get(Calendar.SECOND);
        s = s + (sec > 9 ? sec : "0" + sec);
        return s;
    }


    /**
     * 转换时间为yyyy.MM.dd hh:mm
     *
     * @param mes 毫秒
     * @return
     */
    public static String mesToDate(long mes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mes);

        String s = calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.DATE)
                + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":";

        int m = calendar.get(Calendar.MINUTE);
        s = s + (m > 9 ? m : "0" + m);
        return s;
    }

    /**
     * 转换时间为yyyy-MM-dd
     *
     * @param mes 毫秒
     * @return
     */
    public static String mesToYMD(long mes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mes);

        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DATE);

        return y + "-" + (m > 9 ? m : "0" + m) + "-" + (d > 9 ? d : "0" + d);
    }


    public static long YMDHMToMes(String time, String regular) {
        long mes = System.currentTimeMillis();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(regular);
            Date date = sdf.parse(time);
            mes = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mes;
    }
}
