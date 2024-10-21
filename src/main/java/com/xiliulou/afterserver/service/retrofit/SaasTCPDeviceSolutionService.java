package com.xiliulou.afterserver.service.retrofit;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.device.DeviceBase;
import com.xiliulou.afterserver.util.device.DeviceBaseRequest;
import com.xiliulou.afterserver.util.device.EleDeviceCodeRegisterQuery;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

@RetrofitClient
public interface SaasTCPDeviceSolutionService {
    @POST("outer/afterSale/device/info")
    Call<R> deviceInfo(@Body DeviceBase request);
    
    @POST("outer/afterSale/device/register")
    Call<ResponseBody> deviceRegister(@Body EleDeviceCodeRegisterQuery request);
    
    @POST("outer/afterSale/device/info/batch")
    Call<R> getDeviceInfos(@Body EleDeviceCodeRegisterQuery request);
}
