import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiliulou.afterserver.service.retrofit.SaasTCPDeviceSolutionService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.device.DeviceBase;
import com.xiliulou.afterserver.util.device.EleDeviceCodeRegisterQuery;
import com.xiliulou.afterserver.util.device.registration.SaasTCPDeviceSolutionUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Slf4j
public class Example01 {
    
    public interface BlogService {
        
        @GET("app/device/getDeviceMessage")
        Call<ResponseBody> getBlog(@HeaderMap Map<String, String> headers, @Query("sn") String sn);
        
        @POST("outer/afterSale/device/info")
        Call<R> deviceInfo(@Body DeviceBase request);
        
        @POST("outer/afterSale/device/info/batch")
        Call<R> getDeviceInfos(@Body EleDeviceCodeRegisterQuery request);
        
        @POST("outer/afterSale/device/register")
        Call<ResponseBody> deviceRegister(@Body EleDeviceCodeRegisterQuery request);
        
        
    }
    
    private static BlogService getResult() {
        
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
        BlogService service = retrofit.create(BlogService.class);
        return service;
    }
    
    @Test
    public void deviceInfo() throws IOException, InterruptedException {
        // 构造请求体
        DeviceBase data = new DeviceBase("202012091412", "a1QqoBrbcT1", "test001");
        
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);
        System.out.println(json);
        
        BlogService client = getResult();
        System.out.println("================ call =================");
        Call<R> call = client.deviceInfo(data);
        try {
            Response<R> execute = call.execute();
            if (execute.isSuccessful()) {
                
                R result = (R) execute.body();
                System.out.println(result.getData());
                // 输出数据类型
                System.out.println("输出数据类型");
                System.out.println(result.getData().getClass());
                Map<String, Object> dataMap = (Map<String, Object>) result.getData();
                
                // 创建 DeviceBase 对象并手动设置字段
                DeviceBase deviceBase = new DeviceBase();
                deviceBase.setDeviceName((String) dataMap.get("deviceName"));
                deviceBase.setProductKey((String) dataMap.get("productKey"));
                deviceBase.setDeviceSecret((String) dataMap.get("secret"));
                
                
            } else {
                log.error("request failed, status code: {}, message: {}", execute.code(), execute.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Test
    public void getDeviceInfos() throws IOException, InterruptedException {
        String deviceName[] = {"test001", "test002", "test003"};
        // 转set
        Set<String> strings = new HashSet<>();
        Arrays.stream(deviceName).forEach(strings::add);
        EleDeviceCodeRegisterQuery data = new EleDeviceCodeRegisterQuery("202012091412", "a1QqoBrbcT1", strings);
        //        log.info("deviceInfo request parameters: {}", data);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);
        System.out.println(json);
        
        BlogService client = getResult();
        Call<R> call = client.getDeviceInfos(data);
        try {
            Response<R> execute = call.execute();
            if (execute.isSuccessful()) {
                System.out.println(execute);
                System.out.println(" ------ ");
                R result = (R) execute.body();
                System.out.println(result);
                // 输出数据类型
                System.out.println(result.getData().getClass());
                if (result.getData() instanceof List) {
                    System.out.println("=========");
                    List<Map<String, Object>> resultData = (List<Map<String, Object>>) result.getData();
                    List<String> collected = resultData.stream().map(e -> e.get("deviceName") == null ? "" : e.get("deviceName").toString()).collect(Collectors.toList());
                    
                    System.out.println(collected);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void deviceRegister() throws IOException, InterruptedException {
        //        BlogService result = getResult();
        //        Retrofit retrofit = new Retrofit.Builder()
        //                .baseUrl("https://tcupboard.xiliulou.com/after-service/")
        //                .addConverterFactory(GsonConverterFactory.create())
        //                .build();
        
        //创建并指定自定义的OkHttpClient
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ele.xiliulou.com/electricityCabinet/").addConverterFactory(GsonConverterFactory.create()).build();
        BlogService result = retrofit.create(BlogService.class);
        
        // 构造请求体
        DeviceBase data = new DeviceBase();
        data.setDeviceName("test003");
        data.setProductKey("a1QqoBrbcT1");
        data.setDeviceSecret("abababaab6666");
        ArrayList<DeviceBase> deviceBases = new ArrayList<>();
        
        ObjectMapper objectMapper = new ObjectMapper();
        deviceBases.add(data);
        EleDeviceCodeRegisterQuery registerQuery = new EleDeviceCodeRegisterQuery("202012091418", deviceBases);
        String json = objectMapper.writeValueAsString(registerQuery);
        System.out.println(json);
        
        Call<ResponseBody> call = result.deviceRegister(registerQuery);
        System.out.println("================ call =================");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println(response.body().string());
                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("================ onFailure =================");
                t.printStackTrace();
            }
        });
        sleep(2000);
    }
    
    @Test
    public void test() throws IOException {
        SaasTCPDeviceSolutionUtil solutionUtil = new SaasTCPDeviceSolutionUtil();
        System.out.println("================= 创建 ===================");
//        Pair<Boolean, String> booleanStringPair = solutionUtil.batchRegisterDevice(new HashSet<>(Arrays.asList("test004", "test005", "test006")), "a1QqoBrbcT1");
//        System.out.println(booleanStringPair);
        System.out.println("============= 查询 =============");
        System.out.println("====== test000         " + solutionUtil.queryDeviceDetail("test000", "a1QqoBrbcT1"));
        System.out.println("====== test004         " + solutionUtil.queryDeviceDetail("test004", "a1QqoBrbcT1"));
        System.out.println("====== test005         " + solutionUtil.queryDeviceDetail("test005", "a1QqoBrbcT1"));
        
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        //自定义OkHttpClient
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        //添加拦截器
//        okHttpClient.addInterceptor(httpLoggingInterceptor);
        
        //创建并指定自定义的OkHttpClient
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ele.xiliulou.com/electricityCabinet/").addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build()).build();
        System.out.println("================= 校验 ===================");
        System.out.println(
                solutionUtil.devicePresenceVerification(new HashSet<>(Arrays.asList("test004", "test005", "test006")), retrofit.create(SaasTCPDeviceSolutionService.class)));
        
        
    }
    
    public static void main(String[] args) throws IOException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://tcupboard.xiliulou.com/after-service/").addConverterFactory(GsonConverterFactory.create()).build();
        
        BlogService service = retrofit.create(BlogService.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("xll-sin-client-id", "after-sale");
        Call<ResponseBody> call = service.getBlog(headers, "XC-houzbzcs07240020001M");
        // 用法和OkHttp的call如出一辙
        // 不同的是如果是Android系统回调方法执行在主线程
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return;
    }
}
