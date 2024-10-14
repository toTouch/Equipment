package com.xiliulou.afterserver.util.device.registration;

import com.google.common.util.concurrent.RateLimiter;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.xiliulou.afterserver.config.ProductConfig;
import com.xiliulou.afterserver.service.retrofit.SaasTCPDeviceSolutionService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.device.DeviceBase;
import com.xiliulou.afterserver.util.device.EleDeviceCodeRegisterQuery;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SaasTCPDeviceSolutionUtil {
    
    @Autowired
    private ProductConfig productConfig;
    
    // 令牌桶
    public static final RateLimiter TOKEN_BUCKET_50 = RateLimiter.create(50.0);
    
    /**
     * 获取IoTDAClient实例
     *
     * @link <a href="https://support.huaweicloud.com/devg-apisign/api-sign-provide-aksk.html"> 获取ak、sk实例 </a>
     */
    private SaasTCPDeviceSolutionService getIoTDAClient() {
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        //自定义OkHttpClient
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        //添加拦截器
        okHttpClient.addInterceptor(httpLoggingInterceptor);
        
        //创建并指定自定义的OkHttpClient
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ele.xiliulou.com/electricityCabinet/").addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build()).build();
        return retrofit.create(SaasTCPDeviceSolutionService.class);
    }
    
    /**
     * 查询指定设备详情
     */
    public DeviceBase queryDeviceDetail(String deviceName, String productKey) {
        SaasTCPDeviceSolutionService client = getIoTDAClient();
        DeviceBase data = new DeviceBase("queryDeviceDetail:" + System.currentTimeMillis(), productKey, deviceName);
        log.info("deviceInfo request parameters: {}", data);
        
        Call<R> call = client.deviceInfo(data);
        try {
            Response<R> execute = call.execute();
            if (execute.isSuccessful()) {
                log.info("request success, response: {}", execute.body());
                if ((Objects.isNull(execute) || Objects.isNull(execute.body()))) {
                    return new DeviceBase();
                }
                
                R result = (R) execute.body();
                Map<String, Object> dataMap = (Map<String, Object>) result.getData();
                
                // 创建 DeviceBase 对象并手动设置字段
                DeviceBase deviceBase = new DeviceBase();
                deviceBase.setDeviceName((String) dataMap.get("deviceName"));
                deviceBase.setProductKey((String) dataMap.get("productKey"));
                deviceBase.setDeviceSecret((String) dataMap.get("secret"));
                return deviceBase;
            } else {
                log.error("request failed, status code: {}, message: {}", execute.code(), execute.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DeviceBase();
    }
    
    /**
     * 批量注册设备
     */
    public Pair<Boolean, String> batchRegisterDevice(Set<String> deviceNames, String productKey) {
        // 设备是否存在校验
        SaasTCPDeviceSolutionService client = getIoTDAClient();
        Triple<Boolean, Object, String> booleanObjectStringTriple = devicePresenceVerification(deviceNames, client);
        if (booleanObjectStringTriple.getLeft()) {
            return Pair.of(false, booleanObjectStringTriple.getMiddle() + "设备已存在");
        }
        
        ArrayList<DeviceBase> deviceBases = new ArrayList<>();
        for (String deviceName : deviceNames) {
            DeviceBase data = new DeviceBase();
            data.setDeviceName(deviceName);
            data.setProductKey(productKey);
            deviceBases.add(data);
        }
        
        EleDeviceCodeRegisterQuery registerQuery = new EleDeviceCodeRegisterQuery("batchRegisterDevice:" + System.currentTimeMillis(), deviceBases);
        log.info("register device request parameters: {}", registerQuery);
        try {
            Call<ResponseBody> response = client.deviceRegister(registerQuery);
            response.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        log.info("response: {}", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    throw new RuntimeException("注册三元组失败，请重新生成批次");
                }
            });
            TOKEN_BUCKET_50.acquire();
        } catch (Exception e) {
            e.printStackTrace();
            return Pair.of(false, "注册三元组失败，请重新生成批次");
        }
        return Pair.of(true, null);
    }
    
    /**
     * 设备是否存在验证 批量
     *
     * @param deviceNames 设备名称
     * @param client
     * @return
     */
    public Triple<Boolean, Object, String> devicePresenceVerification(Set<String> deviceNames, SaasTCPDeviceSolutionService client) {
        
        EleDeviceCodeRegisterQuery data = new EleDeviceCodeRegisterQuery(System.currentTimeMillis() + "", "a1QqoBrbcT1", deviceNames);
        log.info("deviceInfo request parameters: {}", data);
        
        Call<R> call = client.getDeviceInfos(data);
        try {
            Response<R> execute = call.execute();
            if (execute.isSuccessful()) {
                R result = (R) execute.body();
                log.info("request success, response: {}", result);
                // 输出数据类型
                if (result.getData() instanceof List) {
                    List<Map<String, Object>> resultData = (List<Map<String, Object>>) result.getData();
                    List<String> collected = resultData.stream().map(e -> e.get("deviceName") == null ? "" : e.get("deviceName").toString()).collect(Collectors.toList());
                    log.info("DATA ALREADY EXISTS: {}", collected);
                    if (CollectionUtils.isEmpty(collected)) {
                        return Triple.of(false, null, null);
                    }
                    return Triple.of(true, collected, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Triple.of(false, null, null);
    }
    
}
