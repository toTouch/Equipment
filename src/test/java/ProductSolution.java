import com.huaweicloud.sdk.core.auth.AbstractCredentials;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.AddProduct;
import com.huaweicloud.sdk.iotda.v5.model.CreateProductRequest;
import com.huaweicloud.sdk.iotda.v5.model.CreateProductResponse;
import com.huaweicloud.sdk.iotda.v5.model.ServiceCapability;
import com.huaweicloud.sdk.iotda.v5.model.ServiceCommand;
import com.huaweicloud.sdk.iotda.v5.model.ServiceCommandPara;
import com.huaweicloud.sdk.iotda.v5.model.ServiceCommandResponse;
import com.huaweicloud.sdk.iotda.v5.model.ServiceProperty;
import com.huaweicloud.sdk.iotda.v5.region.IoTDARegion;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSolution {
    
    String ak = "R0YOLDOWWEBZYUAGFQFP";  // System.getenv("CLOUD_SDK_AK");
    
    String sk = "gWcndZnssP8QslPFFy1AhSCxQUB98ZnZQO0Df1qH"; // System.getenv("CLOUD_SDK_SK");
    
    /**
     * 获取IoTDAClient实例
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
     * 创建产品
     */
    
    public void createProduct() {
        IoTDAClient ioTDAClient = getIoTDAClient(ak, sk, "cn-north-4");
        CreateProductRequest request = new CreateProductRequest();
        AddProduct body = new AddProduct();
        List<ServiceCommandPara> listResponsesParas = new ArrayList<>();
        listResponsesParas.add(
                new ServiceCommandPara().withParaName("force").withDataType("string").withRequired(false).withMin("1").withMax("100").withMaxLength(100).withStep((double) 0.1)
                        .withUnit("km/h").withDescription("force"));
        List<ServiceCommandResponse> listCommandsResponses = new ArrayList<>();
        listCommandsResponses.add(new ServiceCommandResponse().withResponseName("ACK").withParas(listResponsesParas));
        List<ServiceCommandPara> listCommandsParas = new ArrayList<>();
        listCommandsParas.add(
                new ServiceCommandPara().withParaName("force").withDataType("string").withRequired(false).withMin("1").withMax("100").withMaxLength(100).withStep((double) 0.1)
                        .withUnit("km/h").withDescription("force"));
        List<ServiceCommand> listServiceCapabilitiesCommands = new ArrayList<>();
        listServiceCapabilitiesCommands.add(new ServiceCommand().withCommandName("reboot").withParas(listCommandsParas).withResponses(listCommandsResponses));
        List<ServiceProperty> listServiceCapabilitiesProperties = new ArrayList<>();
        listServiceCapabilitiesProperties.add(
                new ServiceProperty().withPropertyName("temperature").withDataType("decimal").withRequired(true).withMin("1").withMax("100").withMaxLength(100)
                        .withStep((double) 0.1).withUnit("centigrade").withMethod("RW").withDescription("force").withDefaultValue("{\"color\":\"red\",\"size\":1}"));
        List<ServiceCapability> listbodyServiceCapabilities = new ArrayList<>();
        listbodyServiceCapabilities.add(new ServiceCapability()
                .withServiceId("temperature,") // T 服务IDsize < 64，只允许中文、字母、数字及_?'#().,&%@!-等字符的组合。
                .withServiceType("temperature")); // T 设备的服务类型
              
        body.withAppId("jeQDJQZltU8iKgFFoW060F5SGZka"); // F 资源空间ID。此参数为非必选参数
        body.withDescription("this is a thermometer produced by Huawei"); // F 产品描述 size < 128
        body.withIndustry("New energy for electricity"); // F 产品所属行业 新能源换电
        body.withManufacturerName("XILIULOU"); // F 产品厂商名称
        body.withServiceCapabilities(listbodyServiceCapabilities); // T 设备的服务能力列表
        body.withDataFormat("json"); // T 数据格式
        body.withProtocolType("MQTT"); // T 产品协议类型
        body.withDeviceType("ElectricCabinet"); // T 产品设备类型
        body.withName("Thermometer,"); // T 产品名称 size < 64，只允许中文、字母、数字及_?'#().,&%@!-等字符的组合。
        body.withProductId("5ba24f5ebbe8f56f5a14f605"); // F 产品ID
        request.withBody(body);
        try {
            CreateProductResponse response = ioTDAClient.createProduct(request);
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
}
