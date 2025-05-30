package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @GetMapping("/batch/list")
    public R queryByfactory(@RequestParam(value = "offset", required = false) Long offset,
                            @RequestParam(value = "limit", required = false) Long size,
                            @RequestParam(value = "notShipped",required = false)Integer notShipped,
                            @RequestParam(value = "batchNo",required = false) String batchNo
           ){
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



        return batchService.queryByfactory(offset, size, notShipped, batchNo);
    }

    /**
     *手持终端 获取柜机列表
     * @param batchId
     * @return
     */
    @GetMapping("/productNew")
    public R queryByBatchAndSupplier(@RequestParam("batchId") Long batchId,
                                     @RequestParam(value = "productNo", required = false) String productNo,
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

        return productNewService.queryByBatchAndSupplier(batchId, offset, size, productNo);
    }

    /**
     * 手持终端 获取柜机详情
     * @param no
     * @return
     */
    @GetMapping("/productNew/info")
    public R queryProductNewInfoById(@RequestParam("no")String no, HttpServletResponse response){
        return productNewService.queryProductNewProcessInfo(no, response);
    }

    /**
     * 手持终端 更新产品信息
     * @param productNewDetailsQuery
     * @return
     */
    //@PutMapping("/productNew")
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

}
