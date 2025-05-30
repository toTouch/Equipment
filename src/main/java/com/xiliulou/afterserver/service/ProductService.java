package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
public interface ProductService extends IService<Product> {
    
    IPage getPage(Long offset, Long size, Integer shelfStatus, Product product);
    
    IPage getPage(Long offset, Long size, String name);
    
    IPage getAllByName(String name);
    
    void exportExcel(Product product, HttpServletResponse response);
    
    R insertSerialNumber(ProductSerialNumberQuery productSerialNumberQuery);
    
    R getSerialNumberPage(Long offset, Long size, ProductSerialNumberQuery productSerialNumber);
    
    void serialNumberExportExcel(ProductSerialNumberQuery productSerialNumberQuery, HttpServletResponse response);
    
    Integer getByDateQuery(Map<String, Object> params);
    
    Integer getMouth(Map<String, Object> params);
    
    Integer getGeneral(Map<String, Object> params);
    
    Integer getTotal(Map<String, Object> params);
    
    Integer getRepairCount(Map<String, Object> params);
    
    R productList();
    
    R productSerialNumber(ProductSerialNumberQuery productSerialNumberQuery);
    
    R productSerialNumberInfo(Long id);
    
    R updateProductSerialNumberInfo(ProductSerialNumberQuery productSerialNumberQuery);
    
    R getProductSerialNumListByPid(Long id);
    
    Product getByName(String name);
    
    R queryProductModelPull(String name);
    
    R productSearch(Long offset, Long size, String name);
    
    R removeProductById(Long id);
}
