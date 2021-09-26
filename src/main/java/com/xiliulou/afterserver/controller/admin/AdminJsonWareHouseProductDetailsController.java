package com.xiliulou.afterserver.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WareHouseProductDetailsService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.WareHouseProductDetailsQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author zgw
 * @date 2021/9/17 18:17
 * @mood
 */

@RestController
@Slf4j
public class AdminJsonWareHouseProductDetailsController extends BaseController {
    @Autowired
    WareHouseProductDetailsService wareHouseProductDetailsService;

    @GetMapping("admin/wareHouseProductDetails/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size,  WareHouseProductDetailsQuery wareHouseProductDetailsQuery) {
        if(wareHouseProductDetailsQuery == null || Objects.isNull(wareHouseProductDetailsQuery.getWareHouseId())){
            R.fail("请传入仓库Id");
        }
        return R.ok(wareHouseProductDetailsService.getPage(offset, size, wareHouseProductDetailsQuery));
    }

    @PostMapping("admin/wareHouseProductDetails")
    public R insert(Long wareHouseId, @RequestParam("WareHouseProductDetailsQuerys") String WareHouseProductDetailsQuerys){
        List<WareHouseProductDetailsQuery> list = JSON.parseArray(WareHouseProductDetailsQuerys, WareHouseProductDetailsQuery.class);
        return wareHouseProductDetailsService.insert(wareHouseId, list);
    }
}
