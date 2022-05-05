package com.xiliulou.afterserver.util;

import com.alibaba.fastjson.JSONObject;
import com.xiliulou.afterserver.constant.CommonConstants;
import com.xiliulou.afterserver.web.query.GeoCodeResultQuery;
import com.xiliulou.core.json.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zgw
 * @date 2022/5/5 15:36
 * @mood
 */
public class GaoDeMapUtil {
    private static final String GAO_DE_KEY = "75f594346e35dced0bf957474ea5ae89";

    /**
     * 功能描述: 地址转换为经纬度
     *
     * @return java.lang.String 经纬度
     * @author isymikasan
     * @date 2022-01-26 09:17:13
     */
    public static GeoCodeResultQuery getLonLat(String address) {
        // 返回输入地址address的经纬度信息, 格式是 经度,纬度
        String queryUrl = String.format(CommonConstants.GEOCODE_URL, GAO_DE_KEY, address);
        // 高德接口返回的是JSON格式的字符串
        String queryResult = getResponse(queryUrl);
        GeoCodeResultQuery geoCodeResultQuery = JsonUtil.fromJson(queryResult, GeoCodeResultQuery.class);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
