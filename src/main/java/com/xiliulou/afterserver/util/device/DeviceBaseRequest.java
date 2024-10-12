package com.xiliulou.afterserver.util.device;


import com.xiliulou.afterserver.util.SignUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class DeviceBaseRequest {
    
    
    private static final String APP_ID = "afterSale";
    
    public static final String APP_SECRET = "asegfHVZfFLTa0buIAUvU3VoidGdlERz+lf1HqVfR4s=";
    
    public static final String VERSION = "1.0.0";
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 请求时间
     */
    private Long requestTime;
    
    /**
     * 请求Id
     */
    private String requestId;
    
    /**
     * appId
     */
    private String appId;
    
    /**
     * 生成的签名
     */
    private String sign;
    
    /**
     * 附带的数据
     */
    private String data;
    
    public DeviceBaseRequest(String requestId) {
        this.requestId = requestId;
        this.data = data;
        
        this.version =VERSION;
        this.requestTime = System.currentTimeMillis();
        this.appId = APP_ID;
        try {
            this.sign = SignUtils.getSaasSignature(requestId, appId, requestTime, version, APP_SECRET);
        } catch (Exception e) {
            log.error("sign error", e);
        }
    }
}
