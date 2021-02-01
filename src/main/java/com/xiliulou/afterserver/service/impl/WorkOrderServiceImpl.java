package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.WorkOrderMapper;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SaveWorkOrderQuery;
import com.xiliulou.afterserver.web.vo.ProductExcelVo;
import com.xiliulou.afterserver.web.vo.WorkOrderExcelVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * @create: 2021-02-01 09:27
 **/
@Service
@Slf4j
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Autowired
    CustomerService customerService;
    @Autowired
    SupplierService supplierService;

    @Override
    public IPage getPage(Long offset, Long size, WorkOrder workOrder) {
        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, workOrder);
        if (ObjectUtil.isNotEmpty(page.getRecords())) {
            List<WorkOrderVo> workOrderVoList = page.getRecords();
            for (WorkOrderVo workOrderVo : workOrderVoList) {
                if (ObjectUtil.isNotEmpty(workOrderVo.getCompanyType()) && ObjectUtil.isNotEmpty(workOrderVo.getCompanyId())) {
                    if (ObjectUtil.equal(WorkOrder.COMPANY_TYPE_SUPPLIER, workOrderVo.getCompanyType())) {
                        Supplier supplier = supplierService.getById(workOrderVo.getCompanyId());
                        if (ObjectUtil.isNotEmpty(supplier) && ObjectUtil.isNotEmpty(supplier.getName())) {
                            workOrderVo.setCompanyName(supplier.getName());
                        }
                    }
                    if (ObjectUtil.equal(WorkOrder.COMPANY_TYPE_CUSTOMER, workOrderVo.getCompanyType())) {
                        Customer customer = customerService.getById(workOrderVo.getCompanyId());
                        if (ObjectUtil.isNotEmpty(customer) && ObjectUtil.isNotEmpty(customer.getName())) {
                            workOrderVo.setCompanyName(customer.getName());
                        }
                    }
                }
            }
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveWorkOrder(SaveWorkOrderQuery saveWorkOrderQuery) {
        for (WorkOrder workOrder : saveWorkOrderQuery.getWorkOrderList()) {
            workOrder.setCreateTime(System.currentTimeMillis());
            if (!StrUtil.isEmpty(saveWorkOrderQuery.getProcessor())) {
                workOrder.setProcessor(saveWorkOrderQuery.getProcessor());
            }
            workOrder.setStatus(WorkOrder.STATUS_FINISHED);
            baseMapper.insert(workOrder);
        }

        return R.ok();
    }

    @Override
    public void exportExcel(WorkOrder workOrder, HttpServletResponse response) {

        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        if (ObjectUtil.isNotEmpty(workOrderVoList)) {

            for (WorkOrderVo workOrderVo : workOrderVoList) {
                if (ObjectUtil.isNotEmpty(workOrderVo.getCompanyType()) && ObjectUtil.isNotEmpty(workOrderVo.getCompanyId())) {
                    if (ObjectUtil.equal(WorkOrder.COMPANY_TYPE_SUPPLIER, workOrderVo.getCompanyType())) {
                        Supplier supplier = supplierService.getById(workOrderVo.getCompanyId());
                        if (ObjectUtil.isNotEmpty(supplier) && ObjectUtil.isNotEmpty(supplier.getName())) {
                            workOrderVo.setCompanyName(supplier.getName());
                        }
                    }
                    if (ObjectUtil.equal(WorkOrder.COMPANY_TYPE_CUSTOMER, workOrderVo.getCompanyType())) {
                        Customer customer = customerService.getById(workOrderVo.getCompanyId());
                        if (ObjectUtil.isNotEmpty(customer) && ObjectUtil.isNotEmpty(customer.getName())) {
                            workOrderVo.setCompanyName(customer.getName());
                        }
                    }
                }
            }
        }

        if (ObjectUtil.isEmpty(workOrderVoList)) {
            throw new CustomBusinessException("没有查询到工单!无法导出！");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<WorkOrderExcelVo> workOrderExcelVoList = new ArrayList<>(workOrderVoList.size());
        for (WorkOrderVo o : workOrderVoList) {
            WorkOrderExcelVo workOrderExcelVo = new WorkOrderExcelVo();
            BeanUtil.copyProperties(o, workOrderExcelVo);
            workOrderExcelVo.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
            workOrderExcelVo.setProcessorTimeStr(simpleDateFormat.format(new Date(o.getProcessorTime())));
            workOrderExcelVoList.add(workOrderExcelVo);
        }

        String fileName = "工单.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, WorkOrderExcelVo.class).sheet("sheet").doWrite(workOrderExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    private String getStatusStr(Integer status) {
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "待处理";
                break;
            case 2:
                statusStr = "处理中";
                break;
            case 3:
                statusStr = "已完成";
                break;
        }
        return statusStr;
    }


}
