package com.xiliulou.afterserver;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zgw
 * @date 2022/5/5 10:32
 * @mood
 */
public class AmapApiTest {
    
    private static final String GAO_DE_KEY = "75f594346e35dced0bf957474ea5ae89";
    
    /**
     * 功能描述: 地址转换为经纬度
     *
     * @return java.lang.String 经纬度
     * @author isymikasan
     * @date 2022-01-26 09:17:13
     */
    @Test
    public void getLonLat() {
        
        try {
            // 返回输入地址address的经纬度信息, 格式是 经度,纬度
            String queryLonLatUrl = "http://restapi.amap.com/v3/geocode/geo?key=%s&address=%s&batch=true";
            String queryUrl = String.format(queryLonLatUrl, GAO_DE_KEY, "北京市海淀区中关村街道中关村东路金五星科技文化园区");
            // 高德接口返回的是JSON格式的字符串
            String queryResult = getResponse(queryUrl);
            System.out.println(queryResult);
            JSONObject job = JSONObject.parseObject(queryResult);
            JSONObject jobJSON = JSONObject.parseObject(job.get("geocodes").toString().substring(1, job.get("geocodes").toString().length() - 1));
            String LngAndLat = jobJSON.get("location").toString();
            
            System.out.println(LngAndLat);
        } catch (Exception e) {
        
        }
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
