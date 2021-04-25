package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.export.WareHouseInfo;
import com.xiliulou.afterserver.listener.ClientListener;
import com.xiliulou.afterserver.listener.WareHouseListener;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


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
    public R insert(@RequestBody WareHouse wareHouse) { return R.ok(warehouseService.save(wareHouse)); }

    @PutMapping("admin/warehouse")
    public R update(@RequestBody WareHouse wareHouse) {
        return R.ok(warehouseService.updateById(wareHouse));
    }

    @DeleteMapping("admin/warehouse")
    public R delete(@RequestParam("id") Long id) {
        return R.ok(warehouseService.removeById(id));
    }

    @GetMapping("admin/ware/list")
    public R list() {
        return R.ok(warehouseService.list(Wrappers.<WareHouse>lambdaQuery().orderByDesc(WareHouse::getCreateTime)));
    }

    /**
     * 导入
     */
    @PostMapping("admin/ware/upload")
    public R upload(MultipartFile file) throws IOException {

        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), WareHouseInfo.class,new WareHouseListener(warehouseService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

}
