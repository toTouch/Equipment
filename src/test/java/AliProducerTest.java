import com.aliyun.mns.common.ClientException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.xiliulou.afterserver.AfterServerApplication;
import com.xiliulou.iot.config.IotConfig;
import com.xiliulou.iot.service.RegisterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.rmi.ServerException;
import java.util.HashSet;
import java.util.Set;

//@SpringBootTest(classes = AfterServerApplication.class)
//@RunWith(SpringRunner.class)
@ActiveProfiles("loc")
// @ActiveProfiles("dev")
@Slf4j
public class AliProducerTest {
    
    @Autowired
    private IotConfig iotConfig;
    
    @Test
    public void tt() throws Exception {
        
        DefaultProfile profile = DefaultProfile.getProfile(iotConfig.getRegionId(), iotConfig.getAccessKey(), iotConfig.getAccessSecret());
        
        IAcsClient client = new DefaultAcsClient(profile);
        
        CommonRequest request = new CommonRequest();
        
        request.setMethod(MethodType.POST);
        request.setDomain("iot.cn-shanghai.aliyuncs.com");
        request.setVersion("2018-01-20");
        request.setAction("QueryProductList");
        request.putQueryParameter("PageSize", "10");
        request.putQueryParameter("CurrentPage", "1");
        
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void batchCheckDeviceNamesTest1() {
        
        Set<String> s = new HashSet<>();
        s.add("1234");
        s.add("1235");
        
        RegisterDeviceService registerDeviceService = new RegisterDeviceService(iotConfig);
        System.out.println("===================== API换电柜  批量检查自定义设备名称的合法性 ==========================");
        Long result = registerDeviceService.batchCheckDeviceNames("a1aru3g173j", s);
        System.out.println(result);
        System.out.println("===================== 换电柜 批量检查自定义设备名称的合法性 ==========================");
        Long result1 = registerDeviceService.batchCheckDeviceNames("a1QqoBrbcT1", s);
        System.out.println(result1);
    }
    
    @Test
    public void finalTest() {
        System.out.println("hello world");
        try {
            System.out.println("hello world  111");
            return;
        }finally {
            System.out.println("hello world  222");
        }
    }
}
