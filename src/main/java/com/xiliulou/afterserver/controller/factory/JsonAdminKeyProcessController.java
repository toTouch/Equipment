package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.GET;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/5/23 14:16
 * @mood
 */
@RestController
@RequestMapping("/admin/factory")
@Slf4j
public class JsonAdminKeyProcessController {

    @Autowired
    private ProductNewService productNewService;

    @Autowired
    private AliyunOssService aliyunOssService;

    @Autowired
    private FileService fileService;

    /**
     * 校验资产编码是否存在
     */
    @GetMapping("check/productNo")
    public R getProductNewByNo(@RequestParam("no")String no){
        return productNewService.getProductNewByNo(no);
    }

    /**
     *电装检验合格
     */
    @PostMapping("electrical/qualified")
    public R electricalQualified(@RequestParam("no")String no){
        return productNewService.electricalQualified(no);
    }

    /**
     *出货检验回显
     */
    @GetMapping("delivery/qualified")
    public R deliveryQualified(@RequestParam("no")String no){
        return productNewService.deliveryQualified(no);
    }

    /**
     * 出货检验合格
     */
    @PutMapping("delivery/qualified")
    public R deliveryQualifiedStatusUpdate(@RequestParam("no")String no){
        return productNewService.deliveryQualifiedStatusUpdate(no);
    }

    /**
     * 获取oss签名
     */
    @GetMapping("/oss/getPolicy")
    public Map<String, String> policy(@RequestParam("moduleName")String moduleName) {
        String name = moduleName + "/";
        Map<String, String> ossUploadSign = aliyunOssService.getOssUploadSign(name);
        return ossUploadSign;
    }

    /**
     * 上传图片
     */
    @PostMapping("productNew/file")
    public R saveFile(@RequestBody File file){
        return productNewService.factorySaveFile(file);
    }

    /**
     * 删除图片
     */
    @DeleteMapping("productNew/file/{id}")
    public R removeFile(@PathVariable("id") Long id,  @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType){
        return R.ok(fileService.removeFile(id, fileType));
    }

    /**
     * 下载图片
     */
    @GetMapping("downLoad")
    public R getFile(@RequestParam("fileName") String fileName, @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType, HttpServletResponse response) {
        return fileService.downLoadFile(fileName,fileType, response);
    }
}
