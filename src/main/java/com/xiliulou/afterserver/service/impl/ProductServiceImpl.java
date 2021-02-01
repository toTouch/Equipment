package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.web.vo.ProductExcelVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.create.table.Index;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {


    @Override
    public IPage getPage(Long offset, Long size, Product product) {

        Page page = PageUtil.getPage(offset, size);
        return baseMapper.selectPage(page, Wrappers.lambdaQuery(product).orderByDesc(Product::getCreateTime));
    }

    @Override
    public void exportExcel(Product product, HttpServletResponse response) {
        List<Product> productList = list();
        if (ObjectUtil.isEmpty(productList)) {
            throw new CustomBusinessException("没有查询到订单!无法导出！");
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
}
