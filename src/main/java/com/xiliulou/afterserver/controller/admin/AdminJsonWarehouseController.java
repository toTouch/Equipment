package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class AdminJsonWarehouseController extends BaseController {

    @Autowired
    WarehouseService warehouseService;

    @GetMapping("admin/warehouse/page")
    public R getPage(@RequestParam("offset") Long offset,@RequestParam("size") Long size,WareHouse wareHouse) {
//        warehouseService.getPageForList(map);
        return R.ok(warehouseService.getPage(offset, size, wareHouse));
    }


    @PostMapping("admin/warehouse")
    public R insert(@RequestBody WareHouse wareHouse) {
        wareHouse.setCreateTime(System.currentTimeMillis());
        return R.ok(warehouseService.save(wareHouse));
    }

    @PutMapping("admin/warehouse")
    public R update(@RequestBody WareHouse wareHouse) {
        return R.ok(warehouseService.updateById(wareHouse));
    }

    @DeleteMapping("admin/warehouse")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }

    @GetMapping("admin/ware/list")
    public R list() {
        return R.ok(warehouseService.list(Wrappers.<WareHouse>lambdaQuery().orderByDesc(WareHouse::getCreateTime)));
    }

}
