package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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


    @GetMapping("admin/product/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Product product) {
        return R.ok(productService.getPage(offset, size, product));
    }


    @PostMapping("admin/product")
    public R insert(@RequestBody Product product) {
        return R.ok(productService.save(product));
    }

    @PutMapping("admin/product")
    public R update(@RequestBody Product product) {
        return R.ok(productService.updateById(product));
    }

    @DeleteMapping("admin/product")
    public R delete(@RequestParam("id") Long id) {
        return R.ok(productService.removeById(id));
    }

    @GetMapping("admin/product/exportExcel")
    public void exportExcel(Product product, HttpServletResponse response) {
        productService.exportExcel(product, response);
    }

    @PostMapping("admin/product/serialNumber")
    public R insertSerialNumber(@RequestBody ProductSerialNumberQuery productSerialNumberQuery) {

        return productService.insertSerialNumber(productSerialNumberQuery);
    }

    @GetMapping("admin/product/serialNumber/page")
    public R getSerialNumberPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size,
                                 ProductSerialNumberQuery productSerialNumber) {
        return R.ok(productService.getSerialNumberPage(offset, size, productSerialNumber));
    }

    @GetMapping("admin/product/serialNumber/exportExcel")
    public void serialNumberExportExcel(ProductSerialNumberQuery productSerialNumberQuery, HttpServletResponse response) {
        productService.serialNumberExportExcel(productSerialNumberQuery, response);
    }


}
