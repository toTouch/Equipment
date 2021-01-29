package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 09:13
 **/
@RestController
@Slf4j
public class AdminJsonSupplierController extends BaseController {


    @Autowired
    SupplierService supplierService;

    @GetMapping("admin/supplier/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Supplier supplier) {
        return R.ok(supplierService.getPage(offset, size, supplier));
    }


    @PostMapping("admin/supplier")
    public R insert(@RequestBody Supplier supplier) {
        supplier.setCreateTime(System.currentTimeMillis());
        return R.ok(supplierService.save(supplier));
    }

    @PutMapping("admin/supplier")
    public R update(@RequestBody Supplier supplier) {
        return R.ok(supplierService.updateById(supplier));
    }

    @DeleteMapping("admin/supplier")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }
}
