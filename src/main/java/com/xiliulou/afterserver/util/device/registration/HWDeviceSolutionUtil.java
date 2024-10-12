package com.xiliulou.afterserver.util.device.registration;

import com.google.common.util.concurrent.RateLimiter;
import com.huaweicloud.sdk.core.auth.AbstractCredentials;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.core.region.Region;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.AddDevice;
import com.huaweicloud.sdk.iotda.v5.model.AddDeviceRequest;
import com.huaweicloud.sdk.iotda.v5.model.AddDeviceResponse;
import com.huaweicloud.sdk.iotda.v5.model.ListDevicesRequest;
import com.huaweicloud.sdk.iotda.v5.model.ListDevicesResponse;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceRequest;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceResponse;
import com.huaweicloud.sdk.iotda.v5.region.IoTDARegion;
import com.xiliulou.afterserver.config.ProductConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Component
public class HWDeviceSolutionUtil {
    
    @Autowired
    private ProductConfig productConfig;
    
    // 令牌桶
    public static final RateLimiter TOKEN_BUCKET_50 = RateLimiter.create(50.0);
    
    /**
     * 获取IoTDAClient实例
     *
     * @link <a href="https://support.huaweicloud.com/devg-apisign/api-sign-provide-aksk.html"> 获取ak、sk实例 </a>
     */
    private IoTDAClient getIoTDAClient() {
        return getIoTDAClient(productConfig.getHuaweiAccessKey(), productConfig.getHuaweiAccessSecret(), null, productConfig.getEndpoint());
    }
    
    /**
     * 获取IoTDAClient实例列表
     *
     * @param ak       Access Key id
     * @param sk       Secret Access Key
     * @param regionId 区域id
     * @return
     * @link <a href="https://support.huaweicloud.com/devg-apisign/api-sign-provide-aksk.html"> 获取ak、sk实例 </a>
     */
    private IoTDAClient getIoTDAClient(String ak, String sk, String regionId) {
        regionId = StringUtils.isEmpty(regionId) ? "cn-north-4" : regionId;
        ICredential auth = new BasicCredentials().withDerivedPredicate(AbstractCredentials.DEFAULT_DERIVED_PREDICATE) // Used in derivative ak/sk authentication scenarios
                .withAk(ak).withSk(sk);
        
        return IoTDAClient.newBuilder().withCredential(auth).withRegion(IoTDARegion.valueOf(regionId)).build();
    }
    
    private IoTDAClient getIoTDAClient(String ak, String sk, String regionId, String endpoint) {
        regionId = StringUtils.isEmpty(regionId) ? "cn-north-4" : regionId;
        ICredential auth = new BasicCredentials().withDerivedPredicate(AbstractCredentials.DEFAULT_DERIVED_PREDICATE) // Used in derivative ak/sk authentication scenarios
                .withAk(ak).withSk(sk);
        
        return IoTDAClient.newBuilder().withCredential(auth).withRegion(new Region(regionId, endpoint)).build();
    }
    
    /**
     * 查询设备详情
     */
    public ShowDeviceResponse queryDeviceDetail(IoTDAClient client, String deviceName) {
        try {
            ShowDeviceRequest request = new ShowDeviceRequest();
            request.setDeviceId(deviceName);
            ShowDeviceResponse response = client.showDevice(request);
            System.out.println("================ response =================");
            System.out.println(response.toString());
            // System.out.println(response.get);
            return response;
        } catch (ConnectionException | RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getRequestId());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
        return null;
    }
    
    public ShowDeviceResponse queryDeviceDetail(String productKey, String deviceName) {
        IoTDAClient ioTDAClient = getIoTDAClient();
        try {
            ShowDeviceRequest request = new ShowDeviceRequest();
            request.setDeviceId(deviceName);
            ShowDeviceResponse response = ioTDAClient.showDevice(request);
            System.out.println("================ response =================");
            System.out.println(response.toString());
            // System.out.println(response.get);
            return response;
        } catch (ConnectionException | RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getRequestId());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
        return null;
    }
    
    /**
     * 注册设备
     *
     * @param productKey 产品KEY
     * @param deviceName 设备名称
     */
    public void registerDevice(IoTDAClient client, String productKey, String deviceName) {
        AddDeviceRequest request = getAddDeviceRequest();
        AddDevice body = new AddDevice();
        
        body.withDeviceName(deviceName); // F 设备名称 要传进来的参数
        body.withDeviceId(deviceName); // F 设备ID
        body.withNodeId(deviceName);  // T 设备标识码，通常使用IMEI、MAC地址或Serial No作为node_id   -- deviceName
        body.withProductId(productKey); // T 设备关联的产品ID，用于唯一标识一个产品模型  -- productKey
        request.withBody(body);
        
        try {
            AddDeviceResponse response = client.addDevice(request);
            System.out.println("================ response =================");
            System.out.println(response.toString());
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getRequestId());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
    }
    
    private static AddDeviceRequest getAddDeviceRequest() {
        AddDeviceRequest request = new AddDeviceRequest();
        return request;
    }
    
    /**
     * 批量注册设备
     */
    public Pair<Boolean, String> batchRegisterDevice(Set<String> deviceNames, String productKey) {
        IoTDAClient client = getIoTDAClient();
        try {
            Triple<Boolean, Object, String> booleanObjectStringTriple = devicePresenceVerification(deviceNames, client);
            if (booleanObjectStringTriple.getLeft()) {
                return Pair.of(false, booleanObjectStringTriple.getMiddle() + "设备已存在");
            }
            deviceNames.parallelStream().forEach(deviceName -> {
                registerDevice(client, productKey, deviceName);
                TOKEN_BUCKET_50.acquire();
            });
        } catch (ConnectionException | RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getRequestId());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
        return Pair.of(true, null);
    }
    
    /**
     * 设备是否存在验证
     *
     * @param deviceNames 设备名称
     * @param client
     * @return
     */
    private Triple<Boolean, Object, String> devicePresenceVerification(Set<String> deviceNames, IoTDAClient client) {
        Set<String> deviceAlreadyExists = new HashSet<>();
        deviceNames.parallelStream().forEach(deviceName -> {
            ShowDeviceResponse showDeviceResponse = queryDeviceDetail(client, deviceName);
            if (showDeviceResponse != null && showDeviceResponse.getDeviceName() != null) {
                deviceAlreadyExists.add(showDeviceResponse.getDeviceName());
            }
            TOKEN_BUCKET_50.acquire();
        });
        
        if (!CollectionUtils.isEmpty(deviceAlreadyExists)) {
            System.out.println("deviceAlreadyExists");
            return Triple.of(true, deviceAlreadyExists, "Device already exists!");
        }
        return Triple.of(false, null, null);
    }
    
    /**
     * 查询设备信息列表
     */
    public void queryDevice(String deviceName, String productKey) {
        IoTDAClient client = getIoTDAClient();
        ListDevicesRequest request = new ListDevicesRequest();
        request.withNodeId(deviceName); // T 设备标识码，通常使用IMEI、MAC地址或Serial No作为node_id   -- deviceName
        request.withProductId(productKey); // T 设备关联的产品ID，用于唯一标识一个产品模型  -- productKey
        
        try {
            ListDevicesResponse response = client.listDevices(request);
            System.out.println(response.toString());
            System.out.println(response.getDevices().toString());
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getRequestId());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
    }
    
    
}
