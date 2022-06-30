package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.service.AuditProcessService;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.KeyProcessQuery;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.GET;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author zgw
 * 电装检验、发货检验
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

    @Autowired
    private AuditProcessService auditProcessService;

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
     * 删除ossFile
     */
    @DeleteMapping("oss/file")
    public R delOssFileByName(@RequestParam String name){
        return productNewService.delOssFileByName(name);
    }

    /**
     * 获取关键流程内容
     */
    @GetMapping("key/process")
    public R getKeyProcess(@RequestParam("no")String no, @RequestParam("type")String type, @RequestParam(value = "groupId", required = false)Long groupId){
        return auditProcessService.getKeyProcess(no, type, groupId, null);
    }

    /**
     *更新关键流程内容
     */
    @PutMapping("key/process")
    public R putKeyProcess(@Validated @RequestBody KeyProcessQuery keyProcessQuery){
        return auditProcessService.putKeyProcess(keyProcessQuery);
    }
}
