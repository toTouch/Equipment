import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiliulou.afterserver.util.device.DeviceBaseRequest;
import com.xiliulou.afterserver.util.device.DeviceBase;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.lang.Thread.sleep;

@Slf4j
public class okhttpTest {
    @Test
    public void deviceRegister() throws IOException {
        // 构造请求体
        DeviceBase data = new DeviceBase("202012091412","a1X6h2h6yh","test001");
        
        ArrayList<DeviceBase> deviceData = new ArrayList<>();
        
        ObjectMapper objectMapper = new ObjectMapper();
        deviceData.add(data);

        String json = objectMapper.writeValueAsString(data);
        System.out.println(json);
        
        
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url("https://ele.xiliulou.com/electricityCabinet/outer/afterSale/device/register")
                .method("POST", body)
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Accept", "*/*")
                                .addHeader("Host", "ele.xiliulou.com")
                                .addHeader("Connection", "keep-alive")
                .build();
        System.out.println("================ call =================");
        okhttp3.Response response = client.newCall(request).execute();
        System.out.println(response);
        System.out.println("================ response =================");
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                System.out.println(e);
            }
            
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                System.out.println(response);
            }
        });
        
    }
    
    @Test
    public void deviceInfo() throws Exception {
        // 构造请求体
        DeviceBase data = new DeviceBase("202012091412","a1X6h2h6yh","test001");

        
        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(data);
        System.out.println(json);
        
        System.out.println("================ 签名 =================");
        System.out.println(
                getSignature(data.getAppId(), data.getRequestTime(), data.getRequestId(), data.getVersion(), DeviceBaseRequest.APP_SECRET));
        
        
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json );
        Request request = new Request.Builder()
                .url("https://ele.xiliulou.com/electricityCabinet/outer/afterSale/device/info")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Host", "ele.xiliulou.com")
                .addHeader("Connection", "keep-alive")
                .build();
     
        System.out.println("================ call =================");
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
  
        System.out.println("================ response =================");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response);
                // 处理响应数据
                String responseBody = response.body().string();
                log.info("responseBody:", responseBody);
                System.out.println(responseBody);
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                // 处理请求失败情况
                e.printStackTrace();
                log.error(e.getMessage());
            }
        });
        sleep(3000);
    }
    
    public String getSignature(String appId,Long requestTime,String requestId,String version, String appSecret) throws Exception {        Map<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        map.put("requestTime", requestTime);
        map.put("requestId", requestId);
        map.put("version", version);
        
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, Object> sortedParams = new TreeMap<>(map);
        Set<Map.Entry<String, Object>> entrySet = sortedParams.entrySet();
        
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder stringToSign = new StringBuilder();
        for (Map.Entry<String, Object> param : entrySet) {
            stringToSign.append(param.getKey()).append("=").append(param.getValue()).append(",");
        }
        stringToSign.deleteCharAt(stringToSign.length() - 1);
        return calSignature(appSecret, stringToSign.toString());
    }
    final String ALGORITHM = "HmacSHA256";
    
    final String CHARSETS_UTF8 = "UTF-8";
    private String calSignature(String appSecret, String dataToSign) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Mac sha256HMAC = Mac.getInstance(ALGORITHM);
        sha256HMAC.init(secretKeySpec);
        byte[] hmacResult = sha256HMAC.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
        Base64.Encoder encoder = Base64.getUrlEncoder();
        byte[] base64Result = encoder.encode(hmacResult);
        return new String(base64Result);
    }
    
    
    @Test
    public void test() throws IOException {
    
    }
}
