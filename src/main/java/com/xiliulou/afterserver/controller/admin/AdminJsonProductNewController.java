package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.CompressionQuery;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import com.xiliulou.afterserver.web.query.ProductNewQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private ProductService productService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private PointProductBindService pointProductBindService;
    @Autowired
    private PointNewService pointNewService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private WarehouseService warehouseService;

    //@PostMapping("/admin/productNew")
    public R saveAdminPointNew(@RequestBody ProductNew productNew){
        return productNewService.saveAdminProductNew(productNew);
    }

    @PutMapping("/admin/productNew")
    public R putAdminPointNew(@RequestBody ProductNewQuery query){
        return productNewService.putAdminProductNew(query);
    }

    @DeleteMapping("/admin/productNew/{id}")
    public R delAdminPointNew(@PathVariable("id") Long id){
        return productNewService.delAdminProductNew(id);
    }

    @GetMapping("/admin/productNew/list")
    public R pointList(@RequestParam("offset") Integer offset,
                       @RequestParam("limit") Integer limit,
                       @RequestParam(value = "no",required = false) String no,
                       @RequestParam(value = "modelId",required = false) Long modelId,
                       @RequestParam(value = "pointId",required = false) Long pointId,
                       @RequestParam(value = "pointType",required = false) Integer pointType,
                       @RequestParam(value = "startTime",required = false) Long startTime,
                       @RequestParam(value = "endTime",required = false) Long endTime){
        List<ProductNew> productNews = productNewService.queryAllByLimit(offset,limit,no,modelId,startTime,endTime,pointId, pointType);

        productNews.forEach(item -> {

            PointProductBind pointProductBind = pointProductBindService.queryByProductId(item.getId());
            if(Objects.nonNull(pointProductBind)){
                if(Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_POINT)){
                    PointNew pointNew = pointNewService.getById(pointProductBind.getPointId());
                    if(Objects.nonNull(pointNew)){
                        item.setPointId(pointNew.getId().intValue());
                        item.setPointName(pointNew.getName());
                        item.setPointType(PointProductBind.TYPE_POINT);
                    }
                }
                if(Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_WAREHOUSE)){
                    WareHouse wareHouse = warehouseService.getById(pointProductBind.getPointId());
                    if(Objects.nonNull(wareHouse)){
                        item.setPointId(wareHouse.getId());
                        item.setPointName(wareHouse.getWareHouses());
                        item.setPointType(PointProductBind.TYPE_WAREHOUSE);
                    }
                }
                if(Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_SUPPLIER)){
                    Supplier supplier = supplierService.getById(pointProductBind.getPointId());
                    if(Objects.nonNull(supplier)){
                        item.setPointId(supplier.getId().intValue());
                        item.setPointName(supplier.getName());
                        item.setPointType(PointProductBind.TYPE_SUPPLIER);
                    }
                }
            }

            if (Objects.nonNull(item.getModelId())){
                Product product = productService.getBaseMapper().selectById(item.getModelId());
                if (Objects.nonNull(product)){
                    item.setModelName(product.getName());
                }
            }

            if (Objects.nonNull(item.getBatchId())){
                Batch batch = batchService.queryByIdFromDB(item.getBatchId());
                if (Objects.nonNull(batch)){
                    item.setBatchName(batch.getBatchNo());
                }
            }

            if(Objects.nonNull(item.getSupplierId())){
                Supplier supplier = supplierService.getById(item.getSupplierId());
                if(Objects.nonNull(supplier)){
                    item.setSupplierName(supplier.getName());
                }
            }
        });


        Integer count = productNewService.count(no,modelId,startTime,endTime,pointId);

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("data",productNews);
        stringObjectHashMap.put("count",count);
        return R.ok(stringObjectHashMap);
    }


    @GetMapping("/admin/productNew/{id}")
    public R getProductFile(@PathVariable("id") Long id){
        return productNewService.getProductFile(id);
    }

    @PutMapping("/admin/productNew/update/status")
    public R updateStatusFromBatch(@RequestParam(value = "ids",required = false) List<Long> ids,
                          @RequestParam("status") Integer status){
        if (ids.isEmpty()){
            return R.fail("id不能为空");
        }

        return productNewService.updateStatusFromBatch(ids,status);

    }

    @GetMapping("/admin/queryProductInfo")
    public R  queryProductInfo(String no){
        return productNewService.queryProductInfo(no);
    }

    @GetMapping("/admin/queryLikeProductByNo")
    public R queryLikeProductByNo(String no){
        return productNewService.queryLikeProductByNo(no);
    }

    @PostMapping("/admin/bindPoint")
    public R bindPoint(Long productId, Long pointId, Integer pointType){
        return productNewService.bindPoint(productId, pointId, pointType);
    }

    @PostMapping("/admin/check/compression")
    public R checkCompression(@RequestBody CompressionQuery compression){
        return productNewService.checkCompression(compression);
    }

    @PostMapping("/admin/success/compression")
    public R successCompression(@RequestBody CompressionQuery compression){
        return productNewService.successCompression(compression);
    }

    /**
     * 根据资产编码拉取物联网卡信息
     * @param no
     * @return
     */
    @GetMapping("/admin/productNew/findIotCard")
    public R findIotCard(@RequestParam("no") String no){
        return productNewService.findIotCard(no);
    }

    /**
     *手持终端 获取柜机列表
     * @param batchId
     * @return
     */
    @GetMapping("/admin/productNew/factory")
    public R queryByBatchAndSupplier(@RequestParam("batchId") Long batchId){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }
        return productNewService.queryByBatchAndSupplier(batchId);
    }

    /**
     * 手持终端获取 柜机详情
     * @param id
     * @return
     */
    @GetMapping("/admin/productNew/info")
    public R queryProductNewInfoById(@RequestParam("id")Long id){
        return productNewService.queryProductNewInfoById(id);
    }

    @PutMapping("/admin/productNew/update")
    public R updateProductNew(@RequestBody ProductNewDetailsQuery productNewDetailsQuery){
        return productNewService.updateProductNew(productNewDetailsQuery);
    }
}
