package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.WorkOrderMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SaveWorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.ProductExcelVo;
import com.xiliulou.afterserver.web.vo.ReconciliationSummaryVo;
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
import java.io.ObjectStreamClass;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;

    @Override
    public IPage getPage(Long offset, Long size, WorkOrderQuery workOrder) {
        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, workOrder);

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public R saveWorkOrder(SaveWorkOrderQuery saveWorkOrderQuery) {
        User user = userService.getUserById(saveWorkOrderQuery.getUid());
        if (Objects.isNull(user)) {
            log.error("SAVE_WORK_ORDER ERROR ,NOT FOUND USER BY ID ,ID:{}", saveWorkOrderQuery.getUid());
            return R.failMsg("用户不存在!");
        }
        for (WorkOrder workOrder : saveWorkOrderQuery.getWorkOrderList()) {
            workOrder.setCreateTime(System.currentTimeMillis());
            if (!StrUtil.isEmpty(saveWorkOrderQuery.getProcessor())) {
                workOrder.setProcessor(saveWorkOrderQuery.getProcessor());
            }
            workOrder.setCreaterId(saveWorkOrderQuery.getUid());
            workOrder.setStatus(WorkOrder.STATUS_FINISHED);
            workOrder.setOrderNo(String.valueOf(IdUtil.getSnowflake(1, 1).nextId()));
            baseMapper.insert(workOrder);
        }

        return R.ok();
    }


    @Override
    public void exportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {

        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);


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
            if (ObjectUtil.isNotEmpty(o.getProcessTime())) {
                workOrderExcelVo.setProcessorTimeStr(simpleDateFormat.format(new Date(o.getProcessTime())));
            }
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

    @Override
    public R insertWorkOrder(WorkOrderQuery workOrder) {
        User user = userService.getUserById(workOrder.getCreaterId());
        if (Objects.isNull(user)) {
            log.error("SAVE_WORK_ORDER ERROR ,NOT FOUND USER BY ID ,ID:{}", workOrder.getCreaterId());
            return R.failMsg("用户不存在!");
        }

        workOrder.setCreateTime(System.currentTimeMillis());


        workOrder.setStatus(WorkOrder.STATUS_FINISHED);
        workOrder.setOrderNo(String.valueOf(IdUtil.getSnowflake(1, 1).nextId()));
        baseMapper.insert(workOrder);
        if (ObjectUtil.isNotEmpty(workOrder.getFileNameList())) {
            List<File> filList = new ArrayList();
            for (String name : workOrder.getFileNameList()) {
                File file = new File();
                file.setFileName(name);
                file.setBindId(workOrder.getId());
                file.setType(File.TYPE_WORK_ORDER);
                file.setCreateTime(System.currentTimeMillis());
                filList.add(file);
            }
            fileService.saveBatch(filList);
        }

        return R.ok();
    }


    @Override
    public R reconciliation(WorkOrderQuery workOrder) {
        return R.ok(baseMapper.getPage(PageUtil.getPage(workOrder.getOffset(), workOrder.getSize()), workOrder));
    }

    @Override
    public void reconciliationExportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {
        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        log.info("workOrderVoList:{}", workOrderVoList);
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
            if (ObjectUtil.isNotEmpty(o.getProcessTime())) {
                workOrderExcelVo.setProcessorTimeStr(simpleDateFormat.format(new Date(o.getProcessTime())));
            }
            workOrderExcelVoList.add(workOrderExcelVo);
        }

        ExcelWriter excelWriter = null;
        try {
            String fileName = URLEncoder.encode("客商信息表", "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            ServletOutputStream outputStream = response.getOutputStream();

            excelWriter = EasyExcel.write(outputStream).build();
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "全部类型").head(WorkOrderExcelVo.class).build();
            excelWriter.write(workOrderExcelVoList, writeSheet1);
            Map<String, List<WorkOrderExcelVo>> maps = workOrderExcelVoList.stream().collect(Collectors.groupingBy(WorkOrderExcelVo::getWorkOrderType));
            if (maps.size() > 1) {
                int i = 1;
                for (Map.Entry<String, List<WorkOrderExcelVo>> entry : maps.entrySet()) {
                    String workOrderType = entry.getKey();
                    List<WorkOrderExcelVo> workOrderExcelVos = entry.getValue();
                    WriteSheet writeSheet2 = EasyExcel.writerSheet(i, workOrderType).head(WorkOrderExcelVo.class).build();
                    excelWriter.write(workOrderExcelVos, writeSheet2);
                    i++;
                }
            }
        } catch (Exception e) {
            log.error("导出报表失败!", e);
            throw new CustomBusinessException("导出报表失败!请联系客服!");
        } finally {
            excelWriter.finish();
        }
    }

    //    /**
//     * 预览
//     *
//     * @param workOrder
//     * @return
//     */
//    @Override
//    public R reconciliationPreview(WorkOrderQuery workOrder) {
//
//        return R.ok(baseMapper.reconciliationPreview(workOrder));
//    }
//
//    /**
//     * 对账 page
//     *
//     * @param workOrder
//     * @return
//     */
//    @Override
//    public R reconciliation(WorkOrderQuery workOrder) {
//        return R.ok(baseMapper.reconciliationPage(workOrder.getThirdCompanyType(), workOrder.getThirdCompanyId(), workOrder.getCreateTimeStart(), workOrder.getCreateTimeEnd()));
//    }
}
