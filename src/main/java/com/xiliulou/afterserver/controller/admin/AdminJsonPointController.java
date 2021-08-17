package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.IndexDataQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description: 点位
 * @author: Mr.YG
 * @create: 2021-01-29 11:24
 **/
@RestController
@Slf4j
public class AdminJsonPointController {

    @Autowired
    PointService pointService;
    @Autowired
    CityService cityService;
    @Autowired
    CustomerService customerService;


    @GetMapping("admin/point/page")
    public R getPage(@RequestParam("offset") Long offset,
                     @RequestParam("size") Long size,
                     PointQuery point) {
        return R.ok(pointService.getPage(offset, size, point));
    }

    @GetMapping("admin/point/list")
    public R list() {
        return R.ok(pointService.getlist());
    }

    @GetMapping("/admin/pull/point/list")
    public R pullList(@RequestParam(value = "name",required = false)String name){
        return R.ok(pointService.list(Wrappers.<Point>lambdaQuery().like(Objects.nonNull(name),Point::getName,name)));
    }


    @PostMapping("admin/point")
    public R insert(@RequestBody PointQuery pointQuery) {
        pointQuery.setCreateTime(System.currentTimeMillis());
        pointQuery.setSetTime(System.currentTimeMillis());
        return pointService.savePoint(pointQuery);
    }

    @GetMapping("/admin/point/get/bind/serial/num")
    public R pointGetBingList(@RequestParam("pid")Long pid){
        return pointService.pointGetBingList(pid);
    }

    @PostMapping("admin/point/bindSerialNumber")
    public R pointBindSerialNumber(@RequestBody PointQuery pointQuery) {

        return pointService.pointBindSerialNumber(pointQuery);
    }

    @PutMapping("admin/point/unBindSerialNumber")
    public R unBindSerialNumber(@RequestParam("pid")Long pid){
        return pointService.unBindSerialNumber(pid);
    }

    @PutMapping("admin/point")
    public R update(@RequestBody PointQuery point) {
        // TODO: 2021/1/29 0029  怎么修改 产品绑定

        return pointService.updatePoint(point);
    }

    @DeleteMapping("admin/point")
    public R delete(@RequestParam("id") Long id) {
        pointService.removeById(id);
        return R.ok();
    }

    @GetMapping("admin/indexData/costAmount")
    public R getCostIndexData(IndexDataQuery indexDataQuery) {
        return R.ok(pointService.getCostIndexData(indexDataQuery));
    }

    @GetMapping("admin/point/page/reconciliation")
    public R reconciliationPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, PointQuery point) {
        return pointService.reconciliationPage(offset, size, point);
    }

    @GetMapping("admin/point/reconciliation/exportExcel")
    public void reconciliationPage(PointQuery point, HttpServletResponse response) {
        pointService.exportExcel(point, response);
    }
//
//
//    /**
//     * 导入
//     */
//    @PostMapping("admin/point/upload")
//    public R upload(MultipartFile file) throws IOException {
//
//        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), PointInfo.class,new PointListener(pointService,customerService,cityService)).build();
//        ReadSheet readSheet = EasyExcel.readSheet(0).build();
//        excelReader.read(readSheet);
//        excelReader.finish();
//        return R.ok();
//    }

}
