package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.PurchaseMapper;
import com.xiliulou.afterserver.service.PurchaseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.web.vo.PurchaseExcelVo;
import com.xiliulou.afterserver.web.vo.PurchaseVo;
import com.xiliulou.afterserver.web.vo.WorkOrderExcelVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:09
 **/
@Slf4j
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {


    @Override
    public IPage getPage(Long offset, Long size, Purchase purchase) {
        Page page = PageUtil.getPage(offset, size);
        return baseMapper.getPage(page, purchase);
    }


    @Override
    public void exportExcel(Purchase purchase, HttpServletResponse response) {

        List<PurchaseVo> purchaseList = baseMapper.getList(purchase);
        if (ObjectUtil.isEmpty(purchaseList)) {
            throw new CustomBusinessException("没有查询到采购记录!无法导出！");
        }
        List<PurchaseExcelVo> purchaseExcelVoList = new ArrayList<>(purchaseList.size());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (PurchaseVo purchaseVo : purchaseList) {
            PurchaseExcelVo purchaseExcelVo = new PurchaseExcelVo();
            BeanUtil.copyProperties(purchaseVo, purchaseExcelVo);
            purchaseExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(purchaseVo.getCreateTime())));
            purchaseExcelVo.setSettingTimeStr(simpleDateFormat.format(new Date(purchaseVo.getSettingTime())));
            purchaseExcelVoList.add(purchaseExcelVo);

        }


        String fileName = "采购记录.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, PurchaseExcelVo.class).sheet("sheet").doWrite(purchaseExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

}

