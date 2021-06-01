package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.SupplierInfo;
import com.xiliulou.afterserver.listener.SupplierListener;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @program: XILIULOU
 * @description: 供应商列表
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
        this.supplierService.removeById(id);
        return R.ok();
    }

    @GetMapping("admin/supplier/list")
    public R list() {
        return R.ok(supplierService.list(Wrappers.<Supplier>lambdaQuery().orderByDesc(Supplier::getCreateTime)));
    }

    /**
     * 导入
     */
    @PostMapping("admin/supplier/upload")
    public R upload(MultipartFile file) throws IOException {

        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), SupplierInfo.class,new SupplierListener(supplierService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

}
