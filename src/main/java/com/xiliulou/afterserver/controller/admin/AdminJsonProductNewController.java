package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.service.ProductNewService;
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
                       @RequestParam("limit") Integer limit){
        List<ProductNew> productNews = productNewService.queryAllByLimit(offset, limit);




        Integer count = productNewService.count();

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("data",productNews);
        stringObjectHashMap.put("count",count);
        return R.ok(stringObjectHashMap);
    }

}
