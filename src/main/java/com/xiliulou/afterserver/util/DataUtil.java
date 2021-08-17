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

        String date = getDate(System.currentTimeMillis());
        String[] s = date.split(" ");
        String s1 = s[0];
        System.out.println(s1.replaceAll("-", ""));



    }

    public static Long getNo(){
        String date = getDate(System.currentTimeMillis());
        String[] s = date.split(" ");
        String s1 = s[0];
        return Long.parseLong(s1.replaceAll("-", ""));
    }


}
