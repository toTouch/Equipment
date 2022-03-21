package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.mapper.WorkOrderMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.*;
import com.xiliulou.afterserver.web.vo.*;
import io.micrometer.core.instrument.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Resource
    ProductSerialNumberMapper productSerialNumberMapper;
    @Autowired
    PointService pointService;
    @Autowired
    DeliverService deliverService;
    @Autowired
    ServerService serverService;
    @Autowired
    WorkOrderTypeService workOrderTypeService;
    @Autowired
    WorkOrderReasonService workOrderReasonService;
    @Autowired
    PointNewService pointNewService;
    @Autowired
    WarehouseService warehouseService;
    @Autowired
    ProductService productService;


    @Override
    public IPage getPage(Long offset, Long size, WorkOrderQuery workOrder) {
        if (Objects.nonNull(workOrder.getWorkOrderType())) {
            workOrder.setType(workOrder.getWorkOrderType().toString());
        }
        //检查质保过期点位
        pointNewService.updatePastWarrantyStatus();

        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, workOrder);

        List<WorkOrderVo> list = (List<WorkOrderVo>)page.getRecords();
        if (list.isEmpty()){
            return page;
        }

        list.forEach(item -> {

            item.setPaymentMethodName(getPaymentMethod(item.getPaymentMethod()));

            if (Objects.nonNull(item.getCreaterId())){
                User userById = userService.getUserById(item.getCreaterId());
                if (Objects.nonNull(userById)){
                    item.setUserName(userById.getUserName());
                }
            }

            if (Objects.nonNull(item.getPointId())){
                PointNew pointNew = pointNewService.queryByIdFromDB(item.getPointId());
                if (Objects.nonNull(pointNew)){
                    item.setPointName(pointNew.getName());
                }
            }

            if(Objects.equals(item.getDestinationType(), "2")){
                WareHouse wareHouse = warehouseService.getById(item.getTransferDestinationPointId());
                if (Objects.nonNull(wareHouse)){
                    item.setTransferDestinationPointName(wareHouse.getWareHouses());
                }
            }

            if(Objects.equals(item.getSourceType(), "2")){
                WareHouse wareHouse = warehouseService.getById(item.getTransferSourcePointId());
                if (Objects.nonNull(wareHouse)){
                    item.setTransferSourcePointName(wareHouse.getWareHouses());
                }
            }

            if(Objects.nonNull(item.getProcessTime())){
                String prescription = DateUtils.getDatePoor(item.getProcessTime() , item.getCreateTime());
                item.setPrescription(prescription);
                item.setPrescriptionMillis(item.getProcessTime() - item.getCreateTime());
            }

            if(Objects.nonNull(item.getProductInfo())) {
                List<ProductInfoQuery> productInfo = JSON.parseArray(item.getProductInfo(), ProductInfoQuery.class);
                item.setProductInfoList(productInfo);
            }

            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(Objects.nonNull(workOrder.getWorkOrderType()),File::getFileType, workOrder.getWorkOrderType())
                    .eq(File::getBindId, item.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            item.setFileList(fileList);
            item.setFileCount(CollectionUtil.isEmpty(fileList) ? 0 : fileList.size());

            item.setParentWorkOrderReason(this.getParentWorkOrderReason(item.getWorkOrderReasonId()));
        });
        page.setRecords(list);
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
            workOrder.setProcessTime(System.currentTimeMillis());
            if (!StrUtil.isEmpty(saveWorkOrderQuery.getProcessor())) {
                workOrder.setProcessor(saveWorkOrderQuery.getProcessor());
            }
//            workOrder.setCreateId(saveWorkOrderQuery.getUid());
            workOrder.setStatus(WorkOrder.STATUS_FINISHED);
            workOrder.setOrderNo(RandomUtil.randomString(10));
            baseMapper.insert(workOrder);
        }

        return R.ok();
    }


    @Override
    public void exportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {
        workOrder.setType(String.valueOf(workOrder.getWorkOrderType()));
        if("null".equals(workOrder.getType())){
            workOrder.setType(null);
        }
        //扫描质保过期点位
        pointNewService.updatePastWarrantyStatus();

        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);


        if (ObjectUtil.isEmpty(workOrderVoList)) {
            throw new CustomBusinessException("没有查询到工单!无法导出！");
        }

        if(StringUtils.isBlank(workOrder.getType()) || "4".equals(workOrder.getType())){
            exportExcelMoveMachine(workOrder, workOrderVoList, response);
        }else{
            exportExcelNotMoveMachine(workOrder, workOrderVoList, response);
        }
    }

    private void exportExcelMoveMachine(WorkOrderQuery workOrder, List<WorkOrderVo> workOrderVoList, HttpServletResponse response){
        //headerSet
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"审核状态","工单类型", "点位","点位状态", "移机起点", "移机终点",  "创建人",
                "状态", "时效", "描述",  "备注",  "工单原因", "第三方原因" , "第三方公司",
                "第三方费用", "费用", "图片数量","处理时间","创建时间","服务商", "结算方式",
                "第三方责任对接人", "工单编号", "第三方结算状态", "sn码","审核内容"};

        List<Product> productAll = productService.list();


        for(String s : header){
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(s);
            headList.add(headTitle);
        }

        if(productAll != null && !productAll.isEmpty()){
            for (Product p : productAll){
                List<String> headTitle = new ArrayList<>();
                headTitle.add(p.getName());
                headList.add(headTitle);
            }
        }

        table.setHead(headList);

        List<List<Object>> data = new ArrayList<>();
        Integer codeMaxSize = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (WorkOrderVo o : workOrderVoList) {
            List<Object> row = new ArrayList<>();
            //审核状态
            row.add(this.getAuditStatus(o.getAuditStatus()));

            //typeName
            if(Objects.nonNull(o.getType())){
                WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
                if (Objects.nonNull(workOrderType)){
                    row.add(workOrderType.getType());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }
            //pointName
            if(Objects.nonNull(o.getPointId())){
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)){
                    row.add(pointNew.getName());
                    //row.add(pointNew.getSnNo());
                }else{
                    row.add("");
                    //row.add("");
                }
            }else{
                row.add("");
                //row.add("");
            }

            row.add(getPointStatusName(o.getPointStatus()));

            //transferSourcePointId
            if("1".equals(o.getSourceType())){
                PointNew pointNew = pointNewService.getById(o.getTransferSourcePointId());
                if (Objects.nonNull(pointNew)){
                    row.add(pointNew.getName());
                }else{
                    row.add("");
                }
            }else if("2".equals(o.getSourceType())){
                WareHouse warehouse = warehouseService.getById(o.getTransferSourcePointId());
                if (Objects.nonNull(warehouse)){
                    row.add(warehouse.getWareHouses());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //transfer_destination_point_id
            if("1".equals(o.getDestinationType())){
                PointNew pointNew = pointNewService.getById(o.getTransferDestinationPointId());
                if (Objects.nonNull(pointNew)){
                    row.add(pointNew.getName());
                }else{
                    row.add("");
                }
            }else if("2".equals(o.getDestinationType())){
                WareHouse warehouse = warehouseService.getById(o.getTransferDestinationPointId());
                if (Objects.nonNull(warehouse)){
                    row.add(warehouse.getWareHouses());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //"创建人",
            if(Objects.nonNull(o.getCreaterId())){
                User user = userService.getUserById(o.getCreaterId());
                if (Objects.nonNull(user)){
                    row.add(user.getUserName());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //"状态",
            //status  1;待处理2:已处理3:待分析4：已完结
            if(Objects.nonNull(o.getStatus())){
                row.add(this.getStatusStr(o.getStatus()));
            }

            //时效
            String prescription = DateUtils.getDatePoor(o.getProcessTime(), o.getCreateTime());
            row.add(StringUtils.isBlank(prescription)?"":prescription);

            //"描述", "
            row.add(o.getDescribeinfo() == null? "" : o.getDescribeinfo());

            //"备注",
            row.add(o.getInfo() == null? "" : o.getInfo());

            //"工单原因",
            //workOrderReasonName
            if(Objects.nonNull(o.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    row.add(workOrderReason.getName());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //第三方原因
            row.add(o.getThirdReason() == null? "" : o.getThirdReason());

            if (Objects.nonNull(o.getThirdCompanyType())){
                this.setThirdCompanyNameAndServerName(o);
                row.add(o.getThirdCompanyName());
            }else{
                row.add("");
            }

            //third_company_pay
            row.add(o.getThirdCompanyPay() == null ? "" : o.getThirdCompanyPay());

            row.add(o.getFee() == null? "" : o.getFee());

            //图片数量
            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(Objects.nonNull(workOrder.getWorkOrderType()),File::getFileType, workOrder.getWorkOrderType())
                    .eq(File::getBindId, o.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            row.add(CollectionUtils.isEmpty(fileList) ? 0 : fileList.size());

            //"处理时间",
            //processTime
            if(Objects.nonNull(o.getProcessTime())){
                row.add(simpleDateFormat.format(new Date(o.getProcessTime())));
            }else{
                row.add("");
            }

            //创建时间"
            if(Objects.nonNull(o.getCreateTime())){
                row.add(simpleDateFormat.format(new Date(o.getCreateTime())));
            }else{
                row.add("");
            }
            //服务商
            row.add(o.getServerName() == null ? "" : o.getServerName());

            //PaymentMethod
            row.add(getPaymentMethod(o.getPaymentMethod()));


            //"第三方责任对接人"
            row.add(o.getThirdResponsiblePerson() == null? "" : o.getThirdResponsiblePerson());

            //"工单编号",
            row.add(o.getOrderNo() == null? "" : o.getOrderNo());

            //thirdPaymentStatus
            if(Objects.nonNull(o.getThirdPaymentStatus())){
                if(o.getThirdPaymentStatus().equals(1)){
                    row.add("无需结算");
                }else if(o.getThirdPaymentStatus().equals(2)){
                    row.add("未结算");
                }else if(o.getThirdPaymentStatus().equals(3)){
                    row.add("已结算");
                }else {
                    row.add("");
                }
            }else{
                row.add("");
            }

            //sn
            if(Objects.nonNull(o.getPointId())){
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)){
                    row.add(pointNew.getSnNo());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //审核备注
            row.add(o.getAuditRemarks() == null? "" : o.getAuditRemarks());



            //产品个数
            if(productAll != null && !productAll.isEmpty()) {
                List<ProductInfoQuery> productInfoQueries = null;
                if(Objects.nonNull(o.getProductInfo())){
                    productInfoQueries = JSON.parseArray(o.getProductInfo(), ProductInfoQuery.class);
                }
                if (!CollectionUtil.isEmpty(productInfoQueries)) {
                    for (Product p : productAll) {
                        ProductInfoQuery index = null;
                        for (ProductInfoQuery entry : productInfoQueries) {
                            if (Objects.equals(p.getId(), entry.getProductId())) {
                                index = entry;
                            }
                        }

                        if (index != null) {
                            row.add(index.getNumber());
                        } else {
                            row.add("");
                        }
                    }
                }else{
                    for (Product p : productAll) {
                        row.add("");
                    }
                }
            }

            List<String> codes = JSON.parseArray(o.getCode(), String.class);
            if(!CollectionUtils.isEmpty(codes)){
                codeMaxSize = codeMaxSize > codes.size() ? codeMaxSize : codes.size();
                for (String code : codes){
                    row.add(code);
                }
            }

            data.add(row);
        }

        for(int i= 1 ; i < codeMaxSize + 1; i++){
            List<String> headTitle = new ArrayList<String>();
            headTitle.add("产品编码" + i);
            headList.add(headTitle);
        }

        String fileName = "工单列表.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(data,sheet,table).finish();
            //EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    private void exportExcelNotMoveMachine(WorkOrderQuery workOrder, List<WorkOrderVo> workOrderVoList, HttpServletResponse response){

        //headerSet
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"审核状态","工单类型", "点位", "点位状态", "创建人",
                "状态", "时效", "描述",  "备注",  "工单原因", "第三方原因" , "第三方公司",
                "第三方费用", "费用", "图片数量","处理时间","创建时间","服务商", "结算方式",
                "第三方责任对接人", "工单编号", "第三方结算状态", "sn码","审核内容"};

        List<Product> productAll = productService.list();


        for(String s : header){
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(s);
            headList.add(headTitle);
        }

        if(productAll != null && !productAll.isEmpty()){
            for (Product p : productAll){
                List<String> headTitle = new ArrayList<>();
                headTitle.add(p.getName());
                headList.add(headTitle);
            }
        }

        table.setHead(headList);

        List<List<Object>> data = new ArrayList<>();
        Integer codeMaxSize = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (WorkOrderVo o : workOrderVoList) {
            List<Object> row = new ArrayList<>();
            //审核状态
            row.add(this.getAuditStatus(o.getAuditStatus()));

            //typeName
            if(Objects.nonNull(o.getType())){
                WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
                if (Objects.nonNull(workOrderType)){
                    row.add(workOrderType.getType());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }
            //pointName
            if(Objects.nonNull(o.getPointId())){
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)){
                    row.add(pointNew.getName());
                    //row.add(pointNew.getSnNo());
                }else{
                    row.add("");
                    //row.add("");
                }
            }else{
                row.add("");
                //row.add("");
            }

            row.add(getPointStatusName(o.getPointStatus()));

            //"创建人",
            if(Objects.nonNull(o.getCreaterId())){
                User user = userService.getUserById(o.getCreaterId());
                if (Objects.nonNull(user)){
                    row.add(user.getUserName());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //"状态",
            //status  1;待处理2:已处理3:待分析4：已完结
            if(Objects.nonNull(o.getStatus())){
                row.add(this.getStatusStr(o.getStatus()));
            }

            //时效
            String prescription = DateUtils.getDatePoor(o.getProcessTime(), o.getCreateTime());
            row.add(StringUtils.isBlank(prescription) ? "" : prescription);

            //"描述", "
            row.add(o.getDescribeinfo() == null? "" : o.getDescribeinfo());

            //"备注",
            row.add(o.getInfo() == null? "" : o.getInfo());

            //"工单原因",
            //workOrderReasonName
            if(Objects.nonNull(o.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    row.add(workOrderReason.getName());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //第三方原因
            row.add(o.getThirdReason() == null? "" : o.getThirdReason());

            if (Objects.nonNull(o.getThirdCompanyType())){
                this.setThirdCompanyNameAndServerName(o);
                row.add(o.getThirdCompanyName());
            }else{
                row.add("");
            }

            //third_company_pay
            row.add(o.getThirdCompanyPay() == null ? "" : o.getThirdCompanyPay());

            row.add(o.getFee() == null? "" : o.getFee());

            //图片数量
            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(Objects.nonNull(workOrder.getWorkOrderType()),File::getFileType, workOrder.getWorkOrderType())
                    .eq(File::getBindId, o.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            row.add(CollectionUtils.isEmpty(fileList) ? 0 : fileList.size());

            //"处理时间",
            //processTime
            if(Objects.nonNull(o.getProcessTime())){
                row.add(simpleDateFormat.format(new Date(o.getProcessTime())));
            }else{
                row.add("");
            }

            //创建时间"
            if(Objects.nonNull(o.getCreateTime())){
                row.add(simpleDateFormat.format(new Date(o.getCreateTime())));
            }else{
                row.add("");
            }
            //服务商
            row.add(o.getServerName() == null ? "" : o.getServerName());

            //PaymentMethod
            row.add(getPaymentMethod(o.getPaymentMethod()));


            //"第三方责任对接人"
            row.add(o.getThirdResponsiblePerson() == null? "" : o.getThirdResponsiblePerson());

            //"工单编号",
            row.add(o.getOrderNo() == null? "" : o.getOrderNo());

            //thirdPaymentStatus
            if(Objects.nonNull(o.getThirdPaymentStatus())){
                if(o.getThirdPaymentStatus().equals(1)){
                    row.add("无需结算");
                }else if(o.getThirdPaymentStatus().equals(2)){
                    row.add("未结算");
                }else if(o.getThirdPaymentStatus().equals(3)){
                    row.add("已结算");
                }else {
                    row.add("");
                }
            }else{
                row.add("");
            }

            //sn
            if(Objects.nonNull(o.getPointId())){
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)){
                    row.add(pointNew.getSnNo());
                }else{
                    row.add("");
                }
            }else{
                row.add("");
            }

            //审核备注
            row.add(o.getAuditRemarks() == null? "" : o.getAuditRemarks());

            //产品个数
            if(productAll != null && !productAll.isEmpty()) {
                List<ProductInfoQuery> productInfoQueries = null;
                if(Objects.nonNull(o.getProductInfo())){
                    productInfoQueries = JSON.parseArray(o.getProductInfo(), ProductInfoQuery.class);
                }
                if (!CollectionUtil.isEmpty(productInfoQueries)) {
                    for (Product p : productAll) {
                        ProductInfoQuery index = null;
                        for (ProductInfoQuery entry : productInfoQueries) {
                            if (Objects.equals(p.getId(), entry.getProductId())) {
                                index = entry;
                            }
                        }

                        if (index != null) {
                            row.add(index.getNumber());
                        } else {
                            row.add("");
                        }
                    }
                }else{
                    for (Product p : productAll) {
                        row.add("");
                    }
                }
            }

            data.add(row);
        }



        String fileName = "工单列表.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(data,sheet,table).finish();
            //EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    private String getPointStatusName(Integer pointStatus){
        String pointStatusName = "";
        if (Objects.equals(pointStatus,1)){
            pointStatusName = "移机";
        }else if (Objects.equals(pointStatus,2)){
            pointStatusName = "运营中";
        }else if (Objects.equals(pointStatus,3)){
            pointStatusName = "已拆机";
        }else if (Objects.equals(pointStatus,4)){
            pointStatusName = "初始化";
        }else if (Objects.equals(pointStatus,5)){
            pointStatusName = "待安装";
        }else if (Objects.equals(pointStatus,6)){
            pointStatusName = "运输中";
        }else if (Objects.equals(pointStatus,7)){
            pointStatusName = "安装中";
        }else if (Objects.equals(pointStatus,8)){
            pointStatusName = "安装完成";
        }else if (Objects.equals(pointStatus,9)){
            pointStatusName = "已暂停";
        }else if (Objects.equals(pointStatus,10)){
            pointStatusName = "已取消";
        }else if (Objects.equals(pointStatus,11)){
            pointStatusName = "已过保";
        }
        return pointStatusName;
    }

    /*private void exportExcelNotMoveMachine(WorkOrderQuery workOrder, List<WorkOrderVo> workOrderVoList, HttpServletResponse response){


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<WorkOrderListExcelVo> workOrderExcelVoList = new ArrayList<>(workOrderVoList.size());
        for (WorkOrderVo o : workOrderVoList) {
            WorkOrderListExcelVo workOrderExcelVo = new WorkOrderListExcelVo();
            BeanUtil.copyProperties(o, workOrderExcelVo);
            //审核
            workOrderExcelVo.setAuditStatus(getAuditStatus(o.getAuditStatus()));
            workOrderExcelVo.setAuditRemarks(o.getAuditRemarks());
            //typeName
            if(Objects.nonNull(o.getType())){
                WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
                if (Objects.nonNull(workOrderType)){
                    workOrderExcelVo.setTypeName(workOrderType.getType());
                }
            }
            //thirdReason
            workOrderExcelVo.setThirdReason(o.getThirdReason());
            //pointName
            if(Objects.nonNull(o.getPointId())){
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)){
                    workOrderExcelVo.setPointName(pointNew.getName());
                    workOrderExcelVo.setSnNo(pointNew.getSnNo());
                }
            }
            //PaymentMethod
            workOrderExcelVo.setPaymentMethodName(getPaymentMethod(o.getPaymentMethod()));
            //workOrderReasonName
            if(Objects.nonNull(o.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    workOrderExcelVo.setWorkOrderReasonName(workOrderReason.getName());
                }
            }
            //processTime
            if(Objects.nonNull(o.getProcessTime())){
                workOrderExcelVo.setProcessTime(simpleDateFormat.format(new Date(o.getProcessTime())));
            }
            //status  1;待处理2:已处理3:待分析4：已完结
            if(Objects.nonNull(o.getStatus())){
                workOrderExcelVo.setStatusName(this.getStatusStr(o.getStatus()));
            }
            //createrName
            if(Objects.nonNull(o.getCreaterId())){
                User user = userService.getUserById(o.getCreaterId());
                if (Objects.nonNull(user)){
                    workOrderExcelVo.setCreaterName(user.getUserName());
                }
            }


            workOrderExcelVo.setThirdCompanyName(o.getThirdCompanyName());
            //thirdPaymentStatus
            if(Objects.nonNull(o.getThirdPaymentStatus())){
                String thirdPaymentStatus = getThirdPaymentStatus(o.getThirdPaymentStatus());
                workOrderExcelVo.setThirdPaymentStatus(thirdPaymentStatus);
            }

            //createTime
            if(Objects.nonNull(o.getCreateTime())){
                workOrderExcelVo.setCreateTime(simpleDateFormat.format(new Date(o.getCreateTime())));
            }
            //时效
            String prescription = DateUtils.getDatePoor(o.getProcessTime(), o.getCreateTime());
            workOrderExcelVo.setPrescription(prescription);
            //服务商
            workOrderExcelVo.setServerName(o.getServerName());
            //图片数量
            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(Objects.nonNull(workOrder.getWorkOrderType()),File::getFileType, workOrder.getWorkOrderType())
                    .eq(File::getBindId, o.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            workOrderExcelVo.setFileCount(CollectionUtils.isEmpty(fileList) ? 0 : fileList.size());

            workOrderExcelVoList.add(workOrderExcelVo);
        }

        String fileName = "工单列表.xls";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, WorkOrderListExcelVo.class).sheet("sheet").doWrite(workOrderExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }*/

    private String getOverInsurance(Integer overInsurance){
        String overInsuranceStr = "";
        switch (overInsurance){
            case 0: overInsuranceStr = "未过保"; break;
            case 1: overInsuranceStr = "已过保"; break;
        }
        return overInsuranceStr;
    }

    private String getPaymentMethod(Integer method){
        String methodStr = "";
        method = method == null ? 0 : method;
        switch (method) {
            case 1:
                methodStr = "月结";
                break;
            case 2:
                methodStr = "现结";
                break;
        }
        return methodStr;
    }

    private String getThirdPaymentStatus(Integer status){
        String statusStr = "";
        status = status == null ? 0 : status;
        switch (status) {
            case 1:
                statusStr = "无需结算";
                break;
            case 2:
                statusStr = "未结算";
                break;
            case 3:
                statusStr = "已结算";
                break;
        }
        return statusStr;
    }

    private String getStatusStr(Integer status) {
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "待处理";
                break;
            case 2:
                statusStr = "已处理";
                break;
            case 3:
                statusStr = "待分析";
                break;
            case 4:
                statusStr = "已完结";
                break;
            case 5:
                statusStr = "已暂停";
                break;
        }
        return statusStr;
    }

    private String getAuditStatus(Integer auditStatus){
        String auditStatusStr = "";
        auditStatus = auditStatus == null ? -1 : auditStatus;

        switch (auditStatus) {
            case 1:
                auditStatusStr = "待审核";
                break;
            case 2:
                auditStatusStr = "未通过";
                break;
            case 3:
                auditStatusStr = "通过";
                break;
        }
        return auditStatusStr;
    }

    private String getThirdCompanyType(Integer thirdCompanyType){
        String thirdCompanyTypeStr = "";
        thirdCompanyType = thirdCompanyType == null ? -1 : thirdCompanyType;
        switch (thirdCompanyType) {
            case 1:
                thirdCompanyTypeStr = "客户";
                break;
            case 2:
                thirdCompanyTypeStr = "供应商";
                break;
            case 3:
                thirdCompanyTypeStr = "服务商";
                break;
        }
        return thirdCompanyTypeStr;
    }

    @Override
    public R insertWorkOrder(WorkOrderQuery workOrder) {
        User user = userService.getUserById(workOrder.getCreaterId());
        if (Objects.isNull(user)) {
            log.error("SAVE_WORK_ORDER ERROR ,NOT FOUND USER BY ID ,ID:{}", workOrder.getCreaterId());
            return R.failMsg("用户不存在!");
        }
        if (ObjectUtil.equal(WorkOrderType.TRANSFER, workOrder.getWorkOrderType()) && ObjectUtil.isNotEmpty(workOrder.getProductSerialNumberIdList())) {
            //移机
            //转移产品序列号
            if (Objects.isNull(workOrder.getTransferSourcePointId()) || Objects.isNull(workOrder.getTransferDestinationPointId())) {
                return R.failMsg("转移点位起点和终点不能为空!");
            }
            for (Long id : workOrder.getProductSerialNumberIdList()) {
                ProductSerialNumber productSerialNumber = productSerialNumberMapper.selectById(id);
                if (Objects.isNull(productSerialNumber)) {
                    throw new CustomBusinessException("未找到产品id为" + id + "的产品!");
                }
                productSerialNumber.setPointId(workOrder.getTransferDestinationPointId());
                productSerialNumberMapper.updateById(productSerialNumber);
            }
        }
        workOrder.setProcessTime(System.currentTimeMillis());


        workOrder.setStatus(WorkOrder.STATUS_FINISHED);
        workOrder.setOrderNo(RandomUtil.randomString(10));
        baseMapper.insert(workOrder);
        if (ObjectUtil.isNotEmpty(workOrder.getFileList())) {
            List<File> filList = new ArrayList();
            for (String name : workOrder.getFileList()) {
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
        //workOrder.setProcessTimeStart(workOrder.getCreateTimeStart());
        //workOrder.setProcessTimeEnd(workOrder.getCreateTimeEnd());

        Integer count = baseMapper.countOrderList(workOrder);
        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        workOrderVoList.forEach(o -> {

            o.setPaymentMethodName(getPaymentMethod(o.getPaymentMethod()));

            WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
            if (Objects.nonNull(workOrderType)){
                o.setWorkOrderType(workOrderType.getType());
            }

            PointNew point = pointNewService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                o.setPointName(point.getName());
            }
            WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());

            if (Objects.nonNull(workOrderReason)) {
                o.setWorkOrderReasonName(workOrderReason.getName());
            }
            o.setThirdCompanyPay(o.getThirdCompanyPay());

            setThirdCompanyNameAndServerName(o);
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",count);
        map.put("data",workOrderVoList);
        return R.ok(map);
    }

    private void setThirdCompanyNameAndServerName(WorkOrderVo o){
        if (o.getThirdCompanyType() != null && o.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)){
            Customer customer = customerService.getById(o.getThirdCompanyId());
            if (Objects.nonNull(customer)){
                o.setThirdCompanyName(customer.getName());
            }
        }

        if (o.getThirdCompanyType() != null && o.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)){
            Supplier supplier = supplierService.getById(o.getThirdCompanyId());
            if(Objects.nonNull(supplier)){
                o.setThirdCompanyName(supplier.getName());
            }
        }

        if (o.getThirdCompanyType() != null && o.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SERVER)){
            Server server = serverService.getById(o.getThirdCompanyId());
            if(Objects.nonNull(server)){
                o.setThirdCompanyName(server.getName());
            }
        }

        if (o.getServerId()!=null){
            Server server = serverService.getById(o.getServerId());
            if (Objects.nonNull(server)){
                o.setServerName(server.getName());
            }
        }
    }

    @Override
    public List<WorkOrderVo> getWorkOrderList(WorkOrderQuery workOrder) {
        return baseMapper.orderList(workOrder);
    }

    @Override
    public List<WorkOrder> staffFuzzy(String accessToken) {
        return this.baseMapper.selectList(new QueryWrapper<WorkOrder>().like("info", accessToken));
    }
    @Override
    public void reconciliationExportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {
        if (workOrder.getCreateTimeStart() == null || workOrder.getCreateTimeEnd() == null){
            throw new CustomBusinessException("请选择创建开始时间结束时间");
        }

        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        log.info("workOrderVoList:{}", workOrderVoList);
        if (ObjectUtil.isEmpty(workOrderVoList)) {
            throw new CustomBusinessException("没有查询到工单!无法导出！");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<WorkOrderExcelVo> excelVoList = new ArrayList<>(workOrderVoList.size());

        workOrderVoList.stream().forEach(item -> {
            WorkOrderExcelVo workOrderExcelVo = new WorkOrderExcelVo();

            workOrderExcelVo.setThirdCompanyType(getThirdCompanyType(item.getThirdCompanyType()));

//            setThirdCompanyNameAndServerName(item);
            workOrderExcelVo.setThirdCompanyName(item.getThirdCompanyName());
            workOrderExcelVo.setServerName(item.getServerName());

            WorkOrderType workOrderType = workOrderTypeService.getById(item.getType());
            if (Objects.nonNull(workOrderType)){
                workOrderExcelVo.setWorkOrderType(workOrderType.getType());
            }

            PointNew point = pointNewService.getById(item.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo.setPointName(point.getName());
            }

            if (Objects.nonNull(item.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(item.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    workOrderExcelVo.setWorkOrderReasonName(workOrderReason.getName());
                }
            }

            workOrderExcelVo.setPaymentMethodName(getPaymentMethod(item.getPaymentMethod()));
            workOrderExcelVo.setThirdPaymentStatus(getThirdPaymentStatus(item.getThirdPaymentStatus()));
            workOrderExcelVo.setThirdCompanyPay(item.getThirdCompanyPay());
            workOrderExcelVo.setRemarks(item.getInfo());
            workOrderExcelVo.setDescribeinfo(item.getDescribeinfo());
            workOrderExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(item.getCreateTime())));
            if(!Objects.isNull(item.getProcessTime())){
                workOrderExcelVo.setProcessTimeStr(simpleDateFormat.format(new Date(item.getProcessTime())));
            }

            excelVoList.add(workOrderExcelVo);
        });

        String fileName = "工单结算.xls";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, WorkOrderExcelVo.class).sheet("sheet").doWrite(excelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    //@Override
    /*public void reconciliationExportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {

        if (workOrder.getCreateTimeStart() == null || workOrder.getCreateTimeEnd() == null){
            throw new CustomBusinessException("请选择创建开始时间结束时间");
        }

        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        log.info("workOrderVoList:{}", workOrderVoList);
        if (ObjectUtil.isEmpty(workOrderVoList)) {
            throw new CustomBusinessException("没有查询到工单!无法导出！");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<WorkOrderExcelVo> customerExcelVoList = new ArrayList<>(workOrderVoList.size());
        List<WorkOrderExcelVo2> supplierExcelVoList = new ArrayList<>(workOrderVoList.size());
        List<WorkOrderExcelVo3> serverExcelVoList = new ArrayList<>(workOrderVoList.size());

        AtomicInteger customerPayAmount = new AtomicInteger();
        AtomicInteger supplierPayAmount = new AtomicInteger();
        AtomicInteger serverPayAmount = new AtomicInteger();




        workOrderVoList.forEach(o -> {
            WorkOrderExcelVo workOrderExcelVo = new WorkOrderExcelVo();

            // TODO: 2021/6/8 0008 工单原因
            WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
            if (Objects.nonNull(workOrderType)){
                workOrderExcelVo.setWorkOrderType(workOrderType.getType());
            }

            PointNew point = pointNewService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo.setPointName(point.getName());
            }

            if (Objects.nonNull(o.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    workOrderExcelVo.setWorkOrderReasonName(workOrderReason.getName());
                }
            }
            workOrderExcelVo.setDescribeinfo(o.getDescribeinfo());
            workOrderExcelVo.setRemarks(o.getInfo());
            workOrderExcelVo.setThirdCompanyPay(o.getThirdCompanyPay());
//            workOrderExcelVo.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo.setPaymentMethodName(getPaymentMethod(o.getPaymentMethod()));
            workOrderExcelVo.setThirdPaymentStatus(getThirdPaymentStatus(o.getThirdPaymentStatus()));
            workOrderExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
            if(!Objects.isNull(o.getProcessTime())){
                workOrderExcelVo.setProcessTimeStr(simpleDateFormat.format(new Date(o.getProcessTime())));
            }

            if (o.getThirdCompanyType() != null && o.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)){
                Customer customer = customerService.getById(o.getThirdCompanyId());
                if (Objects.nonNull(customer)){
                    workOrderExcelVo.setThirdCompanyName(customer.getName());
                }
                workOrderExcelVo.setThirdCompanyType("客户");
                if (o.getThirdCompanyPay()!=null) {
                    customerPayAmount.addAndGet((int) o.getThirdCompanyPay().doubleValue());
                }
                customerExcelVoList.add(workOrderExcelVo);
            }

           if(Objects.nonNull(o.getServerId())){
               Server server = serverService.getById(o.getServerId());
               if (Objects.nonNull(server)){
                   workOrderExcelVo.setServerName(server.getName());
               }
           }
        });
//        WorkOrderExcelVo tailLine = new WorkOrderExcelVo();
//        tailLine.setThirdCompanyType("客户总金额");
//        tailLine.setThirdCompanyName(customerPayAmount.toString());
//        customerExcelVoList.add(tailLine);


        workOrderVoList.forEach(o -> {
            WorkOrderExcelVo2 workOrderExcelVo2 = new WorkOrderExcelVo2();
            // TODO: 2021/6/8 0008 工单原因
            WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
            if (Objects.nonNull(workOrderType)){
                workOrderExcelVo2.setWorkOrderType(workOrderType.getType());
            }

            PointNew point = pointNewService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo2.setPointName(point.getName());
            }

            if (Objects.nonNull(o.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    workOrderExcelVo2.setWorkOrderReasonName(workOrderReason.getName());
                }
            }

            workOrderExcelVo2.setRemarks(o.getInfo());
            workOrderExcelVo2.setThirdCompanyPay(o.getThirdCompanyPay());
            workOrderExcelVo2.setDescribeinfo(o.getDescribeinfo());
//            workOrderExcelVo2.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo2.setPaymentMethodName(getPaymentMethod(o.getPaymentMethod()));
            workOrderExcelVo2.setThirdPaymentStatus(getThirdPaymentStatus(o.getThirdPaymentStatus()));
            workOrderExcelVo2.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
            if(!Objects.isNull(o.getProcessTime())){
                workOrderExcelVo2.setProcessTimeStr(simpleDateFormat.format(new Date(o.getProcessTime())));
            }
            if (o.getThirdCompanyType() != null && o.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)){
                Supplier supplier = supplierService.getById(o.getThirdCompanyId());
                if(Objects.nonNull(supplier)){
                    workOrderExcelVo2.setThirdCompanyName(supplier.getName());
                }
                workOrderExcelVo2.setThirdCompanyType("供应商");
                if (o.getThirdCompanyPay()!=null) {
                    supplierPayAmount.addAndGet((int) o.getThirdCompanyPay().doubleValue());
                }
                supplierExcelVoList.add(workOrderExcelVo2);
            }

            if(Objects.nonNull(o.getServerId())){
                Server server = serverService.getById(o.getServerId());
                if (Objects.nonNull(server)){
                    workOrderExcelVo2.setServerName(server.getName());
                }
            }
        });

//        WorkOrderExcelVo2 tailLine2 = new WorkOrderExcelVo2();
//        tailLine2.setThirdCompanyType("供应商总金额");
//        tailLine2.setThirdCompanyName(supplierPayAmount.toString());
//        supplierExcelVoList.add(tailLine2);


        workOrderVoList.forEach(o -> {
            WorkOrderExcelVo3 workOrderExcelVo3 = new WorkOrderExcelVo3();

            // TODO: 2021/6/8 0008 工单原因
            WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
            if (Objects.nonNull(workOrderType)){
                workOrderExcelVo3.setWorkOrderType(workOrderType.getType());
            }


            if (Objects.nonNull(o.getWorkOrderReasonId())){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)){
                    workOrderExcelVo3.setWorkOrderReasonName(workOrderReason.getName());
                }
            }

            PointNew point = pointNewService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo3.setPointName(point.getName());
            }
            workOrderExcelVo3.setRemarks(o.getInfo());
            workOrderExcelVo3.setThirdCompanyPay(o.getThirdCompanyPay());
            workOrderExcelVo3.setDescribeinfo(o.getDescribeinfo());
//            workOrderExcelVo3.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo3.setPaymentMethodName(getPaymentMethod(o.getPaymentMethod()));
            workOrderExcelVo3.setThirdPaymentStatus(getThirdPaymentStatus(o.getThirdPaymentStatus()));
            workOrderExcelVo3.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
            if(!Objects.isNull(o.getProcessTime())){
                workOrderExcelVo3.setProcessTimeStr(simpleDateFormat.format(new Date(o.getProcessTime())));
            }
            if (o.getServerId()!=null){
                Server server = serverService.getById(o.getThirdCompanyId());
                if (Objects.nonNull(server)){
                    workOrderExcelVo3.setThirdCompanyName(server.getName());
                }
                workOrderExcelVo3.setThirdCompanyType("服务商");
                if (o.getThirdCompanyPay()!=null) {
                    serverPayAmount.addAndGet((o.getFee() == null ? 0 : o.getFee().intValue()));
                }
                workOrderExcelVo3.setThirdCompanyPay(o.getFee());
                serverExcelVoList.add(workOrderExcelVo3);
            }

            if(Objects.nonNull(o.getServerId())){
                Server server = serverService.getById(o.getServerId());
                if (Objects.nonNull(server)){
                    workOrderExcelVo3.setServerName(server.getName());
                }
            }

        });
//        WorkOrderExcelVo3 tailLine3 = new WorkOrderExcelVo3();
//        tailLine3.setThirdCompanyType("服务商总金额");
//        tailLine3.setThirdCompanyName(serverPayAmount.toString());
//        serverExcelVoList.add(tailLine3);

        ExcelWriter excelWriter = null;
        try {
            String fileName = URLEncoder.encode("客商信息表", "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
            ServletOutputStream outputStream = response.getOutputStream();
            excelWriter = EasyExcel.write(outputStream).build();

            //总的导表

            WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "客户").head(WorkOrderExcelVo.class).build();
            excelWriter.write(customerExcelVoList, writeSheet1);

            WriteSheet writeSheet2 = EasyExcel.writerSheet(1, "供应商").head(WorkOrderExcelVo2.class).build();
            excelWriter.write(supplierExcelVoList, writeSheet2);

            WriteSheet writeSheet3 = EasyExcel.writerSheet(2, "服务商").head(WorkOrderExcelVo3.class).build();
            excelWriter.write(serverExcelVoList, writeSheet3);

        } catch (Exception e) {
            log.error("导出报表失败!", e);
            throw new CustomBusinessException("导出报表失败!请联系客服!");
        } finally {
            excelWriter.finish();
        }
    }*/




    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertSerialNumber(ProductSerialNumberQuery productSerialNumberQuery) {

        WorkOrder workOrder = getById(productSerialNumberQuery.getProductId());
        if (Objects.isNull(workOrder)) {
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
            productSerialNumber.setCreateTime(System.currentTimeMillis());
            productSerialNumberMapper.insert(productSerialNumber);
        }
        return R.ok();
    }

    @Override
    public IPage getSerialNumberPage(Long offset, Long size, ProductSerialNumberQuery productSerialNumber) {
        return productSerialNumberMapper.getSerialNumberPage(PageUtil.getPage(offset, size), productSerialNumber);
    }

    //绑定产品列表
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R pointBindSerialNumber(WorkOrderQuery workOrderQuery) {
        WorkOrder workOrder = getById(workOrderQuery.getId());
        if (Objects.isNull(workOrder)) {
            return R.failMsg("未找到点位信息!");
        }
        if (ObjectUtil.isNotEmpty(workOrderQuery.getProductSerialNumberIdAndSetNoMap())) {
            BigDecimal totalAmount = Objects.isNull(workOrder.getFee()) ? BigDecimal.ZERO : workOrder.getFee();
            for (Map.Entry<Long, Integer> entry : workOrderQuery.getProductSerialNumberIdAndSetNoMap().entrySet()) {
                ProductSerialNumber productSerialNumber = productSerialNumberMapper.selectById(entry.getKey());
                if (Objects.isNull(productSerialNumber)) {
                    log.error("not found productSerialNumber by id:{}", entry.getKey());
                    throw new CustomBusinessException("未找到产品序列号!");
                }
                productSerialNumber.setPointId(workOrder.getId());
                productSerialNumber.setSetNo(entry.getValue());
                productSerialNumberMapper.updateById(productSerialNumber);
                totalAmount = totalAmount.add(productSerialNumber.getPrice());
            }
            WorkOrder workOrderUpdate = new WorkOrder();
            workOrderUpdate.setId(workOrderUpdate.getId());
            workOrder.setFee(totalAmount);
            baseMapper.updateById(workOrderUpdate);
        }
        return R.ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveWorkerOrder(WorkOrderQuery workOrder) {
        R r = checkProperties(workOrder);
        if(Objects.nonNull(r)){
            return r;
        }

        workOrder.setOrderNo(RandomUtil.randomString(10));
        if (workOrder.getThirdCompanyId() != null && workOrder.getThirdCompanyType() != null) {
            if (workOrder.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)){
                Customer customer = customerService.getById(workOrder.getThirdCompanyId());
                if(Objects.nonNull(customer)) {
                    workOrder.setThirdCompanyName(customer.getName());
                }
            }

            if (workOrder.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)){
                Supplier supplier = supplierService.getById(workOrder.getThirdCompanyId());
                if (Objects.nonNull(supplier)){
                    workOrder.setThirdCompanyName(supplier.getName());
                }
            }

            if (workOrder.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SERVER)){
                Server server = serverService.getById(workOrder.getThirdCompanyId());
                if (Objects.nonNull(server)){
                    workOrder.setThirdCompanyName(server.getName());
                }
            }
        }

        if (workOrder.getServerId()!=null){
            Server server = serverService.getById(workOrder.getServerId());
            if(Objects.nonNull(server)){
                workOrder.setServerName(server.getName());
            }
        }

        if(!Objects.equals(workOrder.getType(), String.valueOf(WorkOrder.TYPE_AFTER))){
            if(Objects.nonNull(workOrder.getProductInfoList())) {
                Iterator<ProductInfoQuery> iterator = workOrder.getProductInfoList().iterator();
                while (iterator.hasNext()){
                    ProductInfoQuery productInfoQuery = iterator.next();
                    if(Objects.isNull(productInfoQuery.getProductId()) || Objects.isNull(productInfoQuery.getNumber())){
                        iterator.remove();
                    }
                }
                String productInfo = JSON.toJSONString(workOrder.getProductInfoList());
                workOrder.setProductInfo(productInfo);
            }
        }



        int insert = this.baseMapper.insert(workOrder);
        if (insert == 0) {
            return R.fail("数据库保存出错");
        }

        if (!workOrder.getFileList().isEmpty()){
            workOrder.getFileList().forEach(item -> {
                File file = new File();
                file.setType(File.TYPE_WORK_ORDER);
                file.setFileName(item);
                file.setCreateTime(System.currentTimeMillis());
                file.setBindId(workOrder.getId());
                file.setFileType(Integer.parseInt(workOrder.getType()));
                fileService.save(file);
            });
        }
        return R.ok();
    }


    @Override
    public R updateWorkOrderStatus(WorkerOrderUpdateStatusQuery query) {
        WorkOrder workOrder = baseMapper.selectById(query.getId());

        if (Objects.isNull(workOrder)) {
            return R.fail("id不存在");
        }
        workOrder.setStatus(query.getStatus());

        workOrder.setWorkOrderReasonId(query.getWorkOrderReasonId());
        baseMapper.updateById(workOrder);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateWorkOrder(WorkOrderQuery workOrder) {
        WorkOrder oldWorkOrder = this.getById(workOrder.getId());
        if(Objects.isNull(oldWorkOrder)){
            return R.fail("未查询到工单");
        }

        workOrder.setCreaterId(oldWorkOrder.getCreaterId());
        workOrder.setCreateTime(oldWorkOrder.getCreateTime());

        R r = checkProperties(workOrder);
        if(Objects.nonNull(r)){
            return r;
        }

        if (workOrder.getThirdCompanyId() != null && workOrder.getThirdCompanyType() != null) {
            if (workOrder.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)){
                Customer customer = customerService.getById(workOrder.getThirdCompanyId());
                if(Objects.nonNull(customer)) {
                    workOrder.setThirdCompanyName(customer.getName());
                }
            }

            if (workOrder.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)){
                Supplier supplier = supplierService.getById(workOrder.getThirdCompanyId());
                if (Objects.nonNull(supplier)){
                    workOrder.setThirdCompanyName(supplier.getName());
                }
            }

            if (workOrder.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SERVER)){
                Server server = serverService.getById(workOrder.getThirdCompanyId());
                if (Objects.nonNull(server)){
                    workOrder.setThirdCompanyName(server.getName());
                }
            }
        }

        if (workOrder.getServerId()!=null){
            Server server = serverService.getById(workOrder.getServerId());
            if(Objects.nonNull(server)){
                workOrder.setServerName(server.getName());
            }
        }

        if(!Objects.equals(workOrder.getType(), String.valueOf(WorkOrder.TYPE_AFTER))){
            if(Objects.nonNull(workOrder.getProductInfoList())) {
                Iterator<ProductInfoQuery> iterator = workOrder.getProductInfoList().iterator();
                while (iterator.hasNext()){
                    ProductInfoQuery productInfoQuery = iterator.next();
                    if(Objects.isNull(productInfoQuery.getProductId()) || Objects.isNull(productInfoQuery.getNumber())){
                        iterator.remove();
                    }
                }
                String productInfo = JSON.toJSONString(workOrder.getProductInfoList());
                workOrder.setProductInfo(productInfo);
            }
        }


        workOrder.setAuditStatus(WorkOrder.AUDIT_STATUS_WAIT);
        this.baseMapper.updateOne(workOrder);
        //this.baseMapper.updateById(workOrder);
        return R.ok();
    }

    @Override
    public List<AfterCountVo> qualityCount(Long pointId, Integer cityId,Long startTime,Long endTime) {

      return this.baseMapper.qualityCount(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterCountListVo> qualityCountList(Long pointId, Integer cityId,Long startTime,Long endTime) {

        return this.baseMapper.qualityCountList(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterOrderVo> afterWorkOrderByCity(Long pointId, Integer cityId, Long startTime,Long endTime) {
        return this.baseMapper.afterWorkOrderByCity(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterOrderVo> afterWorkOrderByPoint(Long pointId, Integer cityId,Long startTime,Long endTime) {
        return this.baseMapper.afterWorkOrderByPoint(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterOrderVo> afterWorkOrderList(Long pointId, Integer cityId, Long startTime,Long endTime) {
        return this.baseMapper.afterWorkOrderList(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterOrderVo> installWorkOrderByCity(Long pointId, Integer cityId,Long startTime,Long endTime) {
        return this.baseMapper.installWorkOrderByCity(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterOrderVo> installWorkOrderByPoint(Long pointId, Integer cityId,Long startTime,Long endTime) {
        return this.baseMapper.installWorkOrderByPoint(pointId,cityId,startTime,endTime);
    }

    @Override
    public List<AfterOrderVo> installWorkOrderList(Long pointId, Integer cityId,Long startTime,Long endTime) {
        return this.baseMapper.installWorkOrderList(pointId,cityId,startTime,endTime);
    }

    @Override
    public R updateWorkorderProcessTime(Long id) {
        WorkOrder workOrder = baseMapper.selectById(id);

        if (Objects.isNull(workOrder)) {
            return R.fail("id不存在");
        }
        workOrder.setProcessTime(System.currentTimeMillis());
        baseMapper.updateById(workOrder);
        return R.ok();
    }

    @Override
    public R updateAuditStatus(WorkOrderAuditStatusQuery workOrderAuditStatusQuery) {
        if(Objects.isNull(workOrderAuditStatusQuery.getId()) || Objects.isNull(workOrderAuditStatusQuery.getAuditStatus())){
            return R.fail("参数不合法");
        }

        WorkOrder workOrder = this.getById(workOrderAuditStatusQuery.getId());
        if(Objects.isNull(workOrder)){
            return R.fail("未查询到相关工单");
        }

        WorkOrder workOrderUpdate = new WorkOrder();
        workOrderUpdate.setId(workOrderAuditStatusQuery.getId());
        workOrderUpdate.setAuditStatus(workOrderAuditStatusQuery.getAuditStatus());
        workOrderUpdate.setAuditRemarks(workOrderAuditStatusQuery.getAuditRemarks());
        this.updateById(workOrderUpdate);
        return R.ok();
    }

    @Override
    public R putAdminPointNewCreateUser(Long id, Long createUid) {
        if(Objects.isNull(id) || Objects.isNull(createUid)){
            return R.fail("参数非法，请检查");
        }

        User user = userService.getUserById(createUid);
        if(Objects.isNull(user)){
            return R.fail("没有查询到该用户");
        }

        Integer len = baseMapper.putAdminPointNewCreateUser(id, createUid);

        if(len != null && len > 0){
            return R.ok();
        }

        return R.fail("修改失败");
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

    private R checkProperties(WorkOrderQuery workOrderQuery){
        if(StringUtils.isBlank(workOrderQuery.getType())){
            return R.fail("请填写工单类型");
        }else{
            WorkOrderType workOrderType = workOrderTypeService.getById(workOrderQuery.getType());
            if (Objects.isNull(workOrderType)){
                return R.fail("请填写正确的工单类型");
            }
        }

        if(Objects.isNull(workOrderQuery.getPointId())){
            return R.fail("请填写点位");
        }else{
            PointNew point = pointNewService.getById(workOrderQuery.getPointId());
            if (Objects.isNull(point)) {
                return R.fail("未查询到相关点位");
            }

        }

        if(Objects.isNull(workOrderQuery.getCreaterId())){
            return R.fail("创建人为空");
        }else{
            User user = userService.getUserById(workOrderQuery.getCreaterId());
            if (Objects.isNull(user)) {
                return R.fail("未查询到相关创建人");
            }
        }

        if(Objects.isNull(workOrderQuery.getStatus())){
            return R.fail("请填写工单状态");
        }else{
            String status = this.getStatusStr(workOrderQuery.getStatus());
            if(Objects.equals("", status)){
                return R.fail("请填写正确的工单状态");
            }
        }

        if(Objects.isNull(workOrderQuery.getCreateTime())){
            return R.fail("请填写创建时间");
        }

        if(Objects.isNull(workOrderQuery.getProcessTime())){
            return R.fail("请填写处理时间");
        }

        if(Objects.equals(workOrderQuery.getType(), String.valueOf(WorkOrder.TYPE_MOBLIE))){
            if(Objects.isNull(workOrderQuery.getSourceType())){
                return R.fail("请填写起点类型");
            }
            if(Objects.isNull(workOrderQuery.getTransferSourcePointId())){
                return R.fail("请填写起点");
            }else{
                if(Objects.equals(workOrderQuery.getSourceType(), 1)){
                    PointNew pointNew = pointNewService.queryByIdFromDB(workOrderQuery.getTransferSourcePointId());
                    if(Objects.isNull(pointNew)){
                        return R.fail("未查询到相关起点");
                    }
                }

                if(Objects.equals(workOrderQuery.getSourceType(), 2)){
                    WareHouse wareHouse = warehouseService.getById(workOrderQuery.getTransferSourcePointId());
                    if(Objects.isNull(wareHouse)){
                        return R.fail("未查询到相关起点");
                    }
                }
            }
            if(Objects.isNull(workOrderQuery.getDestinationType())){
                return R.fail("请填写终点类型");
            }
            if(Objects.isNull(workOrderQuery.getTransferDestinationPointId())){
                return R.fail("请填写终点");
            }else{
                if(Objects.equals(workOrderQuery.getDestinationType(), 1)){
                    PointNew pointNew = pointNewService.queryByIdFromDB(workOrderQuery.getTransferDestinationPointId());
                    if(Objects.isNull(pointNew)){
                        return R.fail("未查询到相关终点");
                    }
                }

                if(Objects.equals(workOrderQuery.getDestinationType(), 2)){
                    WareHouse wareHouse = warehouseService.getById(workOrderQuery.getTransferDestinationPointId());
                    if(Objects.isNull(wareHouse)){
                        return R.fail("未查询到相关终点");
                    }
                }
            }
        }
        return null;
    }

    private String getParentWorkOrderReason(Long id){
        Integer i = 0;
        Long workOrderReasonId = id;
        while ( i < 10){
            WorkOrderReason workOrderReason = workOrderReasonService.getById(workOrderReasonId);
            if(Objects.nonNull(workOrderReason)){
                workOrderReasonId = workOrderReason.getParentId();
                if(Objects.equals(workOrderReason.getParentId(), -1L)){
                    return workOrderReason.getName();
                }
            }else{
                return null;
            }
            ++i;
        }
        return null;
    }
}
