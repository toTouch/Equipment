package com.xiliulou.afterserver.controller.factory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/2/25 11:39
 * @mood
 */
@RestController
@RequestMapping("/admin/factory")
@Slf4j
public class JsonAdminOrderBatchController {
    /**
     * 服务对象
     */
    @Autowired
    private BatchService batchService;
    @Autowired
    private ProductNewService productNewService;
    @Autowired
    private IotCardService iotCardService;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private ColorCardService colorCardService;
    @Autowired
    private FileService fileService;
    @Autowired
    private AliyunOssService aliyunOssService;


    @GetMapping("/batch/list")
    public R queryByfactory(@RequestParam(value = "offset", required = false) Long offset,
                            @RequestParam(value = "limit", required = false) Long size){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }

        if(Objects.isNull(size)){
            size = 50L;
        }

        if(Objects.isNull(offset)){
            offset = 0L;
        }

        //分页 手持终端第一页从零开始
        offset = offset  * size;



        return batchService.queryByfactory(offset, size);
    }

    /**
     *手持终端 获取柜机列表
     * @param batchId
     * @return
     */
    @GetMapping("/productNew")
    public R queryByBatchAndSupplier(@RequestParam("batchId") Long batchId,
                                     @RequestParam(value = "offset", required = false) Long offset,
                                     @RequestParam(value = "size", required = false) Long size){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }

        if(Objects.isNull(size)){
            size = 50L;
        }

        if(Objects.isNull(offset)){
            offset = 0L;
        }

        //分页 手持终端第一页从零开始
        offset = offset  * size;

        return productNewService.queryByBatchAndSupplier(batchId, offset, size);
    }

    /**
     * 手持终端获取 柜机详情
     * @param no
     * @return
     */
    @GetMapping("/productNew/info")
    public R queryProductNewInfoById(@RequestParam("no")String no, HttpServletResponse response){
        return productNewService.queryProductNewInfoById(no, response);
    }

    /**
     * 手持终端 更新产品信息
     * @param productNewDetailsQuery
     * @return
     */
    @PutMapping("/productNew")
    public R updateProductNew(@RequestBody ProductNewDetailsQuery productNewDetailsQuery){
        return productNewService.updateProductNew(productNewDetailsQuery);
    }

    @GetMapping("/iotCard/snLike")
    public R iotCardSnLike(@RequestParam(value = "sn", required = false) String sn,
                    @RequestParam(value = "offset", required = false) Long offset,
                    @RequestParam(value = "size", required = false) Long size) {

        if(Objects.isNull(size)){
            size = 10L;
        }

        if(Objects.isNull(offset)){
            offset = 0L;
        }

        //分页 手持终端第一页从零开始
        offset = offset  * size;

        //log.info("物联网卡模糊搜索 -----> sn = {}", sn);
        return iotCardService.pageIotcardLikeSn(offset, size, sn);
    }

    @GetMapping("camera/snLike")
    public R cameraSnLike(@RequestParam(value = "sn", required = false) String sn,
                                   @RequestParam(value = "offset", required = false) Long offset,
                                   @RequestParam(value = "size", required = false) Long size){

        if(Objects.isNull(size)){
            size = 10L;
        }

        if(Objects.isNull(offset)){
            offset = 0L;
        }

        //分页 手持终端第一页从零开始
        offset = offset  * size;


        return cameraService.cameraSnLike(offset, size, sn);
    }

    @GetMapping("camera/iotCardInfo")
    public R queryCameraIotCardBySn(@RequestParam(value = "sn") String sn){
        return cameraService.queryCameraIotCardBySn(sn);
    }

    @GetMapping("colorCard/pull")
    public R queryPull(@RequestParam(value = "name", required = false) String name){
        return  colorCardService.queryPull(name);
    }

    /**
     * 上传图片
     * @param file
     * @return
     */
    @PostMapping("productNew/file")
    public R saveFile(@RequestBody File file){
        file.setCreateTime(System.currentTimeMillis());
        return R.ok(fileService.save(file));
    }

    /**
     * 获取oss签名
     * @param moduleName
     * @return
     */
    @GetMapping("/oss/getPolicy")
    public Map<String, String> policy(@RequestParam("moduleName")String moduleName) {
        String name = moduleName + "/";
        Map<String, String> ossUploadSign = aliyunOssService.getOssUploadSign(name);
        return ossUploadSign;
    }

    /**
     * 删除图片
     * @param id
     * @return
     */
    @DeleteMapping("productNew/file/{id}")
    public R removeFile(@PathVariable("id") Long id,  @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType){
        return R.ok(fileService.removeFile(id, fileType));
    }

    /**
     * 下载图片
     * @param fileName
     * @param fileType
     * @param response
     * @return
     */
    @GetMapping("downLoad")
    public R getFile(@RequestParam("fileName") String fileName, @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType, HttpServletResponse response) {
        return fileService.downLoadFile(fileName,fileType, response);
    }
}
