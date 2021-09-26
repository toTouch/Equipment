package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
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
import java.util.List;
import java.util.Objects;


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
        if(ObjectUtils.isNotNull(wareHouse)){
            if(ObjectUtils.isNotNull(wareHouse.getWareHouses())){
                return R.fail("请输入仓库名称！");
            }

            if(ObjectUtils.isNotNull(wareHouse.getAddress())){
                return R.fail("请输入仓库地址！");
            }

            wareHouse.setCreateTime(System.currentTimeMillis());
            List<WareHouse> list = warehouseService.list();

            for(WareHouse item : list){
                if(Objects.equals(wareHouse.getWareHouses(), item.getWareHouses())){
                    return R.fail("已存在该仓库！");
                }
            }

            return R.ok(warehouseService.save(wareHouse));
        }
        return R.fail("参数异常！");
    }

    @PutMapping("admin/warehouse")
    public R update(@RequestBody WareHouse wareHouse) {
        return R.ok(warehouseService.updateById(wareHouse));
    }

    @DeleteMapping("admin/warehouse")
    public R delete(@RequestParam("id") Long id) {
        return R.ok(warehouseService.removeById(id));
    }

    @GetMapping("admin/warehouse/list")
    public R list(@RequestParam(value = "name",required = false) String name) {
        return R.ok(warehouseService.list(Wrappers.<WareHouse>lambdaQuery().orderByDesc(WareHouse::getCreateTime)
                .like(Objects.nonNull(name),WareHouse::getWareHouses,name)
        ));
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
