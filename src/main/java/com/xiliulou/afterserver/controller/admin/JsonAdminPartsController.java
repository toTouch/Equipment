package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.export.PartsInfo;
import com.xiliulou.afterserver.export.ServiceInfo;
import com.xiliulou.afterserver.listener.PartsListener;
import com.xiliulou.afterserver.listener.ServiceListener;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.afterserver.web.query.PartsQuery;
import com.xiliulou.core.web.R;
import java.io.IOException;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * (Parts)表控制层
 *
 * @author Hardy
 * @since 2022-12-15 15:02:06
 */
@RestController
public class JsonAdminPartsController {
    /**
     * 服务对象
     */
    @Resource
    private PartsService partsService;

    @GetMapping("admin/parts")
    public R queryList(@RequestParam("size") Integer size,
                       @RequestParam("offset") Integer offset,
                       @RequestParam(value = "name", required = false) String name){
        return  partsService.queryList(size, offset, name);
    }

    @PostMapping("admin/parts")
    public R saveOne(@RequestBody @Validated PartsQuery partsQuery){
        return  partsService.saveOne(partsQuery);
    }

    @PutMapping("admin/parts")
    public R updateOne(@RequestBody @Validated PartsQuery partsQuery){
        return  partsService.updateOne(partsQuery);
    }

    @DeleteMapping("admin/parts/{id}")
    public R deleteOne(@PathVariable("id")Long id){
        return  partsService.deleteOne(id);
    }

    @GetMapping("admin/parts/pull")
    public R queryPull(@RequestParam("size") Integer size,
        @RequestParam("offset") Integer offset,
        @RequestParam(value = "name", required = false) String name){
        if(Objects.isNull(offset) || offset < 0) {
            offset = 0;
        }

        if(Objects.isNull(size) || size < 0 || size > 50) {
            size = 50;
        }
        return  partsService.queryPull(size, offset, name);
    }

    /**
     * 导入
     */
    @PostMapping("admin/parts/upload")
    public R upload(MultipartFile file) throws IOException {
        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), PartsInfo.class,new PartsListener(partsService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }
}
