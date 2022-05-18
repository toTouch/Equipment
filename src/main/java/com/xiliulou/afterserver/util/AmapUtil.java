package com.xiliulou.afterserver.util;

import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.web.query.GeoCodeResultQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zgw
 * @date 2022/5/17 14:36
 * @mood
 */
public class AmapUtil {
    private static final String GAO_DE_KEY = "38dd4fe518ef94b2247f31229f22bf38";

    /**
     * 功能描述: 地址转换为经纬度
     *
     * @return java.lang.String 经纬度
     * @author isymikasan
     * @date 2022-01-26 09:17:13
     */
    public static GeoCodeResultQuery getLonLat(String address) {
        final String GEOCODE_URL = "http://restapi.amap.com/v3/geocode/geo?key=%s&address=%s";
        // 返回输入地址address的经纬度信息, 格式是 经度,纬度
        String queryUrl = String.format(GEOCODE_URL, GAO_DE_KEY, address);
        // 高德接口返回的是JSON格式的字符串
        String queryResult = getResponse(queryUrl);
        GeoCodeResultQuery geoCodeResultQuery = JSON.parseObject(queryResult, GeoCodeResultQuery.class);
        return geoCodeResultQuery;
    }

    private static String getResponse(String serverUrl) {
        // 用JAVA发起http请求，并返回json格式的结果
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL(serverUrl);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println(serverUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
