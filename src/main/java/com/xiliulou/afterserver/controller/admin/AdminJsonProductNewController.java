package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
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

    @PostMapping("/admin/productNew")
    public R saveAdminPointNew(@RequestBody ProductNew productNew){
        return productNewService.saveAdminProductNew(productNew);
    }

    @PutMapping("/admin/productNew")
    public R putAdminPointNew(@RequestBody ProductNew productNew){
        return productNewService.putAdminProductNew(productNew);
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
                       @RequestParam(value = "startTime",required = false) Long startTime,
                       @RequestParam(value = "endTime",required = false) Long endTime){
        List<ProductNew> productNews = productNewService.queryAllByLimit(offset,limit,no,modelId,startTime,endTime);

        productNews.forEach(item -> {
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
        });


        Integer count = productNewService.count(no,modelId,startTime,endTime);

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("data",productNews);
        stringObjectHashMap.put("count",count);
        return R.ok(stringObjectHashMap);
    }


    @GetMapping("/admin/productNew/{id}")
    public R getProductFile(@PathVariable("id") Long id){
        return productNewService.getProductFile(id);
    }


}
