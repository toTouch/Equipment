package com.xiliulou.afterserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Hardy
 * @date 2021/3/31 0031 15:50
 * @Description:
 */
public class DateUtils {


    /**
     * 获取两个时间差
     * @param endTime
     * @param startTime
     * @return
     */
    public static String getDatePoor(Long endTime, Long startTime) {

        if(Objects.isNull(endTime) || Objects.isNull(startTime) || endTime - startTime <= 0){
            return "0天0小时0分钟";
        }

        long diff = endTime - startTime;

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;

        long day = diff / nd;

        long hour = diff % nd / nh;

        long min = diff % nd % nh / nm;

        /*if (day == 0 && hour == 0){
            return min + "分钟";
        }

        if (day == 0){
            return hour + "小时" + min + "分钟";
        }*/

        return day + "天" + hour + "小时" + min + "分钟";
    }


    /***
     * 传入一个天数返回天数的时间戳
     */
    public static long daysToStamp(int days){
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY,0);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);
        instance.add(Calendar.DAY_OF_MONTH, days);

        return instance.getTime().getTime();


    }


    /**
     * //指定日期加上指定天数得到新日期
     */
    public static Long getNewDay(Long currentTime,int days)  {
        return currentTime + days * 24 * 60 * 60 * 10000L;
    }


    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToTime(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 获取当月开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getMonthStartTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


}
