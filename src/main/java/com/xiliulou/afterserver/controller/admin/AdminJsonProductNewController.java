package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.CompressionQuery;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import com.xiliulou.afterserver.web.query.ProductNewQuery;
import com.xiliulou.afterserver.web.vo.OssUrlVo;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Hardy
 * @date 2021/8/17 10:34
 * @mood 产品模块新改
 */
@RestController
@Slf4j
public class AdminJsonProductNewController {
    @Autowired
    private ProductNewService productNewService;
    //    @Autowired
//    private ProductService productService;
//    @Autowired
//    private BatchService batchService;
//    @Autowired
//    private PointProductBindService pointProductBindService;
//    @Autowired
//    private PointNewService pointNewService;
//    @Autowired
//    private SupplierService supplierService;
//    @Autowired
//    private WarehouseService warehouseService;
//    @Autowired
//    private IotCardService iotCardService;
//    @Autowired
//    private CameraService cameraService;
    @Autowired
    private AliyunOssService aliyunOssService;
    @Autowired
    private StorageConfig StorageConfig;
//    @Autowired
//    private ColorCardService colorCardService;

    //@PostMapping("/admin/productNew")
//    public R saveAdminPointNew(@RequestBody ProductNew productNew){
//        return productNewService.saveAdminProductNew(productNew);
//    }

    @PutMapping("/admin/productNew")
    public R putAdminPointNew(@RequestBody ProductNewQuery query) {
        return productNewService.putAdminProductNew(query);
    }

   /*
    * 删除产品
    */
    @DeleteMapping("/admin/productNew/{id}")
    public R delAdminPointNew(@PathVariable("id") Long id) {
        return productNewService.delAdminProductNew(id);
    }

    /**
     * 产品列表
     * @param offset 查询起始位置
     * @param limit 条数
     * @param no 产品编号
     * @param modelId 产品型号
     * @param pointId 产品位置
     * @param pointType 产品位置类型
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @param testType 测试类型
     * @return
     */

    @GetMapping("/admin/productNew/list")
    public R pointList(@RequestParam("offset") Integer offset,
                       @RequestParam("limit") Integer limit,
                       @RequestParam(value = "no", required = false) String no,
                       @RequestParam(value = "modelId", required = false) Long modelId,
                       @RequestParam(value = "pointId", required = false) Long pointId,
                       @RequestParam(value = "pointType", required = false) Integer pointType,
                       @RequestParam(value = "startTime", required = false) Long startTime,
                       @RequestParam(value = "endTime", required = false) Long endTime,
                       @RequestParam(value = "testType", required = false) String testType) {
        return productNewService.pointList(offset, limit, no, modelId, pointId, pointType, startTime, endTime, testType);

    }


    //查看图片
    @GetMapping("/admin/productNew/{id}")
    public R getProductFile(@PathVariable("id") Long id) {
        return productNewService.getProductFile(id, File.FILE_TYPE_PRODUCT_PRODUCT);
    }

    //更新
    @PutMapping("/admin/productNew/update/status")
    public R updateStatusFromBatch(@RequestParam(value = "ids", required = false) List<Long> ids,
                                   @RequestParam("status") Integer status) {
        if (ids.isEmpty()) {
            return R.fail("id不能为空");
        }

        return productNewService.updateStatusFromBatch(ids, status);

    }

    @GetMapping("/admin/queryProductInfo")
    public R queryProductInfo(String no) {
        return productNewService.queryProductInfo(no);
    }

    @GetMapping("/admin/queryLikeProductByNo")
    public R queryLikeProductByNo(String no) {
        return productNewService.queryLikeProductByNo(no);
    }

    @PostMapping("/admin/bindPoint")
    public R bindPoint(Long productId, Long pointId, Integer pointType) {
        return productNewService.bindPoint(productId, pointId, pointType);
    }


    /**
     * 根据资产编码拉取物联网卡信息
     *
     * @param no
     * @return
     */
    //@GetMapping("/admin/productNew/findIotCard")
    public R findIotCard(@RequestParam("no") String no) {
        return productNewService.findIotCard(no);
    }


    /**
     * 下载柜机测试文件
     *
     * @param fileName
     * @return
     */
    @GetMapping("admin/productNew/testFile")
    public R getTestFile(@RequestParam("fileName") String fileName) {
        String url = null;
        String testFileName = "";
        final String HTTP = "http";
        final String HTTPS = "https";

        if (StringUtils.isNotBlank(StorageConfig.getTestFileDir())) {
            testFileName = StorageConfig.getTestFileDir();
        }
        try {
            url = aliyunOssService.getOssFileUrl(StorageConfig.getOssTestFileBucketName(), testFileName + fileName, System.currentTimeMillis() + 120 * 1000);
        } catch (Exception e) {
            log.error("oss error!", e);
        }

        if (Objects.isNull(url)) {
            return R.fail("oss error");
        }

        url = HTTPS + url.substring(4);

        Map<String, String> result = new HashMap<>(1);
        result.put("url", url);
        return R.ok(result);
    }
}
