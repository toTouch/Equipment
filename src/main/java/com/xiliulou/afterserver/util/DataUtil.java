package com.xiliulou.afterserver.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String getDate(Long dates){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(dates))));
        return sd;
    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        System.out.println(l);
        String date = getDate(l);
        System.out.println(date);
    }
}
