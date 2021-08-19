package com.xiliulou.afterserver.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String getDate(Long dates){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(dates))));
        return sd;
    }

    public static String getDateFormat(Long dates){
        SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(dates))));
        return sd;
    }


    public static void main(String[] args) {

        System.out.println(getNo());


    }

    public static String getNo(){
        String dateFormat = getDateFormat(System.currentTimeMillis());
        String[] split = dateFormat.split("-");
        String resout = "xll" + split[0] + split[1] + split[2] + (int) ((Math.random() * 9 + 1) * 100000);
        return  resout;
    }


}
