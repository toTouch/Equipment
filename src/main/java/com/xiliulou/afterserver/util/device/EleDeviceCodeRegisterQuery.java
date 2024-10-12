package com.xiliulou.afterserver.util.device;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class EleDeviceCodeRegisterQuery extends DeviceBaseRequest {
    
    private String productKey;
    
    private List<DeviceBase> deviceNames;
    
    private Set<String> deviceNameSet;
    
    public EleDeviceCodeRegisterQuery(String requestId, List<DeviceBase> deviceNames) {
        super(requestId);
        this.deviceNames = deviceNames;
    }
    
    public EleDeviceCodeRegisterQuery(String requestId, String productKey, Set<String> deviceNameSet) {
        super(requestId);
        this.deviceNameSet = deviceNameSet;
        this.productKey = productKey;
    }
}
