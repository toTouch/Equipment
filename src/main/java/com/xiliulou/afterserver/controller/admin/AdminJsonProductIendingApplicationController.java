package com.xiliulou.afterserver.controller.admin;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.service.ProductIendingApplicationService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationItemQuery;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/9/18 15:39
 * @mood
 */
@RestController
@Slf4j
public class AdminJsonProductIendingApplicationController extends BaseController {

    @Autowired
    ProductIendingApplicationService productIendingApplicationService;

    @PostMapping("admin/productIendingApplication")
    public R insert(@RequestParam("appQuery") String productLendingApplicationQuery, @RequestParam("appItemQuery") String list){
        ProductLendingApplicationQuery p = JSON.parseObject(productLendingApplicationQuery, ProductLendingApplicationQuery.class);
        List l = JSON.parseArray(list, ProductLendingApplicationItemQuery.class);
        return productIendingApplicationService.insert(p, l);
    }

    @PutMapping("admin/productIendingApplication/updateStatus")
    public R updateStatus(Long id, Long status){
        return productIendingApplicationService.updateStatus(id, status);
    }

    @GetMapping("admin/productIendingApplication/page")
    public R getPage(Long offset, Long size, ProductLendingApplicationQuery productLendingApplicationQuery) {
        return R.ok(productIendingApplicationService.getPage(offset, size, productLendingApplicationQuery));
    }
}
