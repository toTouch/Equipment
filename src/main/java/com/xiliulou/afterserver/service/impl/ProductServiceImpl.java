package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.vo.ProductExcelVo;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberExcelVo;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductSerialNumberMapper productSerialNumberMapper;
    @Autowired
    ProductService productService;
    @Autowired
    WarehouseService warehouseService;
    @Autowired
    PointService pointService;

    @Override
    public IPage getPage(Long offset, Long size, Product product) {

        Page page = PageUtil.getPage(offset, size);
        return baseMapper.selectPage(page, Wrappers.lambdaQuery(product).orderByDesc(Product::getCreateTime));
    }

    @Override
    public void exportExcel(Product product, HttpServletResponse response) {
        List<Product> productList = list();
        if (ObjectUtil.isEmpty(productList)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }
        List<ProductExcelVo> productExcelVos = new ArrayList<>(productList.size());
        for (Product p : productList) {
            ProductExcelVo productExcelVo = new ProductExcelVo();
            BeanUtil.copyProperties(p, productExcelVo);
            productExcelVos.add(productExcelVo);
        }

        String fileName = "产品型号.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, ProductExcelVo.class).sheet("sheet").doWrite(productExcelVos);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    public void serialNumberExportExcel(ProductSerialNumberQuery productSerialNumberQuery, HttpServletResponse response) {
        List<ProductSerialNumberVo> productSerialNumberMapperSerialNumberList = productSerialNumberMapper.getSerialNumberList(productSerialNumberQuery);
        if (ObjectUtil.isEmpty(productSerialNumberMapperSerialNumberList)) {
            throw new CustomBusinessException("没有查询到序列号!无法导出！");
        }
        List<ProductSerialNumberExcelVo> productSerialNumberExcelVoArrayList = new ArrayList<>(productSerialNumberMapperSerialNumberList.size());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ProductSerialNumberVo p : productSerialNumberMapperSerialNumberList) {
            ProductSerialNumberExcelVo productSerialNumberExcelVo = new ProductSerialNumberExcelVo();
            productSerialNumberExcelVo.setSerialNumber(p.getSerialNumber());
            productSerialNumberExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(p.getCreateTime())));
            productSerialNumberExcelVo.setProductName(p.getProductName());
            productSerialNumberExcelVo.setPrice(p.getPrice());
            productSerialNumberExcelVoArrayList.add(productSerialNumberExcelVo);
        }

        String fileName = "产品序列号.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, ProductSerialNumberExcelVo.class).sheet("sheet").doWrite(productSerialNumberExcelVoArrayList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertSerialNumber(ProductSerialNumberQuery productSerialNumberQuery) {

        Product product = getById(productSerialNumberQuery.getProductId());
        if (Objects.isNull(product)) {
            return R.failMsg("未找到产品型号!");
        }
        if (productSerialNumberQuery.getRightInterval() < productSerialNumberQuery.getLeftInterval()
                && productSerialNumberQuery.getRightInterval() <= 9999 && productSerialNumberQuery.getLeftInterval() >= 0) {
            return R.failMsg("请设置合适的编号区间!");
        }
        DecimalFormat decimalFormat = new DecimalFormat("0000");


        for (Long i = productSerialNumberQuery.getLeftInterval(); i <= productSerialNumberQuery.getRightInterval(); i++) {

            ProductSerialNumber productSerialNumber = new ProductSerialNumber();
            productSerialNumber.setSerialNumber(productSerialNumberQuery.getPrefix() + "_" + decimalFormat.format(i));
            productSerialNumber.setProductId(productSerialNumberQuery.getProductId());
            productSerialNumber.setPrice(product.getPrice());
            productSerialNumber.setCreateTime(System.currentTimeMillis());
            productSerialNumberMapper.insert(productSerialNumber);
        }
        return R.ok();
    }

    @Override
    public IPage getSerialNumberPage(Long offset, Long size, ProductSerialNumberQuery productSerialNumber) {
        return productSerialNumberMapper.getSerialNumberPage(PageUtil.getPage(offset, size), productSerialNumber);
    }

    @Override
    public Integer getByDateQuery(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years,mouths);
        return count;
    }

    @Override
    public Integer getMouth(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years,mouths);
        return count;
    }

    @Override
    public Integer getGeneral(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years,mouths);
        return count;
    }

    @Override
    public Integer getTotal(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years,mouths);
        return count;
    }
    
    @Override
    public Integer getRepairCount(Map<String, Object> params) {
        
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years,mouths);
        return count;
    }

    @Override
    public R productList() {
        List<Product> products = this.baseMapper.selectList(null);
        return R.ok(products);
    }

    @Override
    public R productSerialNumber(ProductSerialNumberQuery productSerialNumberQuery) {
        Product product = this.baseMapper.selectById(productSerialNumberQuery.getProductId());
        if (Objects.isNull(product)){
            return R.fail("产品型号有误，请检查");
        }

        Long leftInterval = productSerialNumberQuery.getLeftInterval();
        Long rightInterval = productSerialNumberQuery.getRightInterval();
        String code = product.getCode();

        for (int i = Integer.parseInt(leftInterval.toString()); i < Integer.parseInt(rightInterval.toString()); i++) {

            ProductSerialNumber productSerialNumber = new ProductSerialNumber();
            BeanUtil.copyProperties(productSerialNumberQuery,productSerialNumber);
            productSerialNumber.setSerialNumber(code+"_"+i);
            productSerialNumber.setCreateTime(System.currentTimeMillis());
            productSerialNumberMapper.insert(productSerialNumber);
        }

        return R.ok();
    }

    @Override
    public R productSerialNumberInfo(Long id) {
        ProductSerialNumber productSerialNumber = productSerialNumberMapper.selectById(id);
        ProductSerialNumberVo productSerialNumberVo = new ProductSerialNumberVo();
        BeanUtil.copyProperties(productSerialNumber,productSerialNumberVo);

        if (Objects.nonNull(productSerialNumber)){
            if (productSerialNumberVo.getStatus().equals(ProductSerialNumber.UNUSED)
                    || productSerialNumberVo.getStatus().equals(ProductSerialNumber.TO_BE_REPAIRED)){
                WareHouse wareHouse = warehouseService.getById(productSerialNumber.getPointId());
                if (Objects.nonNull(wareHouse)){
                    productSerialNumberVo.setAddr(wareHouse);
                }
            }

            if (productSerialNumberVo.getStatus().equals(ProductSerialNumber.IN_USE)){
                Point point = pointService.getById(productSerialNumberVo.getPointId());
                if (Objects.nonNull(point)) {
                    productSerialNumberVo.setAddr(point);
                }
            }

        }

        return R.ok(productSerialNumberVo);
    }

    @Override
    public R updateProductSerialNumberInfo(ProductSerialNumberQuery productSerialNumberQuery) {
        int i = productSerialNumberMapper.updateById(productSerialNumberQuery);
        if (i>0){
            return R.ok();
        }
        return R.fail("数据库错误");
    }

}
