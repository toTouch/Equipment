package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.service.ProductSerialNumberService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description: 产品
 * @author: Mr.YG
 * @create: 2021-01-29 09:47
 **/
@RestController
@Slf4j
public class AdminJsonProductController {

    @Autowired
    ProductService productService;
    @Autowired
    ProductFileMapper productFileMapper;
    @Autowired
    ProductSerialNumberService productSerialNumberService;


    // 产品型号列表
    @GetMapping("admin/product/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size,@RequestParam("shelfStatus") Integer shelfStatus, Product product) {
        return R.ok(productService.getPage(offset, size,shelfStatus, product));
    }


    @PostMapping("admin/product")
    @Transactional(rollbackFor = Exception.class)
    public R insert(@RequestBody Product product) {
        Product productOld = productService.getByName(product.getName());
        if(Objects.nonNull(productOld)){
            return R.fail("类型【"+product.getName()+"】已存在");
        }
        //换电柜 ，消防类型不能为空
        if(Objects.equals(product.getProductSeries(),3)){
            if (Objects.isNull(product.getFireFightingType())){
                return R.fail("换电柜消防类型不能为空");
            }
            if (Objects.isNull(product.getHasScreen())){
                return R.fail("换电柜柜机类型不能为空");
            }
            if (Objects.nonNull(product.getBoxNumber())&& product.getBoxNumber()>=100){
                return R.fail("换电柜格口数量必须小于100");
            }
        }
        product.setCreateTime(System.currentTimeMillis());
        productService.save(product);
        return R.ok();
    }

    @PutMapping("admin/product")
    public R update(@RequestBody Product product) {
        Product productOld = productService.getByName(product.getName());
        if(Objects.nonNull(productOld) && !Objects.equals(productOld.getId(), product.getId())){
            return R.fail("类型【"+product.getName()+"】已存在");
        }
        //换电柜 ，消防类型不能为空
        if(Objects.equals(product.getProductSeries(),3)){
            if (Objects.isNull(product.getFireFightingType())){
                return R.fail("换电柜消防类型不能为空");
            }
            if (Objects.isNull(product.getHasScreen())){
                return R.fail("换电柜柜机类型不能为空");
            }
            if (Objects.nonNull(product.getBoxNumber())&& product.getBoxNumber()>=100){
                return R.fail("换电柜格口数量必须小于100");
            }
        }
        return R.ok(productService.updateById(product));
    }

    // 删除产品型号
    @DeleteMapping("admin/product")
    public R delete(@RequestParam("id") Long id) {
        if (Objects.isNull(id)) {
            return R.fail("请选择产品型号");
        }
        return R.ok(productService.removeProductById(id));
    }

    // 产品管理】-【产品型号】增减上下架功能
    @DeleteMapping("admin/product/shelf/{id}")
    public R shelfOne(@PathVariable("id") Long id, @RequestParam("shelfStatus") Integer shelfStatus) {
        return productService.dupdateOneShelf(id, shelfStatus);
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
        return productService.getSerialNumberPage(offset, size, productSerialNumber);
    }

    @GetMapping("admin/product/serialNumber/exportExcel")
    public void serialNumberExportExcel(ProductSerialNumberQuery productSerialNumberQuery, HttpServletResponse response) {
        productService.serialNumberExportExcel(productSerialNumberQuery, response);
    }

    @GetMapping("/admin/product/list")
    public R productList(){
       return productService.productList();
    }

    @GetMapping("/admin/product/serial/number/pid")
    public R getProductSerialNumberByPid(@RequestParam("id") Long id){
        return productService.getProductSerialNumListByPid(id);
    }

    @PostMapping("/admin/product/serial/number")
    public R productSerialNumber(@RequestBody ProductSerialNumberQuery productSerialNumberQuery){
        return productService.productSerialNumber(productSerialNumberQuery);
    }

    @GetMapping("/admin/product/serial/number")
    public R productSerialNumberInfo(@RequestParam("id") Long id){
        return productService.productSerialNumberInfo(id);
    }

    @PutMapping("/admin/product/serial/number")
    public R updateProductSerialNumberInfo(@RequestBody ProductSerialNumberQuery productSerialNumberQuery){
        return productService.updateProductSerialNumberInfo(productSerialNumberQuery);
    }

    @DeleteMapping("/admin/product/serial/number")
    public R delProductSerialNumber(@RequestParam("id") Long id){
        return R.ok(productSerialNumberService.removeById(id));
    }

    @GetMapping("/admin/product/search")
    public R productSearch(@RequestParam("offset")Long offset, @RequestParam("size")Long size,
        @RequestParam(value = "name", required = false)String name){
        if(Objects.isNull(offset) || offset < 0) {
            offset = 0L;
        }
        if(Objects.isNull(size) || size < 0 || size > 20) {
            size = 20L;
        }

        return productService.productSearch(offset, size, name);
    }
}
