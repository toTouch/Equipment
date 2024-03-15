import com.huaweicloud.sdk.core.auth.AbstractCredentials;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.AddDevice;
import com.huaweicloud.sdk.iotda.v5.model.AddDeviceRequest;
import com.huaweicloud.sdk.iotda.v5.model.AddDeviceResponse;
import com.huaweicloud.sdk.iotda.v5.model.ListDevicesRequest;
import com.huaweicloud.sdk.iotda.v5.model.ListDevicesResponse;
import com.huaweicloud.sdk.iotda.v5.model.SearchDevicesRequest;
import com.huaweicloud.sdk.iotda.v5.model.SearchDevicesResponse;
import com.huaweicloud.sdk.iotda.v5.model.SearchSql;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceRequest;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceResponse;
import com.huaweicloud.sdk.iotda.v5.region.IoTDARegion;
import com.xiliulou.afterserver.AfterServerApplication;
import com.xiliulou.afterserver.util.DeviceSolutionUtil;
import com.xiliulou.iot.entity.response.QueryDeviceDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = AfterServerApplication.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@Slf4j
public class DeviceSolution {
    
    String ak = "R0YOLDOWWEBZYUAGFQFP";  // System.getenv("CLOUD_SDK_AK");
    
    String sk = "gWcndZnssP8QslPFFy1AhSCxQUB98ZnZQO0Df1qH"; // System.getenv("CLOUD_SDK_SK");
    
    String productKey = "65f04c2d7bdccc0126c81902";
    /*
    POST /v5/iot/{project_id}/devices   project_id 	ecd4aae0a479487dbadddc67ee1e3d2f
    registerDevice
    
    */
    
    @Autowired
    DeviceSolutionUtil deviceSolutionUtil;
    
    @Test
    public void batchRegisterDevice() {
        Set<String> strings = new HashSet<>();
        strings.add("ABC123456780");
        Pair<Boolean, String> booleanStringPair = deviceSolutionUtil.batchRegisterDevice(strings, productKey);
        System.out.println(booleanStringPair);
    }
    
    /**
     * 注册设备
     */
    @Test
    public void registerDevice() {
        IoTDAClient ioTDAClient = getIoTDAClient(ak, sk);
        registerDevice(ioTDAClient, productKey, "ABC123456780");
    }
    
    /**
     * 查询设备信息列表
     */
    @Test
    public void queryDevice() {
        IoTDAClient client = getIoTDAClient(ak, sk);
        ListDevicesRequest request = new ListDevicesRequest();
        
        /*
        AuthInfo authInfobody = new AuthInfo();
        authInfobody.withAuthType("SECRET").withSecret("3b935a250c50dc2c6d481d048cefdc3c").withSecureAccess(true);
        List<InitialDesired> listbodyShadow = new ArrayList<>();
        listbodyShadow.add(new InitialDesired().withServiceId("WaterMeter").withDesired("{\"temperature\":\"60\"}"));
        body.withShadow(listbodyShadow); // F 设备初始配置。用户使用该字段可以为设备指定初始配置
        body.withAppId("jeQDJQZltU8iKgFFoW060F5SGZka"); //F 资源空间ID。此参数为非必选参数，存在多资源空间的用户需要使用该接口时，建议携带
        body.withDescription("watermeter device");  // F 设备的描述信息
        body.withAuthInfo(authInfobody); // F 参数说明：设备的接入认证信息。
        body.withDeviceName("dianadevice"); //F 设备名称 要传进来的参数
        body.withDeviceId("d4922d8a-6c8e-4396-852c-164aefa6638f"); // F 设备ID
        */
        // request.withNodeId("ABC123456780"); // T 设备标识码，通常使用IMEI、MAC地址或Serial No作为node_id   -- deviceName
        request.withProductId("65f04c2d7bdccc0126c81902"); // T 设备关联的产品ID，用于唯一标识一个产品模型  -- productKey
        
        try {
            ListDevicesResponse response = client.listDevices(request);
            System.out.println("===================== response ======================= ");
            System.out.println(response.toString());
            System.out.println("=============== response.getDevices() ================");
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
    
    /**
     * 获取IoTDAClient实例
     *
     * @param ak Access Key Id
     * @param sk Secret Access Key
     * @return
     * @link <a href="https://support.huaweicloud.com/devg-apisign/api-sign-provide-aksk.html"> 获取ak、sk实例 </a>
     */
    private IoTDAClient getIoTDAClient(String ak, String sk) {
        return getIoTDAClient(ak, sk, null);
    }
    
    /**
     * 注册设备
     *
     * @param productKey 产品KEY
     * @param deviceName 设备名称
     */
    public void registerDevice(IoTDAClient client, String productKey, String deviceName) {
        AddDeviceRequest request = new AddDeviceRequest();
        AddDevice body = new AddDevice();
        /*
        AuthInfo authInfobody = new AuthInfo();
        authInfobody.withAuthType("SECRET").withSecret("3b935a250c50dc2c6d481d048cefdc3c").withSecureAccess(true);
        List<InitialDesired> listbodyShadow = new ArrayList<>();
        listbodyShadow.add(new InitialDesired().withServiceId("WaterMeter").withDesired("{\"temperature\":\"60\"}"));
        body.withShadow(listbodyShadow); // F 设备初始配置。用户使用该字段可以为设备指定初始配置
        body.withAppId("jeQDJQZltU8iKgFFoW060F5SGZka"); //F 资源空间ID。此参数为非必选参数，存在多资源空间的用户需要使用该接口时，建议携带
        body.withDescription("watermeter device");  // F 设备的描述信息
        body.withAuthInfo(authInfobody); // F 参数说明：设备的接入认证信息。
        */
        
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
        
        IoTDAClient client = IoTDAClient.newBuilder().withCredential(auth).withRegion(IoTDARegion.valueOf(regionId)).build();
        return client;
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
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println("=================================");
            System.out.println(e.getRequestId());
            System.out.println("=================================");
            System.out.println(e.getErrorCode());
            System.out.println("=================================");
            System.out.println(e.getErrorMsg());
        }
        return null;
    }
    
    /**
     * 批量注册设备
     *
     *
     */
    public Pair<Boolean, String> batchRegisterDevice(ArrayList<String> deviceNames, String productKey) {
        IoTDAClient client = getIoTDAClient(ak, sk);
        try {
            Triple<Boolean, Object, String> booleanObjectStringTriple = devicePresenceVerification(deviceNames, client);
            if (booleanObjectStringTriple.getLeft()) {
                return Pair.of(false, booleanObjectStringTriple.getMiddle() + "设备已存在");
            }
            deviceNames.parallelStream().forEach(deviceName -> {
                registerDevice(client, productKey, deviceName);
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
    private Triple<Boolean, Object, String> devicePresenceVerification(ArrayList<String> deviceNames, IoTDAClient client) {
        ArrayList<String> deviceAlreadyExists = new ArrayList<>();
        deviceNames.forEach(deviceName -> {
            ShowDeviceResponse showDeviceResponse = queryDeviceDetail(client, deviceName);
            if (showDeviceResponse != null && showDeviceResponse.getDeviceName() != null) {
                deviceAlreadyExists.add(showDeviceResponse.getDeviceName());
            }
        });
        
        if (!CollectionUtils.isEmpty(deviceAlreadyExists)) {
            System.out.println("deviceAlreadyExists");
            return Triple.of(true, deviceAlreadyExists, "Device already exists!");
        }
        return Triple.of(false, null, null);
    }
    
    /**
     * 灵活搜索设备列表
     */
    @Test
    @Disabled()
    public void queryDeviceList() {
        
        IoTDAClient client = getIoTDAClient(ak, sk);
        SearchDevicesRequest request = new SearchDevicesRequest();
        SearchSql body = new SearchSql();
        
        ArrayList<String> deviceNames = new ArrayList<>();
        deviceNames.add("ABC123456780");
        deviceNames.add("ABC123456789");
        
        String query = splicingDeviceNameQuerySQL(deviceNames);
        body.withSql(query);
        request.withBody(body);
        try {
            SearchDevicesResponse response = client.searchDevices(request);
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
    
    /**
     * 拼接设备名称查询sql
     *
     * @param deviceNames 设备名称列表
     * @return sql
     */
    private String splicingDeviceNameQuerySQL(ArrayList<String> deviceNames) {
        StringBuilder inClauseBuilder = new StringBuilder();
        for (int i = 0; i < deviceNames.size(); i++) {
            if (i > 0) {
                inClauseBuilder.append(", ");
            }
            inClauseBuilder.append("'").append(deviceNames.get(i)).append("'");
        }
        return "SELECT * FROM device WHERE node_id IN (" + inClauseBuilder + ")";
    }
    
    
    @Test
    public void queryDeviceDetail() {
        IoTDAClient client = getIoTDAClient(ak, sk);
        try {
            ShowDeviceRequest request = new ShowDeviceRequest();
            request.setDeviceId("hw0001");
            ShowDeviceResponse response = client.showDevice(request);
            System.out.println("================ response =================");
            System.out.println(response.toString());
            // System.out.println(response.get);
            // response.getDeviceName()
            QueryDeviceDetailResult queryDeviceDetailResult=new QueryDeviceDetailResult();
            queryDeviceDetailResult.setDeviceName(response.getDeviceName());
            // queryDeviceDetailResult.setProductKey(response.());
            //
            // queryDeviceDetailResult.setDeviceSecret(response.getDeviceSecret());
            // queryDeviceDetailResult.setDeviceSecret(response.getDeviceSecret());
        } catch (ConnectionException | RequestTimeoutException e) {
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
