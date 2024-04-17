import com.huaweicloud.sdk.core.auth.AbstractCredentials;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.core.region.Region;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.ListProductsRequest;
import com.huaweicloud.sdk.iotda.v5.model.ListProductsResponse;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceRequest;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceResponse;
import com.xiliulou.afterserver.AfterServerApplication;
import com.xiliulou.afterserver.config.ProductConfig;
import com.xiliulou.afterserver.util.DeviceSolutionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = AfterServerApplication.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("loc")
// @ActiveProfiles("dev")
@Slf4j
public class DeviceSolutionAPItest {
    
    String ak = "JBNJ2QSPGIQ7QFNTWWJS";  // System.getenv("CLOUD_SDK_AK");
    
    String sk = "taNpnKa1fRcmruUYMQgnIHQjlsuKkcTPpULcUMcX"; // System.getenv("CLOUD_SDK_SK");
    
    String productKey = "a1mqS72fHNi";
    // String productKey = "65f04c2d7bdccc0126c81902";
    /*
    POST /v5/iot/{project_id}/devices   project_id 	ecd4aae0a479487dbadddc67ee1e3d2f
    registerDevice
    
    */
    
    @Autowired
    DeviceSolutionUtil deviceSolutionUtil;
    
    @Autowired
    private ProductConfig productConfig;
    
    /**
     * 批量创建设备 接口测试
     */
    @Test
    public void batchRegisterDevice() {
        Set<String> strings = new HashSet<>();
        strings.add("zbzcssss001");
        // strings.add("11ABC123456780");
        Pair<Boolean, String> booleanStringPair = deviceSolutionUtil.batchRegisterDevice(strings, productConfig.getHuaweiKey());
        System.out.println(booleanStringPair);
    }
    
    /**
     * 查询产品列表
     */
    @Test
    public void queryProductList() {
        IoTDAClient ioTDAClient = getIoTDAClient();
        System.out.println("================ 查询产品列表 ================");
        ListProductsRequest request = new ListProductsRequest();
        request.withLimit(10);
        // request.withMarker("<marker>");
        // request.withAppId("<app_id>");
        // request.withProductName("<product_name>");
        request.withOffset(0);
        try {
            ListProductsResponse response = ioTDAClient.listProducts(request);
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
     * 查询设备详情
     */
    @Test
    public void queryDeviceDetail() {
        IoTDAClient ioTDAClient = getIoTDAClient();
        System.out.println("================ 查询设备详情 ================");
        queryDeviceDetail(ioTDAClient, "zbzcssss001");
    }
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
    
    /**
     * 获取客户端 getIoTDAClient
     */
    private IoTDAClient getIoTDAClient() {
        return getIoTDAClient(productConfig.getHuaweiAccessKey(), productConfig.getHuaweiAccessSecret(), null, productConfig.getEndpoint());
    }
    
    private IoTDAClient getIoTDAClient(String ak, String sk, String regionId, String endpoint) {
        regionId = StringUtils.isEmpty(regionId) ? "cn-north-4" : regionId;
        ICredential auth = new BasicCredentials().withDerivedPredicate(AbstractCredentials.DEFAULT_DERIVED_PREDICATE) // Used in derivative ak/sk authentication scenarios
                .withAk(ak).withSk(sk);
        
        return IoTDAClient.newBuilder().withCredential(auth).withRegion(new Region(regionId, endpoint)).build();
    }
}
