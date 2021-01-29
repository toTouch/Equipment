package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 11:24
 **/
@RestController
@Slf4j
public class AdminJsonPointController {

    @Autowired
    PointService pointService;


    @GetMapping("admin/point/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Point point) {
        return R.ok(pointService.getPage(offset, size, point));
    }


    @PostMapping("admin/point")
    public R insert(@RequestBody Point point) {
        point.setCreateTime(System.currentTimeMillis());
        return R.ok(pointService.save(point));
    }

    @PutMapping("admin/point")
    public R update(@RequestBody Point point) {
        return R.ok(pointService.updateById(point));
    }

    @DeleteMapping("admin/point")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }

}
