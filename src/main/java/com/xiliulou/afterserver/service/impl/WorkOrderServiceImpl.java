package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.mapper.WorkOrderMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.query.SaveWorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkerOrderUpdateStatusQuery;
import com.xiliulou.afterserver.web.vo.*;
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



    @Override
    public IPage getPage(Long offset, Long size, WorkOrderQuery workOrder) {
        if (Objects.nonNull(workOrder.getWorkOrderType())) {
            workOrder.setType(workOrder.getWorkOrderType().toString());
        }
        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, workOrder);

        List<WorkOrderVo> list = (List<WorkOrderVo>)page.getRecords();
        if (list.isEmpty()){
            return page;
        }

        list.forEach(item -> {
            if (Objects.nonNull(item.getCreaterId())){
                User userById = userService.getUserById(item.getCreaterId());
                if (Objects.nonNull(userById)){
                    item.setUserName(userById.getUserName());
                }
            }
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
//            workOrderExcelVo.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
//            if (ObjectUtil.isNotEmpty(o.getProcessTime())) {
//                workOrderExcelVo.setProcessorTimeStr(simpleDateFormat.format(new Date(o.getProcessTime())));
//            }
            workOrderExcelVoList.add(workOrderExcelVo);
        }

        String fileName = "工单列表.xlsx";
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
        Integer count = baseMapper.countOrderList(workOrder);
        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        workOrderVoList.forEach(o -> {

            WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
            if (Objects.nonNull(workOrderType)){
                o.setWorkOrderType(workOrderType.getType());
            }

            Point point = pointService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                o.setPointName(point.getName());
            }
            WorkOrderReason workOrderReason = workOrderReasonService.getById(o.getWorkOrderReasonId());

            if (Objects.nonNull(workOrderReason)) {
                o.setWorkOrderReasonName(workOrderReason.getName());
            }
            o.setThirdCompanyPay(o.getThirdCompanyPay());

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

            if (o.getServerId()!=null){
                Server server = serverService.getById(o.getServerId());
                if (Objects.nonNull(server)){
                    o.setServerName(server.getName());
                }
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",count);
        map.put("data",workOrderVoList);
        return R.ok(map);
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
            throw new CustomBusinessException("请选择开始时间结束时间");
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

            Point point = pointService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo.setPointName(point.getName());
            }
            workOrderExcelVo.setRemarks(o.getInfo());
            workOrderExcelVo.setWorkOrderReasonName(o.getThirdReason());
            workOrderExcelVo.setThirdCompanyPay(o.getThirdCompanyPay());
//            workOrderExcelVo.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));

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

            Point point = pointService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo2.setPointName(point.getName());
            }
            workOrderExcelVo2.setRemarks(o.getInfo());
            workOrderExcelVo2.setWorkOrderReasonName(o.getThirdReason());
            workOrderExcelVo2.setThirdCompanyPay(o.getThirdCompanyPay());
//            workOrderExcelVo2.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo2.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
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

            Point point = pointService.getById(o.getPointId());
            if (Objects.nonNull(point)){
                workOrderExcelVo3.setPointName(point.getName());
            }
            workOrderExcelVo3.setRemarks(o.getInfo());
            workOrderExcelVo3.setThirdCompanyPay(o.getThirdCompanyPay());
//            workOrderExcelVo3.setStatusStr(getStatusStr(o.getStatus()));
            workOrderExcelVo3.setCreateTimeStr(simpleDateFormat.format(new Date(o.getCreateTime())));
            if (o.getServerId()!=null){
                Server server = serverService.getById(o.getServerId());
                if (Objects.nonNull(server)){
                    workOrderExcelVo3.setThirdCompanyName(server.getName());
                }
                workOrderExcelVo3.setThirdCompanyType("服务商");
                if (o.getThirdCompanyPay()!=null) {
                    serverPayAmount.addAndGet((int) o.getFee().doubleValue());
                }
                workOrderExcelVo3.setThirdCompanyPay(o.getFee());
                serverExcelVoList.add(workOrderExcelVo3);
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
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            ServletOutputStream outputStream = response.getOutputStream();
            excelWriter = EasyExcel.write(outputStream).build();
            /**
             * 总的导表
             */
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
    }




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
        PointNew point = pointNewService.getById(workOrder.getPointId());
        if (Objects.isNull(point)) {
            log.error("WorkOrder Error pointId:{}", workOrder.getPointId());
            return R.fail("未查询到相关点位");
        }
        //如果工单类型包含  派送   则系统生成一个派送工单，自动新增一个发货订单
        if (workOrder.getType().equals(WorkOrder.TYPE_SEND.toString())
                || workOrder.getType().equals(WorkOrder.TYPE_SEND_INSERT.toString())) {
            Deliver deliver = new Deliver();
            deliver.setCreateTime(System.currentTimeMillis());
            deliver.setDeliverCost(workOrder.getFee());
            deliver.setRemark(workOrder.getInfo());
            Server server = serverService.getById(workOrder.getServerId());
            if (Objects.nonNull(server)) {
                deliver.setExpressCompany(server.getName());
            }

            deliver.setDeliverTime(System.currentTimeMillis());
            deliver.setQuantity(workOrder.getCount());
            deliver.setCity(workOrder.getStartAddr());
            deliver.setDestination(point.getName());

            deliverService.save(deliver);
        }

        workOrder.setOrderNo(UUID.randomUUID().toString());
//        if (workOrder.getType().contains(WorkOrder.TYPE_INSTALL.toString())) {
//            point.setStatus(Point.STATUS_TRANSFER);
//            if (!pointService.updateById(point)) {
//                log.error("WorkOrder Error  update point status error data:{}", point.toString());
//                return R.fail("数据库错误");
//            }
//        }
//        saveFile(workOrder);


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
        }

        if (workOrder.getServerId()!=null){
            Server server = serverService.getById(workOrder.getServerId());
            if(Objects.nonNull(server)){
                workOrder.setServerName(server.getName());
            }
        }

        int insert = this.baseMapper.insert(workOrder);
        if (insert > 0) {
            return R.ok();
        }
        return R.fail("数据库保存出错");
    }

    private void saveFile(WorkOrderQuery workOrder) {
        LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                .eq(File::getBindId, workOrder.getPointId())
                .eq(File::getType, File.TYPE_WORK_ORDER);

        List<File> files = fileService.getBaseMapper().selectList(eq);
        if (Objects.nonNull(files)) {
            files.forEach(item -> {
                fileService.removeById(item.getId());
            });
        }
        //照片类型为
        if (workOrder.getFileNameList() != null) {
            workOrder.getFileNameList().forEach(item -> {
                File file = new File();
                file.setFileName(item);
                file.setType(File.TYPE_WORK_ORDER);
                file.setBindId(workOrder.getPointId());

                if (workOrder.getType().equals(WorkOrder.TYPE_AFTER.toString())) {
                    file.setFileType(File.FILE_TYPE_AFTER);
                }
                if (workOrder.getType().equals(WorkOrder.TYPE_INSTALL.toString())
                        || workOrder.getType().equals(WorkOrder.TYPE_SEND_INSERT.toString())) {
                    file.setFileType(File.FILE_TYPE_INSTALL);
                }
                file.setCreateTime(System.currentTimeMillis());
                fileService.save(file);
            });
        }
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
        saveFile(workOrder);
        this.baseMapper.updateById(workOrder);
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
