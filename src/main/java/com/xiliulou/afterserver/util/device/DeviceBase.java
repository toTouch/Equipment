package com.xiliulou.afterserver.util.device;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceBase extends DeviceBaseRequest {
    private String productKey;
    private String deviceName;
    private String deviceSecret;
    
    public DeviceBase(String requestId, String productKey, String deviceName) {
        super(requestId);
        this.productKey = productKey;
        this.deviceName = deviceName;
    }
}
