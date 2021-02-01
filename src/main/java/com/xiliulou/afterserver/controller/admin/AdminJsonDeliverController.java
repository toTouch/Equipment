package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:55
 **/
@RestController
@Slf4j
public class AdminJsonDeliverController {


    @Autowired
    DeliverService deliverService;


    @GetMapping("admin/deliver/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, DeliverQuery deliver) {
        return R.ok(deliverService.getPage(offset, size, deliver));
    }


    @PostMapping("admin/deliver")
    public R insert(@RequestBody Deliver deliver) {
        deliver.setCreateTime(System.currentTimeMillis());
        return R.ok(deliverService.save(deliver));
    }

    @PutMapping("admin/deliver")
    public R update(@RequestBody Deliver deliver) {
        return R.ok(deliverService.updateById(deliver));
    }

    @DeleteMapping("admin/deliver")
    public R delete(@RequestParam("id") Long id) {
        return R.ok(deliverService.removeById(id));
    }

}
