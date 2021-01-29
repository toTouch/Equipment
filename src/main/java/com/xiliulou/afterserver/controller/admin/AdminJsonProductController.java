package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 09:47
 **/
@RestController
@Slf4j
public class AdminJsonProductController {

    @Autowired
    ProductService productService;


    @GetMapping("product/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Product product) {
        return R.ok(productService.getPage(offset, size, product));
    }


    @PostMapping("product")
    public R insert(@RequestBody Product product) {
        product.setCreateTime(System.currentTimeMillis());
        return R.ok(productService.save(product));
    }

    @PutMapping("product")
    public R update(@RequestBody Product product) {
        return R.ok(productService.updateById(product));
    }

    @DeleteMapping("product")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }

}
