package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Product;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
public interface ProductService extends IService<Product> {
    IPage getPage(Long offset, Long size, Product product);

    void exportExcel(Product product, HttpServletResponse response);
}
