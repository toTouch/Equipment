package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
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
import com.xiliulou.afterserver.constant.MqConstant;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.entity.mq.notify.MqNotifyCommon;
import com.xiliulou.afterserver.entity.mq.notify.MqWorkOrderAuditNotify;
import com.xiliulou.afterserver.entity.mq.notify.MqWorkOrderServerNotify;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.mapper.WorkOrderMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.*;
import com.xiliulou.afterserver.web.vo.*;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.mq.service.RocketMqService;
import io.micrometer.core.instrument.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.common.filter.impl.Op;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements
    WorkOrderService {

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
    @Autowired
    WorkOrderServerService workOrderServerService;
    @Autowired
    WorkAuditNotifyService workAuditNotifyService;
    @Autowired
    RocketMqService rocketMqService;
    @Autowired
    MaintenanceUserNotifyConfigService maintenanceUserNotifyConfigService;
    @Autowired
    ServerAuditEntryService serverAuditEntryService;
    @Autowired
    CityService cityService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private WorkOrderPartsService workOrderPartsService;
    @Autowired
    private PartsService partsService;



    @Override
    public IPage getPage(Long offset, Long size, WorkOrderQuery workOrder) {
        if (Objects.nonNull(workOrder.getWorkOrderType())) {
            workOrder.setType(workOrder.getWorkOrderType().toString());
        }
        //检查质保过期点位
        pointNewService.updatePastWarrantyStatus();
        //获取服务商id
        if(StrUtil.isNotBlank(workOrder.getServerName())) {
            List<Integer> serverIds = serverService.getByIdsByName(workOrder.getServerName());
            if(CollectionUtils.isEmpty(serverIds)) {
                Page result = new Page();
                result.setRecords(new ArrayList());
                result.setTotal(0);
                return result;
            }

            List<Integer> workOrderIds = workOrderServerService.getIdsByserverIds(serverIds);
            if(CollectionUtils.isEmpty(workOrderIds)) {
                Page result = new Page();
                result.setRecords(new ArrayList());
                result.setTotal(0);
                return result;
            }
            workOrder.setWorkOrderIds(workOrderIds);
        }


        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, workOrder);

        List<WorkOrderVo> list = (List<WorkOrderVo>) page.getRecords();
        if (list.isEmpty()) {
            return page;
        }

        list.forEach(item -> {

            if (Objects.nonNull(item.getCreaterId())) {
                User userById = userService.getUserById(item.getCreaterId());
                if (Objects.nonNull(userById)) {
                    item.setUserName(userById.getUserName());
                }
            }

            if (Objects.nonNull(item.getCommissionerId())) {
                User userById = userService.getUserById(item.getCommissionerId());
                if (Objects.nonNull(userById)) {
                    item.setCommissionerName(userById.getUserName());
                }
            }

            if (Objects.nonNull(item.getPointId())) {
                PointNew pointNew = pointNewService.queryByIdFromDB(item.getPointId());
                if (Objects.nonNull(pointNew)) {
                    item.setPointName(pointNew.getName());
                }
            }

            if (Objects.equals(item.getDestinationType(), "2")) {
                WareHouse wareHouse = warehouseService
                    .getById(item.getTransferDestinationPointId());
                if (Objects.nonNull(wareHouse)) {
                    item.setTransferDestinationPointName(wareHouse.getWareHouses());
                }
            }

            if (Objects.equals(item.getSourceType(), "2")) {
                WareHouse wareHouse = warehouseService.getById(item.getTransferSourcePointId());
                if (Objects.nonNull(wareHouse)) {
                    item.setTransferSourcePointName(wareHouse.getWareHouses());
                }
            }

            /*if(Objects.nonNull(item.getProcessTime())){
                String prescription = DateUtils.getDatePoor(item.getProcessTime() , item.getCreateTime());
                item.setPrescription(prescription);
                item.setPrescriptionMillis(item.getProcessTime() - item.getCreateTime());
            }*/

            if (Objects.nonNull(item.getProductInfo())) {
                List<ProductInfoQuery> productInfo = JSON
                    .parseArray(item.getProductInfo(), ProductInfoQuery.class);
                item.setProductInfoList(productInfo);
            }

            if (Objects.nonNull(item.getWorkOrderReasonId())) {
                item.setWorkOrderReasonName(
                    this.getWorkOrderReasonStr(item.getWorkOrderReasonId(), ""));
            }

            User userById = userService.getUserById(item.getAuditUid());
            if (Objects.nonNull(userById)) {
                item.setAuditUserName(userById.getUserName());
            }

            //处理图片
            List<WorkOrderServerQuery> workOrderServers = workOrderServerService
                .queryByWorkOrderIdAndServerId(item.getId(), null);
            item.setWorkOrderServerList(workOrderServers);

            //凭证
            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                .eq(File::getType, File.TYPE_WORK_ORDER)
                .eq(File::getFileType, 0)
                .eq(File::getBindId, item.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            item.setVoucherImgFile(fileList);

            //视频
            eq.clear();
            eq.eq(File::getType, File.TYPE_WORK_ORDER)
                .eq(File::getFileType, 90000)
                .eq(File::getBindId, item.getId());
            File files = fileService.getBaseMapper().selectOne(eq);
            item.setVoucherVideoFile(files);

            item.setParentWorkOrderReason(
                this.getParentWorkOrderReason(item.getWorkOrderReasonId()));

            //统计工单图片
            eq.clear();
            eq.eq(File::getType, File.TYPE_WORK_ORDER)
                .eq(File::getBindId, item.getId());
            Integer count = fileService.getBaseMapper().selectCount(eq);
            item.setFileCount(count);

            //处理工单配件
            if (CollectionUtils.isEmpty(workOrderServers)) {
                return;
            }

            //工单配件
            workOrderServers.stream().forEach(t -> {
                if (Objects.equals(t.getHasParts(), WorkOrderServer.NOT_HAS_PARTS)) {
                    return;
                }
                t.setWorkOrderParts(workOrderPartsService
                    .queryByWorkOrderIdAndServerId(t.getWorkOrderId(), t.getServerId(), WorkOrderParts.TYPE_SERVER_PARTS));
                t.setThirdWorkOrderParts(workOrderPartsService
                    .queryByWorkOrderIdAndServerId(t.getWorkOrderId(), t.getServerId(), WorkOrderParts.TYPE_THIRD_PARTS));
            });
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
            log.error("SAVE_WORK_ORDER ERROR ,NOT FOUND USER BY ID ,ID:{}",
                saveWorkOrderQuery.getUid());
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
        if ("null".equals(workOrder.getType())) {
            workOrder.setType(null);
        }
        //扫描质保过期点位
        pointNewService.updatePastWarrantyStatus();
        List<WorkOrderVo> workOrderVoList = null;

        //获取服务商id
        if(StrUtil.isNotBlank(workOrder.getServerName())) {
            List<Integer> serverIds = serverService.getByIdsByName(workOrder.getServerName());
            if(CollectionUtils.isEmpty(serverIds)) {
                workOrderVoList = new ArrayList<>();
            }

            List<Integer> workOrderIds = workOrderServerService.getIdsByserverIds(serverIds);
            if(CollectionUtils.isEmpty(workOrderIds)) {
                workOrderVoList = new ArrayList<>();
            }
            workOrder.setWorkOrderIds(workOrderIds);
        } else {
             workOrderVoList = baseMapper.orderList(workOrder);
        }



        if (ObjectUtil.isEmpty(workOrderVoList)) {
            throw new CustomBusinessException("没有查询到工单!无法导出！");
        }

        if (StringUtils.isBlank(workOrder.getType()) || "4".equals(workOrder.getType())) {
            exportExcelMoveMachine(workOrder, workOrderVoList, response);
        } else {
            exportExcelNotMoveMachine(workOrder, workOrderVoList, response);
        }
    }

    private void exportExcelMoveMachine(WorkOrderQuery workOrder, List<WorkOrderVo> workOrderVoList,
        HttpServletResponse response) {
        //headerSet
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"审核状态", "工单类型", "点位", "点位状态","点位客户", "柜机系列", "移机起点", "移机终点", "创建人",
            "状态", "描述", "备注", "工单原因", "创建时间", "工单编号", "sn码", "审核内容", "专员", "派单时间"};

        String[] serverHeader = {"服务商", "工单费用", "结算方式", "解决方案", "解决时间", "处理时长", "文件个数","是否更换配件", "是否需要第三方承担费用", " 第三方类型",
            "第三方公司", "应收第三方人工费", "应收第三方物料费", "支付状态", "第三方原因", "第三方对接人"};

        List<Product> productAll = productService.list();

        for (String s : header) {
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(s);
            headList.add(headTitle);
        }

        table.setHead(headList);

        List<List<Object>> data = new ArrayList<>();
        Integer codeMaxSize = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Long partsMaxLen = 0L;
        Long thirdPartsMaxLen = 0L;
        List<Long> workOrderIds = new ArrayList<>();

        for(WorkOrderVo item : workOrderVoList) {
            workOrderIds.add(item.getId());
            Long tempPartsMaxLen = Optional.ofNullable(workOrderPartsService.queryPartsMaxCountByWorkOrderId(item.getId(), WorkOrderParts.TYPE_SERVER_PARTS)).orElse(0L);
            Long tempThirdPartsMaxLen = Optional.ofNullable(workOrderPartsService.queryPartsMaxCountByWorkOrderId(item.getId(), WorkOrderParts.TYPE_THIRD_PARTS)).orElse(0L);

            partsMaxLen = partsMaxLen > tempPartsMaxLen ? partsMaxLen : tempPartsMaxLen;
            thirdPartsMaxLen = thirdPartsMaxLen > tempThirdPartsMaxLen ? thirdPartsMaxLen : tempThirdPartsMaxLen;
        }
        Integer serverMaxLen = workOrderServerService.queryMaxCountByWorkOrderId(workOrderIds);

        for (WorkOrderVo o : workOrderVoList) {
            List<Object> row = new ArrayList<>();
            //审核状态
            row.add(this.getAuditStatus(o.getAuditStatus()));

            //typeName
            if (Objects.nonNull(o.getType())) {
                WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
                if (Objects.nonNull(workOrderType)) {
                    row.add(workOrderType.getType());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }
            //pointName
            if (Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(StrUtil.isBlank(pointNew.getName())? "" : pointNew.getName());
                    //row.add(pointNew.getSnNo());
                } else {
                    row.add("");
                    //row.add("");
                }
            } else {
                row.add("");
                //row.add("");
            }

            //点位状态
            row.add(getPointStatusName(o.getPointStatus()));

            //点位客户
            if(Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.queryByIdFromDB(o.getPointId());
                if(Objects.nonNull(pointNew)) {
                    Customer byId = customerService.getById(pointNew.getCustomerId());
                    if(Objects.nonNull(byId)){
                        row.add(StrUtil.isBlank(byId.getName())? "" : byId.getName());
                    } else {
                        row.add("");
                    }
                }else {
                    row.add("");
                }
            }else {
                row.add("");
            }

            //柜机系列
            row.add(getPointProductSeries(o.getProductSeries()));

            //起点
            if ("1".equals(o.getSourceType())) {
                PointNew pointNew = pointNewService.getById(o.getTransferSourcePointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(pointNew.getName());
                } else {
                    row.add("");
                }
            } else if ("2".equals(o.getSourceType())) {
                WareHouse warehouse = warehouseService.getById(o.getTransferSourcePointId());
                if (Objects.nonNull(warehouse)) {
                    row.add(warehouse.getWareHouses());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //终点
            if ("1".equals(o.getDestinationType())) {
                PointNew pointNew = pointNewService.getById(o.getTransferDestinationPointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(pointNew.getName());
                } else {
                    row.add("");
                }
            } else if ("2".equals(o.getDestinationType())) {
                WareHouse warehouse = warehouseService.getById(o.getTransferDestinationPointId());
                if (Objects.nonNull(warehouse)) {
                    row.add(warehouse.getWareHouses());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //"创建人",
            if (Objects.nonNull(o.getCreaterId())) {
                User user = userService.getUserById(o.getCreaterId());
                if (Objects.nonNull(user)) {
                    row.add(user.getUserName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //"状态",
            //status  1;待处理2:已处理3:待分析4：已完结
            if (Objects.nonNull(o.getStatus())) {
                row.add(this.getStatusStr(o.getStatus()));
            }

            //时效
           /* String prescription = DateUtils.getDatePoor(o.getProcessTime(), o.getCreateTime());
            row.add(StringUtils.isBlank(prescription) ? "" : prescription);*/

            //"描述", "
            row.add(o.getDescribeinfo() == null ? "" : o.getDescribeinfo());

            //"备注",
            row.add(o.getInfo() == null ? "" : o.getInfo());

            //"工单原因",
            //workOrderReasonName
            if (Objects.nonNull(o.getWorkOrderReasonId())) {
                String name = this.getWorkOrderReasonStr(o.getWorkOrderReasonId(), "");
                row.add(name);
            } else {
                row.add("");
            }

            //第三方原因
            /*row.add(o.getThirdReason() == null ? "" : o.getThirdReason());

            if (Objects.nonNull(o.getThirdCompanyType())) {
                this.setThirdCompanyNameAndServerName(o);
                row.add(o.getThirdCompanyName());
            } else {
                row.add("");
            }

            //third_company_pay
            row.add(o.getThirdCompanyPay() == null ? "" : o.getThirdCompanyPay());

            row.add(o.getFee() == null ? "" : o.getFee());

            //图片数量
            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(Objects.nonNull(workOrder.getWorkOrderType()), File::getFileType, workOrder.getWorkOrderType())
                    .eq(File::getBindId, o.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            row.add(CollectionUtils.isEmpty(fileList) ? 0 : fileList.size());

            //"处理时间",
            //processTime
            if (Objects.nonNull(o.getProcessTime())) {
                row.add(simpleDateFormat.format(new Date(o.getProcessTime())));
            } else {
                row.add("");
            }*/

            //创建时间"
            if (Objects.nonNull(o.getCreateTime())) {
                row.add(simpleDateFormat.format(new Date(o.getCreateTime())));
            } else {
                row.add("");
            }
            //服务商
            //row.add(o.getServerName() == null ? "" : o.getServerName());

            //PaymentMethod
            //row.add(getPaymentMethod(o.getPaymentMethod()));

            //"第三方责任对接人"
            //row.add(o.getThirdResponsiblePerson() == null ? "" : o.getThirdResponsiblePerson());

            //"工单编号",
            row.add(o.getOrderNo() == null ? "" : o.getOrderNo());

            /*//thirdPaymentStatus
            if (Objects.nonNull(o.getThirdPaymentStatus())) {
                if (o.getThirdPaymentStatus().equals(1)) {
                    row.add("无需结算");
                } else if (o.getThirdPaymentStatus().equals(2)) {
                    row.add("未结算");
                } else if (o.getThirdPaymentStatus().equals(3)) {
                    row.add("已结算");
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }*/

            //sn
            if (Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(pointNew.getSnNo());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //审核备注
            row.add(o.getAuditRemarks() == null ? "" : o.getAuditRemarks());

            //专员
            if (Objects.nonNull(o.getCommissionerId())) {
                User user = userService.getUserById(o.getCommissionerId());
                if (Objects.nonNull(user)) {
                    row.add(user.getUserName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //派单时间
            if (Objects.nonNull(o.getAssignmentTime())) {
                row.add(simpleDateFormat.format(new Date(o.getAssignmentTime())));
            } else {
                row.add("");
            }

            //服务商信息
            List<WorkOrderServerQuery> workOrderServerQueryList = workOrderServerService
                .queryByWorkOrderIdAndServerId(o.getId(), null);

            if (!CollectionUtils.isEmpty(workOrderServerQueryList)) {
                for (WorkOrderServerQuery item : workOrderServerQueryList) {

                    //服务商",
                    Server server = serverService.getById(item.getServerId());
                    if (Objects.nonNull(server)) {
                        row.add(server.getName());
                    } else {
                        row.add("");
                    }

                    // "费用",
                    row.add(item.getFee() == null ? "" : item.getFee());
                    // "结算方式",
                    row.add(this.getPaymentMethod(item.getPaymentMethod()));
                    // "解决方案",
                    row.add(item.getSolution() == null ? "" : item.getSolution());
                    // "解决时间",

                    if (Objects.nonNull(item.getSolutionTime())) {
                        row.add(simpleDateFormat.format(new Date(item.getSolutionTime())));
                    } else {
                        row.add("");
                    }
                    // "处理时长
                    row.add(getPrescriptionStr(item.getPrescription()));
                    //图片个数
                    QueryWrapper<File> wrapper = new QueryWrapper<>();
                    wrapper.eq("type", File.TYPE_WORK_ORDER);
                    wrapper.eq("bind_id", o.getId());
                    wrapper.ge("file_type", 1).lt("file_type", 90000);
                    wrapper.eq("server_id", item.getServerId());

                    int fileCount = fileService.count(wrapper);

                    row.add(fileCount);

                    row.add(Objects.equals(WorkOrderServer.HAS_PARTS, item.getHasParts())?"是":"否");

                    if(Objects.equals(WorkOrderServer.HAS_PARTS, item.getHasParts())){
                        List<WorkOrderParts> workOrderParts = workOrderPartsService.queryByWorkOrderIdAndServerId(o.getId(), item.getServerId(), WorkOrderParts.TYPE_SERVER_PARTS);
                        if(!CollectionUtils.isEmpty(workOrderParts)) {
                            workOrderParts.forEach(e -> {
                                row.add(e.getSn());
                                row.add(e.getName());
                                row.add(e.getSum());
                                //row.add(e.getAmount());
                                Parts byId = partsService.getById(e.getPartsId());
                                if(Objects.nonNull(byId)) {
                                    row.add(StrUtil.isBlank(byId.getSpecification()) ? "" : byId.getSpecification());
                                }else {
                                    row.add("");
                                }
                            });

                            Long maxLine = partsMaxLen - workOrderParts.size();
                            for (int i = 0; i < maxLine; i++) {
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                            }
                        } else {
                            for (int i = 0; i < partsMaxLen; i++) {
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                            }
                        }
                    } else {
                        for (int i = 0; i < partsMaxLen; i++) {
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                        }
                    }

                    row.add(item.getIsUseThird() ? "是" : "否");

                    if(item.getIsUseThird()){
                        //" 第三方类型",
                        row.add(this.getThirdCompanyType(item.getThirdCompanyType()));
                        // "第三方公司",
                        row.add(item.getThirdCompanyName() == null ? "" : item.getThirdCompanyName());
                        // "人工费用",
                        row.add(item.getArtificialFee() == null ? "" : item.getArtificialFee());
                        //物料费用
                        row.add(item.getMaterialFee() == null ? "" : item.getMaterialFee());
                        // "支付状态",
                        row.add(this.getThirdPaymentStatus(item.getThirdPaymentStatus()));
                        // "第三方原因",
                        row.add(item.getThirdReason() == null ? "" : item.getThirdReason());
                        // "第三方对接人"
                        row.add(item.getThirdResponsiblePerson() == null ? ""
                            : item.getThirdResponsiblePerson());

                        List<WorkOrderParts> thirdWorkOrderParts = workOrderPartsService.queryByWorkOrderIdAndServerId(o.getId(), item.getServerId(), WorkOrderParts.TYPE_THIRD_PARTS);
                        if(!CollectionUtils.isEmpty(thirdWorkOrderParts)) {
                            thirdWorkOrderParts.forEach(e -> {
                                row.add(e.getSn());
                                row.add(e.getName());
                                row.add(e.getSum());
                                row.add(e.getAmount());
                                Parts byId = partsService.getById(e.getPartsId());
                                if(Objects.nonNull(byId)) {
                                    row.add(StrUtil.isBlank(byId.getSpecification()) ? "" : byId.getSpecification());
                                }else {
                                    row.add("");
                                }
                            });

                            Long maxLine = thirdPartsMaxLen - thirdWorkOrderParts.size();
                            for (int i = 0; i < maxLine; i++) {
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                            }
                        } else {
                            for (int i = 0; i < thirdPartsMaxLen; i++) {
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                            }
                        }
                    } else {
                        for (int i = 0; i < 7; i++) {
                            row.add("");
                        }
                        for (int i = 0; i < thirdPartsMaxLen; i++) {
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                        }
                    }

                }

                //给服务商不够最大服务商个数的的补充空白
                int supplementCell = serverMaxLen - workOrderServerQueryList.size();
                for (int i = 0; i < supplementCell; i++) {
                    for (String item : serverHeader) {
                        row.add("");
                    }
                }
            }

            //产品个数
            if (productAll != null && !productAll.isEmpty()) {
                List<ProductInfoQuery> productInfoQueries = null;
                if (Objects.nonNull(o.getProductInfo())) {
                    productInfoQueries = JSON
                        .parseArray(o.getProductInfo(), ProductInfoQuery.class);
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
                } else {
                    for (Product p : productAll) {
                        row.add("");
                    }
                }
            }

            List<String> codes = JSON.parseArray(o.getCode(), String.class);
            if (!CollectionUtils.isEmpty(codes)) {
                codeMaxSize = codeMaxSize > codes.size() ? codeMaxSize : codes.size();
                for (String code : codes) {
                    row.add(code);
                }
            }

            data.add(row);
        }

        for (int i = 0; i < serverMaxLen; i++) {
            for (String item : serverHeader) {
                List<String> headTitle = new ArrayList<>();
                headTitle.add(item + (i + 1));
                headList.add(headTitle);
                if(Objects.equals("是否更换配件", item)) {
                    for(int p = 1; p <= partsMaxLen; p++) {
                        List<String> partsTitle1 = new ArrayList<>();
                        partsTitle1.add("配件编号" + p);
                        List<String> partsTitle2 = new ArrayList<>();
                        partsTitle2.add("配件名称" + p);
                        List<String> partsTitle3 = new ArrayList<>();
                        partsTitle3.add("配件数量" + p);
                        List<String> partsTitle4 = new ArrayList<>();
                        partsTitle4.add("配件规格" + p);

                        headList.add(partsTitle1);
                        headList.add(partsTitle2);
                        headList.add(partsTitle3);
                        headList.add(partsTitle4);
                    }
                }

                if(Objects.equals("第三方对接人", item)) {
                    for(int p = 1; p <= thirdPartsMaxLen ; p++) {
                        List<String> partsTitle1 = new ArrayList<>();
                        partsTitle1.add("配件编号" + p);
                        List<String> partsTitle2 = new ArrayList<>();
                        partsTitle2.add("配件名称" + p);
                        List<String> partsTitle3 = new ArrayList<>();
                        partsTitle3.add("配件数量" + p);
                        List<String> partsTitle4 = new ArrayList<>();
                        partsTitle4.add("配件总售价" + p);
                        List<String> partsTitle5 = new ArrayList<>();
                        partsTitle5.add("配件规格" + p);

                        headList.add(partsTitle1);
                        headList.add(partsTitle2);
                        headList.add(partsTitle3);
                        headList.add(partsTitle4);
                        headList.add(partsTitle5);
                    }
                }
            }
        }

        if (productAll != null && !productAll.isEmpty()) {
            for (Product p : productAll) {
                List<String> headTitle = new ArrayList<>();
                headTitle.add(p.getName());
                headList.add(headTitle);
            }
        }

        for (int i = 1; i < codeMaxSize + 1; i++) {
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
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(data, sheet, table).finish();
            //EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }


    private void exportExcelNotMoveMachine(WorkOrderQuery workOrder,
        List<WorkOrderVo> workOrderVoList, HttpServletResponse response) {

        //headerSet
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"审核状态", "工单类型", "点位", "点位状态", "点位客户", "柜机系列", "创建人",
            "状态", "描述", "备注", "工单原因", "创建时间", "工单编号", "sn码", "审核内容", "专员", "派单时间"};

        String[] serverHeader = {"服务商", "工单费用", "结算方式", "解决方案", "解决时间", "处理时长", "文件个数","是否更换配件", "是否需要第三方承担费用", " 第三方类型",
            "第三方公司", "应收第三方人工费", "应收第三方物料费", "支付状态", "第三方原因", "第三方对接人"};

        List<Product> productAll = productService.list();

        for (String s : header) {
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(s);
            headList.add(headTitle);
        }

        table.setHead(headList);

        List<List<Object>> data = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Long partsMaxLen = 0L;
        Long thirdPartsMaxLen = 0L;
        List<Long> workOrderIds = new ArrayList<>();

        for(WorkOrderVo item : workOrderVoList) {
            workOrderIds.add(item.getId());
            Long tempPartsMaxLen = Optional.ofNullable(workOrderPartsService.queryPartsMaxCountByWorkOrderId(item.getId(), WorkOrderParts.TYPE_SERVER_PARTS)).orElse(0L);
            Long tempThirdPartsMaxLen = Optional.ofNullable(workOrderPartsService.queryPartsMaxCountByWorkOrderId(item.getId(), WorkOrderParts.TYPE_THIRD_PARTS)).orElse(0L);

            partsMaxLen = partsMaxLen > tempPartsMaxLen ? partsMaxLen : tempPartsMaxLen;
            thirdPartsMaxLen = thirdPartsMaxLen > tempThirdPartsMaxLen ? thirdPartsMaxLen : tempThirdPartsMaxLen;
        }
        Integer serverMaxLen = workOrderServerService.queryMaxCountByWorkOrderId(workOrderIds);

        for (WorkOrderVo o : workOrderVoList) {
            List<Object> row = new ArrayList<>();
            //审核状态
            row.add(this.getAuditStatus(o.getAuditStatus()));

            //typeName
            if (Objects.nonNull(o.getType())) {
                WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
                if (Objects.nonNull(workOrderType)) {
                    row.add(workOrderType.getType());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }
            //pointName
            if (Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(StrUtil.isBlank(pointNew.getName())? "" : pointNew.getName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            row.add(getPointStatusName(o.getPointStatus()));

            //点位客户
            if(Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.queryByIdFromDB(o.getPointId());
                if(Objects.nonNull(pointNew)) {
                    Customer byId = customerService.getById(pointNew.getCustomerId());
                    if(Objects.nonNull(byId)){
                        row.add(StrUtil.isBlank(byId.getName())? "" : byId.getName());
                    } else {
                        row.add("");
                    }
                }else {
                    row.add("");
                }
            }else {
                row.add("");
            }

            //柜机系列
            row.add(getPointProductSeries(o.getProductSeries()));

            //"创建人",
            if (Objects.nonNull(o.getCreaterId())) {
                User user = userService.getUserById(o.getCreaterId());
                if (Objects.nonNull(user)) {
                    row.add(user.getUserName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //"状态",
            //status  1;待处理2:已处理3:待分析4：已完结
            if (Objects.nonNull(o.getStatus())) {
                row.add(this.getStatusStr(o.getStatus()));
            }

            //时效
            /*String prescription = DateUtils.getDatePoor(o.getProcessTime(), o.getCreateTime());
            row.add(StringUtils.isBlank(prescription) ? "" : prescription);*/

            //"描述", "
            row.add(o.getDescribeinfo() == null ? "" : o.getDescribeinfo());

            //"备注",
            row.add(o.getInfo() == null ? "" : o.getInfo());

            //"工单原因",
            //workOrderReasonName
            if (Objects.nonNull(o.getWorkOrderReasonId())) {
                String name = this.getWorkOrderReasonStr(o.getWorkOrderReasonId(), "");
                row.add(name);
            } else {
                row.add("");
            }

            //第三方原因
            /*row.add(o.getThirdReason() == null ? "" : o.getThirdReason());

            if (Objects.nonNull(o.getThirdCompanyType())) {
                this.setThirdCompanyNameAndServerName(o);
                row.add(o.getThirdCompanyName());
            } else {
                row.add("");
            }

            //third_company_pay
            row.add(o.getThirdCompanyPay() == null ? "" : o.getThirdCompanyPay());

            row.add(o.getFee() == null ? "" : o.getFee());

            //图片数量
            LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(Objects.nonNull(workOrder.getWorkOrderType()), File::getFileType, workOrder.getWorkOrderType())
                    .eq(File::getBindId, o.getId());
            List<File> fileList = fileService.getBaseMapper().selectList(eq);
            row.add(CollectionUtils.isEmpty(fileList) ? 0 : fileList.size());

            //"处理时间",
            //processTime
            if (Objects.nonNull(o.getProcessTime())) {
                row.add(simpleDateFormat.format(new Date(o.getProcessTime())));
            } else {
                row.add("");
            }*/

            //创建时间"
            if (Objects.nonNull(o.getCreateTime())) {
                row.add(simpleDateFormat.format(new Date(o.getCreateTime())));
            } else {
                row.add("");
            }
            //服务商
            /*row.add(o.getServerName() == null ? "" : o.getServerName());

            //PaymentMethod
            row.add(getPaymentMethod(o.getPaymentMethod()));


            //"第三方责任对接人"
            row.add(o.getThirdResponsiblePerson() == null ? "" : o.getThirdResponsiblePerson());*/

            //"工单编号",
            row.add(o.getOrderNo() == null ? "" : o.getOrderNo());

            //thirdPaymentStatus
            /*if (Objects.nonNull(o.getThirdPaymentStatus())) {
                if (o.getThirdPaymentStatus().equals(1)) {
                    row.add("无需结算");
                } else if (o.getThirdPaymentStatus().equals(2)) {
                    row.add("未结算");
                } else if (o.getThirdPaymentStatus().equals(3)) {
                    row.add("已结算");
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }*/

            //sn
            if (Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(pointNew.getSnNo());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //审核备注
            row.add(o.getAuditRemarks() == null ? "" : o.getAuditRemarks());

            //专员
            if (Objects.nonNull(o.getCommissionerId())) {
                User user = userService.getUserById(o.getCommissionerId());
                if (Objects.nonNull(user)) {
                    row.add(user.getUserName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //派单时间
            if (Objects.nonNull(o.getAssignmentTime())) {
                row.add(simpleDateFormat.format(new Date(o.getAssignmentTime())));
            } else {
                row.add("");
            }

            List<WorkOrderServerQuery> workOrderServerQueryList = workOrderServerService
                .queryByWorkOrderIdAndServerId(o.getId(), null);
            if (!CollectionUtils.isEmpty(workOrderServerQueryList)) {
                serverMaxLen =
                    workOrderServerQueryList.size() > serverMaxLen ? workOrderServerQueryList.size()
                        : serverMaxLen;
                for (WorkOrderServerQuery item : workOrderServerQueryList) {
                    //服务商",
                    Server server = serverService.getById(item.getServerId());
                    if (Objects.nonNull(server)) {
                        row.add(server.getName());
                    } else {
                        row.add("");
                    }
                    // "费用",
                    row.add(item.getFee() == null ? "" : item.getFee());

                    // "结算方式",
                    row.add(this.getPaymentMethod(item.getPaymentMethod()));
                    // "解决方案",
                    row.add(item.getSolution() == null ? "" : item.getSolution());

                    if (Objects.nonNull(item.getSolutionTime())) {
                        row.add(simpleDateFormat.format(new Date(item.getSolutionTime())));
                    } else {
                        row.add("");
                    }
                    // "处理时长
                    row.add(getPrescriptionStr(item.getPrescription()));
                    //图片个数
                    QueryWrapper<File> wrapper = new QueryWrapper<>();
                    wrapper.eq("type", File.TYPE_WORK_ORDER);
                    wrapper.eq("bind_id", o.getId());
                    wrapper.ge("file_type", 1).lt("file_type", 90000);
                    wrapper.eq("server_id", item.getServerId());

                    int fileCount = fileService.count(wrapper);

                    row.add(fileCount);

                    row.add(Objects.equals(WorkOrderServer.HAS_PARTS, item.getHasParts())?"是":"否");

                    if(Objects.equals(WorkOrderServer.HAS_PARTS, item.getHasParts())) {
                        List<WorkOrderParts> workOrderParts = workOrderPartsService.queryByWorkOrderIdAndServerId(o.getId(), item.getServerId(), WorkOrderParts.TYPE_SERVER_PARTS);
                        if(!CollectionUtils.isEmpty(workOrderParts)) {
                            workOrderParts.forEach(e -> {
                                row.add(e.getSn());
                                row.add(e.getName());
                                row.add(e.getSum());
                                //row.add(e.getAmount());

                                Parts byId = partsService.getById(e.getPartsId());
                                if(Objects.nonNull(byId)) {
                                    row.add(StrUtil.isBlank(byId.getSpecification()) ? "" : byId.getSpecification());
                                }else {
                                    row.add("");
                                }
                            });

                            Long maxLine = partsMaxLen - workOrderParts.size();
                            for (int i = 0; i < maxLine; i++) {
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                            }
                        } else {
                            for (int i = 0; i < partsMaxLen; i++) {
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                            }
                        }
                    } else {
                        for (int i = 0; i < partsMaxLen; i++) {
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                        }
                    }

                    row.add(item.getIsUseThird() ? "是" : "否");
                   if(item.getIsUseThird()){
                       //" 第三方类型",
                       row.add(this.getThirdCompanyType(item.getThirdCompanyType()));
                       // "第三方公司",
                       row.add(item.getThirdCompanyName() == null ? "" : item.getThirdCompanyName());
                       // "人工费用",
                       row.add(item.getArtificialFee() == null ? "" : item.getArtificialFee());
                       //物料费用
                       row.add(item.getMaterialFee() == null ? "" : item.getMaterialFee());
                       // "支付状态",
                       row.add(this.getThirdPaymentStatus(item.getThirdPaymentStatus()));
                       // "第三方原因",
                       row.add(item.getThirdReason() == null ? "" : item.getThirdReason());
                       // "第三方对接人"
                       row.add(item.getThirdResponsiblePerson() == null ? ""
                           : item.getThirdResponsiblePerson());

                       List<WorkOrderParts> thirdWorkOrderParts = workOrderPartsService.queryByWorkOrderIdAndServerId(o.getId(), item.getServerId(), WorkOrderParts.TYPE_THIRD_PARTS);
                       if(!CollectionUtils.isEmpty(thirdWorkOrderParts)) {
                           thirdWorkOrderParts.forEach(e -> {
                               row.add(e.getSn());
                               row.add(e.getName());
                               row.add(e.getSum());
                               row.add(e.getAmount());

                               Parts byId = partsService.getById(e.getPartsId());
                               if(Objects.nonNull(byId)) {
                                   row.add(StrUtil.isBlank(byId.getSpecification()) ? "" : byId.getSpecification());
                               }else {
                                   row.add("");
                               }
                           });

                           Long maxLine = thirdPartsMaxLen - thirdWorkOrderParts.size();
                           for (int i = 0; i < maxLine; i++) {
                               row.add("");
                               row.add("");
                               row.add("");
                               row.add("");
                               row.add("");
                           }
                       } else {
                           for (int i = 0; i < thirdPartsMaxLen; i++) {
                               row.add("");
                               row.add("");
                               row.add("");
                               row.add("");
                               row.add("");
                           }
                       }
                   } else {
                       for (int i = 0; i < 7; i++) {
                           row.add("");
                       }
                       for (int i = 0; i < thirdPartsMaxLen; i++) {
                           row.add("");
                           row.add("");
                           row.add("");
                           row.add("");
                           row.add("");
                       }
                   }
                }

                //给服务商不够最大服务商个数的的补充空白
                int supplementCell = serverMaxLen - workOrderServerQueryList.size();
                for (int i = 0; i < supplementCell; i++) {
                    for (String item : serverHeader) {
                        row.add("");
                    }
                }
            }

            //产品个数
            if (productAll != null && !productAll.isEmpty()) {
                List<ProductInfoQuery> productInfoQueries = null;
                if (Objects.nonNull(o.getProductInfo())) {
                    productInfoQueries = JSON
                        .parseArray(o.getProductInfo(), ProductInfoQuery.class);
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
                } else {
                    for (Product p : productAll) {
                        row.add("");
                    }
                }
            }

            data.add(row);
        }

        for (int i = 0; i < serverMaxLen; i++) {
            for (String item : serverHeader) {
                List<String> headTitle = new ArrayList<>();
                headTitle.add(item + (i + 1));
                headList.add(headTitle);

                if(Objects.equals("是否更换配件", item)) {
                    for(int p = 1; p <= partsMaxLen; p++) {
                        List<String> partsTitle1 = new ArrayList<>();
                        partsTitle1.add("配件编号" + p);
                        List<String> partsTitle2 = new ArrayList<>();
                        partsTitle2.add("配件名称" + p);
                        List<String> partsTitle3 = new ArrayList<>();
                        partsTitle3.add("配件数量" + p);
                        List<String> partsTitle4 = new ArrayList<>();
                        partsTitle4.add("配件规格" + p);

                        headList.add(partsTitle1);
                        headList.add(partsTitle2);
                        headList.add(partsTitle3);
                        headList.add(partsTitle4);
                    }
                }

                if(Objects.equals("第三方对接人", item)) {
                    for(int p = 1; p <= thirdPartsMaxLen ; p++) {
                        List<String> partsTitle1 = new ArrayList<>();
                        partsTitle1.add("配件编号" + p);
                        List<String> partsTitle2 = new ArrayList<>();
                        partsTitle2.add("配件名称" + p);
                        List<String> partsTitle3 = new ArrayList<>();
                        partsTitle3.add("配件数量" + p);
                        List<String> partsTitle4 = new ArrayList<>();
                        partsTitle4.add("配件总售价" + p);
                        List<String> partsTitle5 = new ArrayList<>();
                        partsTitle5.add("配件规格" + p);

                        headList.add(partsTitle1);
                        headList.add(partsTitle2);
                        headList.add(partsTitle3);
                        headList.add(partsTitle4);
                        headList.add(partsTitle5);
                    }
                }
            }
        }

        if (productAll != null && !productAll.isEmpty()) {
            for (Product p : productAll) {
                List<String> headTitle = new ArrayList<>();
                headTitle.add(p.getName());
                headList.add(headTitle);
            }
        }

        String fileName = "工单列表.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(data, sheet, table).finish();
            //EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }


    private String getPointStatusName(Integer pointStatus) {
        String pointStatusName = "";
        if (Objects.equals(pointStatus, 1)) {
            pointStatusName = "移机";
        } else if (Objects.equals(pointStatus, 2)) {
            pointStatusName = "运营中";
        } else if (Objects.equals(pointStatus, 3)) {
            pointStatusName = "已拆机";
        } else if (Objects.equals(pointStatus, 4)) {
            pointStatusName = "初始化";
        } else if (Objects.equals(pointStatus, 5)) {
            pointStatusName = "待安装";
        } else if (Objects.equals(pointStatus, 6)) {
            pointStatusName = "运输中";
        } else if (Objects.equals(pointStatus, 7)) {
            pointStatusName = "安装中";
        } else if (Objects.equals(pointStatus, 8)) {
            pointStatusName = "安装完成";
        } else if (Objects.equals(pointStatus, 9)) {
            pointStatusName = "已暂停";
        } else if (Objects.equals(pointStatus, 10)) {
            pointStatusName = "已取消";
        } else if (Objects.equals(pointStatus, 11)) {
            pointStatusName = "已过保";
        }
        return pointStatusName;
    }

    private String getPointProductSeries(Integer productSeries) {
        productSeries = productSeries == null ? -1 : productSeries;
        String result = "";
        switch (productSeries) {
            case 1:
                result = "取餐柜";
                break;
            case 2:
                result = "餐厅柜";
                break;
            case 3:
                result = "换电柜";
                break;
            case 4:
                result = "充电柜";
                break;
            case 5:
                result = "寄存柜";
                break;
            case 6:
                result = "生鲜柜";
                break;
        }
        return result;
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

    private String getPrescriptionStr(Long prescription) {
        if (Objects.isNull(prescription)) {
            return "";
        }
        long day = prescription / (24L * 3600 * 1000);
        long hour = prescription % (24L * 3600 * 1000) / 3600 / 1000;
        long minute = prescription % (24L * 3600 * 1000) % (3600 * 1000) / 60000;
        return day + "天" + hour + "时" + minute + "分钟";
    }

    private String getOverInsurance(Integer overInsurance) {
        String overInsuranceStr = "";
        switch (overInsurance) {
            case 0:
                overInsuranceStr = "未过保";
                break;
            case 1:
                overInsuranceStr = "已过保";
                break;
        }
        return overInsuranceStr;
    }

    private String getPaymentMethod(Integer method) {
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

    private String getThirdPaymentStatus(Integer status) {
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
            case 6:
                statusStr = "待派单";
                break;
        }
        return statusStr;
    }

    private String getAuditStatus(Integer auditStatus) {
        String auditStatusStr = "";
        auditStatus = auditStatus == null ? -1 : auditStatus;

        switch (auditStatus) {
            case 0:
                auditStatusStr = "未审核";
                break;
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

    private String getThirdCompanyType(Integer thirdCompanyType) {
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

    private String getWorkOrderReasonStr(Long id, String name) {
        WorkOrderReason workOrderReason = workOrderReasonService.getById(id);
        if (Objects.isNull(workOrderReason)) {
            return name;
        }

        if (Objects.equals(workOrderReason.getParentId(), WorkOrderReason.PARENT_NODE)) {
            return workOrderReason.getName() + "/" + name;
        }

        return getWorkOrderReasonStr(workOrderReason.getParentId(),
            workOrderReason.getName() + "/" + name);
    }

    @Override
    public R insertWorkOrder(WorkOrderQuery workOrder) {
        User user = userService.getUserById(workOrder.getCreaterId());
        if (Objects.isNull(user)) {
            log.error("SAVE_WORK_ORDER ERROR ,NOT FOUND USER BY ID ,ID:{}",
                workOrder.getCreaterId());
            return R.failMsg("用户不存在!");
        }
        if (ObjectUtil.equal(WorkOrderType.TRANSFER, workOrder.getWorkOrderType()) && ObjectUtil
            .isNotEmpty(workOrder.getProductSerialNumberIdList())) {
            //移机
            //转移产品序列号
            if (Objects.isNull(workOrder.getTransferSourcePointId()) || Objects
                .isNull(workOrder.getTransferDestinationPointId())) {
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
        /*if (ObjectUtil.isNotEmpty(workOrder.getFileList())) {
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
        }*/

        return R.ok();
    }


    @Override
    public R reconciliation(WorkOrderQuery workOrder) {
        //workOrder.setProcessTimeStart(workOrder.getCreateTimeStart());
        //workOrder.setProcessTimeEnd(workOrder.getCreateTimeEnd());]
        BigDecimal zero = new BigDecimal("0");

        Integer count = baseMapper.countOrderList(workOrder);
        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);
        workOrderVoList.forEach(o -> {

            //o.setPaymentMethodName(getPaymentMethod(o.getPaymentMethod()));

            WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
            if (Objects.nonNull(workOrderType)) {
                o.setWorkOrderType(workOrderType.getType());
            }

            PointNew point = pointNewService.getById(o.getPointId());
            if (Objects.nonNull(point)) {
                o.setPointName(point.getName());
            }
            WorkOrderReason workOrderReason = workOrderReasonService
                .getById(o.getWorkOrderReasonId());

            if (Objects.nonNull(workOrderReason)) {
                o.setWorkOrderReasonName(workOrderReason.getName());
            }
            //o.setThirdCompanyPay(o.getThirdCompanyPay());

            List<WorkOrderServerQuery> workOrderServerQueryList = workOrderServerService
                .queryByWorkOrderIdAndServerId(o.getId(), null);

            o.setWorkOrderServerList(workOrderServerQueryList);
            if (!CollectionUtils.isEmpty(workOrderServerQueryList)) {
                BigDecimal totalFee = new BigDecimal("0");
                for (WorkOrderServerQuery item : workOrderServerQueryList) {
                    totalFee = totalFee.add(Optional.ofNullable(item.getFee()).orElse(zero));
                }
                o.setTotalFee(totalFee);
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("total", count);
        map.put("data", workOrderVoList);
        return R.ok(map);
    }

    private String getThirdCompanyName(WorkOrderServer o) {
        if (o.getThirdCompanyType() != null && o.getThirdCompanyType()
            .equals(WorkOrder.COMPANY_TYPE_CUSTOMER)) {
            Customer customer = customerService.getById(o.getThirdCompanyId());
            if (Objects.nonNull(customer)) {
                return customer.getName();
            }
        }

        if (o.getThirdCompanyType() != null && o.getThirdCompanyType()
            .equals(WorkOrder.COMPANY_TYPE_SUPPLIER)) {
            Supplier supplier = supplierService.getById(o.getThirdCompanyId());
            if (Objects.nonNull(supplier)) {
                return supplier.getName();
            }
        }

        if (o.getThirdCompanyType() != null && o.getThirdCompanyType()
            .equals(WorkOrder.COMPANY_TYPE_SERVER)) {
            Server server = serverService.getById(o.getThirdCompanyId());
            if (Objects.nonNull(server)) {
                return server.getName();
            }
        }
        return "";
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
        if (workOrder.getCreateTimeStart() == null || workOrder.getCreateTimeEnd() == null) {
            throw new CustomBusinessException("请选择创建开始时间结束时间");
        }

        List<WorkOrderVo> workOrderVoList = baseMapper.orderList(workOrder);

        if (ObjectUtil.isEmpty(workOrderVoList)) {
            throw new CustomBusinessException("没有查询到工单!无法导出！");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Long> workOrderIds = new ArrayList<>();
        workOrderVoList.forEach(item -> {
            workOrderIds.add(item.getId());
        });
        Integer serverMaxLen = workOrderServerService.queryMaxCountByWorkOrderId(workOrderIds);

        //headerSet
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"工单类型", "点位", "工单原因", "创建时间"};

        String[] serverHeader = {"服务商", "工单费用", "解决时间", "处理时长", " 第三方类型", "第三方公司", "应收第三方人工费",
            "应收第三方物料费"};
        String totalFeeHeader = "工单总费用";

        for (String s : header) {
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(s);
            headList.add(headTitle);
        }

        table.setHead(headList);

        List<List<Object>> data = new ArrayList<>();

        for (WorkOrderVo o : workOrderVoList) {
            List<Object> row = new ArrayList<>();

            if (Objects.nonNull(o.getType())) {
                WorkOrderType workOrderType = workOrderTypeService.getById(o.getType());
                if (Objects.nonNull(workOrderType)) {
                    row.add(workOrderType.getType());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }
            //pointName
            if (Objects.nonNull(o.getPointId())) {
                PointNew pointNew = pointNewService.getById(o.getPointId());
                if (Objects.nonNull(pointNew)) {
                    row.add(pointNew.getName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //"工单原因",
            //workOrderReasonName
            if (Objects.nonNull(o.getWorkOrderReasonId())) {
                WorkOrderReason workOrderReason = workOrderReasonService
                    .getById(o.getWorkOrderReasonId());
                if (Objects.nonNull(workOrderReason)) {
                    row.add(workOrderReason.getName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }

            //创建时间"
            if (Objects.nonNull(o.getCreateTime())) {
                row.add(simpleDateFormat.format(new Date(o.getCreateTime())));
            } else {
                row.add("");
            }

            List<WorkOrderServerQuery> workOrderServerQueryList = workOrderServerService
                .queryByWorkOrderIdAndServerId(o.getId(), null);
            if (!CollectionUtils.isEmpty(workOrderServerQueryList)) {

                BigDecimal totalFee = new BigDecimal("0");
                BigDecimal zero = new BigDecimal("0");
                serverMaxLen =
                    workOrderServerQueryList.size() > serverMaxLen ? workOrderServerQueryList.size()
                        : serverMaxLen;

                for (WorkOrderServerQuery item : workOrderServerQueryList) {
                    //计算总费用
                    totalFee = totalFee.add(Optional.ofNullable(item.getFee()).orElse(zero));

                    //服务商",
                    Server server = serverService.getById(item.getServerId());
                    if (Objects.nonNull(server)) {
                        row.add(server.getName());
                    } else {
                        row.add("");
                    }

                    // "费用",
                    row.add(item.getFee() == null ? "" : item.getFee());
                    // "解决时间",
                    if (Objects.nonNull(item.getSolutionTime())) {
                        row.add(simpleDateFormat.format(new Date(item.getSolutionTime())));
                    } else {
                        row.add("");
                    }
                    // "处理时长
                    row.add(getPrescriptionStr(item.getPrescription()));

                    //" 第三方类型",
                    row.add(this.getThirdCompanyType(item.getThirdCompanyType()));
                    // "第三方公司",
                    row.add(item.getThirdCompanyName() == null ? "" : item.getThirdCompanyName());
                    // "人工费用",
                    row.add(item.getArtificialFee() == null ? "" : item.getArtificialFee());
                    //物料费用
                    row.add(item.getMaterialFee() == null ? "" : item.getMaterialFee());
                }

                //给服务商不够最大服务商个数的的补充空白
                int supplementCell = serverMaxLen - workOrderServerQueryList.size();
                for (int i = 0; i < supplementCell; i++) {
                    for (String item : serverHeader) {
                        row.add("");
                    }
                }
                row.add(totalFee);
            }

            data.add(row);
        }

        for (int i = 0; i < serverMaxLen; i++) {
            for (String item : serverHeader) {
                List<String> headTitle = new ArrayList<>();
                headTitle.add(item + (i + 1));
                headList.add(headTitle);
            }
        }

        //工单总费用
        List<String> headTitle = new ArrayList<String>();
        headTitle.add(totalFeeHeader);
        headList.add(headTitle);

        String fileName = "工单列表.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(data, sheet, table).finish();
            //EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
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
            && productSerialNumberQuery.getRightInterval() <= 9999
            && productSerialNumberQuery.getLeftInterval() >= 0) {
            return R.failMsg("请设置合适的编号区间!");
        }
        DecimalFormat decimalFormat = new DecimalFormat("0000");

        for (Long i = productSerialNumberQuery.getLeftInterval();
            i <= productSerialNumberQuery.getRightInterval(); i++) {

            ProductSerialNumber productSerialNumber = new ProductSerialNumber();
            productSerialNumber.setSerialNumber(
                productSerialNumberQuery.getPrefix() + "_" + decimalFormat.format(i));
            productSerialNumber.setProductId(productSerialNumberQuery.getProductId());
            productSerialNumber.setCreateTime(System.currentTimeMillis());
            productSerialNumberMapper.insert(productSerialNumber);
        }
        return R.ok();
    }

    @Override
    public IPage getSerialNumberPage(Long offset, Long size,
        ProductSerialNumberQuery productSerialNumber) {
        return productSerialNumberMapper
            .getSerialNumberPage(PageUtil.getPage(offset, size), productSerialNumber);
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
            //BigDecimal totalAmount = Objects.isNull(workOrder.getFee()) ? BigDecimal.ZERO : workOrder.getFee();
            for (Map.Entry<Long, Integer> entry : workOrderQuery
                .getProductSerialNumberIdAndSetNoMap().entrySet()) {
                ProductSerialNumber productSerialNumber = productSerialNumberMapper
                    .selectById(entry.getKey());
                if (Objects.isNull(productSerialNumber)) {
                    log.error("not found productSerialNumber by id:{}", entry.getKey());
                    throw new CustomBusinessException("未找到产品序列号!");
                }
                productSerialNumber.setPointId(workOrder.getId());
                productSerialNumber.setSetNo(entry.getValue());
                productSerialNumberMapper.updateById(productSerialNumber);
                //totalAmount = totalAmount.add(productSerialNumber.getPrice());
            }
            WorkOrder workOrderUpdate = new WorkOrder();
            workOrderUpdate.setId(workOrderUpdate.getId());
            //workOrder.setFee(totalAmount);
            baseMapper.updateById(workOrderUpdate);
        }
        return R.ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveWorkerOrder(WorkOrderQuery workOrder) {
        R r = checkProperties(workOrder);
        if (Objects.nonNull(r)) {
            return r;
        }

        if (!Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)
            && !Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            return R.fail("状态请选择为待派单或待处理状态");
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)) {
            if (CollectionUtils.isEmpty(workOrder.getWorkOrderServerList())) {
                return R.fail("工单必须填写服务商相关信息");
            }
            for (WorkOrderServerQuery item : workOrder.getWorkOrderServerList()) {
                if (Objects.isNull(item.getServerId())) {
                    return R.fail("工单必须添加服务商");
                }

                if (Objects.isNull(item.getFee())) {
                    return R.fail("工单必须添加应收第三方人工费");
                }

                if (item.getIsUseThird()) {
                    if (Objects.isNull(item.getThirdCompanyType())) {
                        return R.fail("工单必须添加第三方公司类型");
                    }

                    if (Objects.isNull(item.getThirdCompanyId())) {
                        return R.fail("工单必须添加第三方公司");
                    }

                    if (Objects.isNull(item.getPaymentMethod())) {
                        return R.fail("工单必须添加结算方式");
                    }

                    if (Objects.isNull(item.getFee())) {
                        return R.fail("工单必须添加工单费用");
                    }

                    if (Objects.isNull(item.getMaterialFee())) {
                        return R.fail("工单必须添加应收第三方物料费");
                    }

                    if (Objects.isNull(item.getThirdPaymentStatus())) {
                        return R.fail("工单必须添加第三方支付状态");
                    }

                    if (Objects.isNull(item.getThirdReason())) {
                        return R.fail("工单必须添加第三方原因");
                    }

                    if (Objects.isNull(item.getThirdResponsiblePerson())) {
                        return R.fail("工单必须添加第三方责任人");
                    }

                    checkAndclearEntry(item.getThirdWorkOrderParts());
                }
            }
            workOrder.setAssignmentTime(workOrder.getCreateTime());
        }

        for (WorkOrderServerQuery item : workOrder.getWorkOrderServerList()) {
            if (Objects.equals(item.getHasParts(), WorkOrderServer.HAS_PARTS)) {
                if (!checkAndclearEntry(item.getWorkOrderParts())) {
                    return R.fail("请添加服务商相关物料信息");
                }
            }
        }

        WorkOrderType workOrderType = workOrderTypeService.getById(workOrder.getType());
        long startTime = DateUtil.beginOfDay(DateUtil.date()).toInstant().toEpochMilli();
        long endTime = System.currentTimeMillis();
        long maxDaySumNo = queryMaxDaySumNoByType(startTime, endTime, workOrderType.getId());
        maxDaySumNo++;
        workOrder.setDaySumNo(maxDaySumNo);
        workOrder
            .setOrderNo(generateWorkOrderNo(workOrderType, String.format("%05d", maxDaySumNo)));

        if (Objects.nonNull(workOrder.getProductInfoList())) {
            Iterator<ProductInfoQuery> iterator = workOrder.getProductInfoList().iterator();
            while (iterator.hasNext()) {
                ProductInfoQuery productInfoQuery = iterator.next();
                if (Objects.isNull(productInfoQuery.getProductId()) || Objects
                    .isNull(productInfoQuery.getNumber())) {
                    iterator.remove();
                }
            }
            String productInfo = JSON.toJSONString(workOrder.getProductInfoList());
            workOrder.setProductInfo(productInfo);
        }

        int insert = this.baseMapper.insert(workOrder);
        if (insert == 0) {
            return R.fail("数据库保存出错");
        }

        if (!CollectionUtils.isEmpty(workOrder.getWorkOrderServerList())) {
            workOrder.getWorkOrderServerList().forEach(item -> {
                WorkOrderServer workOrderServer = new WorkOrderServer();
                BeanUtils.copyProperties(item, workOrderServer);
                workOrderServer.setWorkOrderId(workOrder.getId());
                //服务商名称
                if (item.getServerId() != null) {
                    Server server = serverService.getById(item.getServerId());
                    if (Objects.nonNull(server)) {
                        workOrderServer.setServerName(server.getName());
                    }
                }
                //第三方公司名称
                if (item.getThirdCompanyId() != null && item.getThirdCompanyType() != null) {
                    if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)) {
                        Customer customer = customerService.getById(item.getThirdCompanyId());
                        if (Objects.nonNull(customer)) {
                            workOrderServer.setThirdCompanyName(customer.getName());
                        }
                    }

                    if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)) {
                        Supplier supplier = supplierService.getById(item.getThirdCompanyId());
                        if (Objects.nonNull(supplier)) {
                            workOrderServer.setThirdCompanyName(supplier.getName());
                        }
                    }

                    if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SERVER)) {
                        Server server = serverService.getById(item.getThirdCompanyId());
                        if (Objects.nonNull(server)) {
                            workOrderServer.setThirdCompanyName(server.getName());
                        }
                    }
                }
                workOrderServer.setCreateTime(System.currentTimeMillis());

                clareAndAddWorkOrderParts(workOrder.getId(), workOrderServer.getServerId(), item.getWorkOrderParts(), WorkOrderParts.TYPE_SERVER_PARTS);
                BigDecimal totalMaterialFee = clareAndAddWorkOrderParts(workOrder.getId(), workOrderServer.getServerId(), item.getThirdWorkOrderParts(), WorkOrderParts.TYPE_THIRD_PARTS);

                workOrderServer.setMaterialFee(totalMaterialFee);
                workOrderServerService.save(workOrderServer);
            });
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)) {
            this.sendWorkServerNotifyMq(workOrder);
        }

        return R.ok();
    }


    @Override
    public boolean checkAndclearEntry(List<WorkOrderParts> workOrderParts) {
        if (CollectionUtils.isEmpty(workOrderParts)) {
            return false;
        }

        Iterator<WorkOrderParts> iterator = workOrderParts.iterator();
        while (iterator.hasNext()) {
            WorkOrderParts next = iterator.next();
            if(Objects.isNull(next.getPartsId()) || Objects.isNull(next.getSum()) || Objects.isNull(next.getSellPrice())) {
                iterator.remove();
            }
        }

        return !CollectionUtils.isEmpty(workOrderParts);
    }


    @Override
    public String generateWorkOrderNo(WorkOrderType type, String no) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        StringBuilder sb = new StringBuilder();
        sb.append(Objects.isNull(type) ? "UNKNOWN" : type.getNo()).append("-");
        sb.append(sdf.format(new Date())).append("-");
        sb.append(no);

        return sb.toString();
    }

    @Override
    public Long queryMaxDaySumNoByType(Long startTime, Long endTime, Long id) {
        return Optional.ofNullable(this.baseMapper.queryMaxDaySumNoByType(startTime, endTime, id))
            .orElse(0L);
    }


    @Override
    public R updateWorkOrderStatus(WorkerOrderUpdateStatusQuery query) {
        WorkOrder workOrder = baseMapper.selectById(query.getId());

        if (Objects.isNull(workOrder)) {
            return R.fail("id不存在");
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_SUSPEND)
            && (!Objects.equals(query.getStatus(), WorkOrder.STATUS_SUSPEND)
            && !Objects.equals(query.getStatus(), WorkOrder.STATUS_INIT))) {
            return R.fail("工单已暂停只能修改为待处理");
        }

        if (Objects.equals(query.getStatus(), WorkOrder.STATUS_SUSPEND)) {
            if (!Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)
                && !Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_SUSPEND)) {

                return R.fail("非待处理的工单不可修改为已暂停");
            }
        }

        if (Objects.equals(workOrder.getAuditStatus(), WorkOrder.AUDIT_STATUS_PASSED)) {
            return R.fail("审核通过的工单不允许修改");
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT) && Objects
            .equals(query.getStatus(), WorkOrder.STATUS_INIT)) {
            workOrder.setAssignmentTime(System.currentTimeMillis());
            this.sendWorkServerNotifyMq(workOrder);
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
        if (Objects.isNull(oldWorkOrder)) {
            return R.fail("未查询到工单");
        }

        workOrder.setCreaterId(oldWorkOrder.getCreaterId());
        workOrder.setCreateTime(oldWorkOrder.getCreateTime());

        R r = checkProperties(workOrder);
        if (Objects.nonNull(r)) {
            return r;
        }

        //List<WorkOrderServerQuery> workOrderServerList = workOrder.getWorkOrderServerList();

        if (!Objects.equals(oldWorkOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)
            && !Objects.equals(oldWorkOrder.getCommissionerId(), workOrder.getCommissionerId())) {
            return R.fail(getStatusStr(oldWorkOrder.getStatus()) + "状态，不可修改专员信息");
        }

        if (Objects.isNull(oldWorkOrder)) {
            return R.fail("未查询到工单相关信息");
        }

        if (Objects.equals(oldWorkOrder.getStatus(), WorkOrder.STATUS_SUSPEND)
            && !Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_SUSPEND)
            && !Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)) {
            return R.fail("工单已暂停只能修改为待处理");
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_SUSPEND)) {
            if (!Objects.equals(oldWorkOrder.getStatus(), WorkOrder.STATUS_INIT)
                && !Objects.equals(oldWorkOrder.getStatus(), WorkOrder.STATUS_SUSPEND)) {

                return R.fail("非待处理的工单不可修改为已暂停");
            }
        }

        if (Objects.equals(oldWorkOrder.getAuditStatus(), WorkOrder.AUDIT_STATUS_PASSED)) {
            return R.fail("审核通过的工单不允许修改");
        }

        if (!Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            if (CollectionUtils.isEmpty(workOrder.getWorkOrderServerList())) {
                return R.fail("工单必须填写服务商相关信息");
            }
            for (WorkOrderServerQuery item : workOrder.getWorkOrderServerList()) {
                if (Objects.isNull(item.getServerId())) {
                    return R.fail("工单必须添加服务商");
                }

                if (Objects.isNull(item.getPaymentMethod())) {
                    return R.fail("工单必须添加结算方式");
                }

                if (Objects.isNull(item.getFee())) {
                    return R.fail("工单必须添加工单费用");
                }

                if (item.getIsUseThird()) {
                    if (Objects.isNull(item.getThirdCompanyType())) {
                        return R.fail("工单必须添加第三方公司类型");
                    }

                    if (Objects.isNull(item.getThirdCompanyId())) {
                        return R.fail("工单必须添加第三方公司");
                    }

                    if (Objects.isNull(item.getArtificialFee())) {
                        return R.fail("工单必须添加应收第三方人工费");
                    }

                    if (Objects.isNull(item.getMaterialFee())) {
                        return R.fail("工单必须添加应收第三方物料费");
                    }

                    if (Objects.isNull(item.getThirdPaymentStatus())) {
                        return R.fail("工单必须添加第三方支付状态");
                    }

                    if (Objects.isNull(item.getThirdReason())) {
                        return R.fail("工单必须添加第三方原因");
                    }

                    if (Objects.isNull(item.getThirdResponsiblePerson())) {
                        return R.fail("工单必须添加第三方责任人");
                    }

                    checkAndclearEntry(item.getThirdWorkOrderParts());
                }
            }
            //workOrder.setAssignmentTime(workOrder.getCreateTime());
        }

        for (WorkOrderServerQuery item : workOrder.getWorkOrderServerList()) {
            if (Objects.equals(item.getHasParts(), WorkOrderServer.HAS_PARTS)) {
                if (!checkAndclearEntry(item.getWorkOrderParts())) {
                    return R.fail("请添加服务商相关物件信息");
                }
            }
        }

        //非待派发状态 不可修添加或删除服务商
        if (!Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            int workOrderServerOldCount = 0;
            int workOrderServerCount = 0;
            if (!CollectionUtils.isEmpty(workOrder.getWorkOrderServerList())) {
                workOrderServerOldCount = workOrder.getWorkOrderServerList().size();
            }
            if (!CollectionUtils.isEmpty(workOrder.getWorkOrderServerList())) {
                workOrderServerCount = workOrder.getWorkOrderServerList().size();
            }
            if (!(workOrderServerOldCount == workOrderServerCount)) {
                return R.fail("非待派发模式不可添加或删除服务商");
            }
        }

        if (Objects.nonNull(workOrder.getProductInfoList())) {
            Iterator<ProductInfoQuery> iterator = workOrder.getProductInfoList().iterator();
            while (iterator.hasNext()) {
                ProductInfoQuery productInfoQuery = iterator.next();
                if (Objects.isNull(productInfoQuery.getProductId()) || Objects
                    .isNull(productInfoQuery.getNumber())) {
                    iterator.remove();
                }
            }
            String productInfo = JSON.toJSONString(workOrder.getProductInfoList());
            workOrder.setProductInfo(productInfo);
        }

        //后台直接派单，添加派单时间
        if (Objects.equals(oldWorkOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)
            && Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)
            && Objects.isNull(oldWorkOrder.getAssignmentTime())) {
            workOrder.setAssignmentTime(System.currentTimeMillis());
            workOrder.setOrderNo(oldWorkOrder.getOrderNo());
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            //服务商第三方信息
            workOrderServerService.removeByWorkOrderId(workOrder.getId());
            if (!CollectionUtils.isEmpty(workOrder.getWorkOrderServerList())) {
                workOrder.getWorkOrderServerList().forEach(item -> {
                    WorkOrderServer workOrderServer = new WorkOrderServer();
                    BeanUtils.copyProperties(item, workOrderServer);

                    workOrderServer.setSolutionTime(null);
                    workOrderServer.setPrescription(null);
                    workOrderServer.setWorkOrderId(workOrder.getId());
                    //服务商名称
                    if (item.getServerId() != null) {
                        Server server = serverService.getById(item.getServerId());
                        if (Objects.nonNull(server)) {
                            workOrderServer.setServerName(server.getName());
                        }
                    }
                    //第三方公司名称
                    if (item.getThirdCompanyId() != null && item.getThirdCompanyType() != null) {
                        if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)) {
                            Customer customer = customerService.getById(item.getThirdCompanyId());
                            if (Objects.nonNull(customer)) {
                                workOrderServer.setThirdCompanyName(customer.getName());
                            }
                        }

                        if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)) {
                            Supplier supplier = supplierService.getById(item.getThirdCompanyId());
                            if (Objects.nonNull(supplier)) {
                                workOrderServer.setThirdCompanyName(supplier.getName());
                            }
                        }

                        if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SERVER)) {
                            Server server = serverService.getById(item.getThirdCompanyId());
                            if (Objects.nonNull(server)) {
                                workOrderServer.setThirdCompanyName(server.getName());
                            }
                        }
                    }

                    workOrderServer.setCreateTime(System.currentTimeMillis());

                    clareAndAddWorkOrderParts(workOrder.getId(), workOrderServer.getServerId(), item.getWorkOrderParts(), WorkOrderParts.TYPE_SERVER_PARTS);
                    BigDecimal totalMaterialFee = clareAndAddWorkOrderParts(workOrder.getId(), workOrderServer.getServerId(), item.getThirdWorkOrderParts(), WorkOrderParts.TYPE_THIRD_PARTS);

                    workOrderServer.setMaterialFee(totalMaterialFee);
                    workOrderServerService.save(workOrderServer);
                });
            }
        } else {
            updateWorkOrderServer(workOrder.getWorkOrderServerList());
        }


        if(Objects.equals(oldWorkOrder.getAuditStatus(), WorkOrder.AUDIT_STATUS_PASSED)
            || Objects.equals(oldWorkOrder.getAuditStatus(), WorkOrder.AUDIT_STATUS_FAIL)) {
            workOrder.setAuditStatus(WorkOrder.AUDIT_STATUS_WAIT);
        }

        this.baseMapper.updateOne(workOrder);

        //发送Mq通知
        if (Objects.equals(oldWorkOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)
            && Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_INIT)) {
            this.sendWorkServerNotifyMq(this.getById(workOrder.getId()));
        }

        return R.ok();
    }

    @Override
    public BigDecimal clareAndAddWorkOrderParts(Long oid, Long sid, List<WorkOrderParts> workOrderParts, Integer type){
        workOrderPartsService.deleteByOidAndServerId(oid, sid, type);

        BigDecimal totalResult = BigDecimal.valueOf(0);
        if(CollectionUtils.isEmpty(workOrderParts)) {
            return totalResult;
        }

        for(WorkOrderParts e : workOrderParts) {
            if(Objects.isNull(e.getSum())) {
                continue;
            }

            if(Objects.isNull(e.getPartsId())) {
                continue;
            }

            Parts parts = partsService.queryByIdFromDB(e.getPartsId());
            if(Objects.isNull(parts)) {
                continue;
            }

            WorkOrderParts insert = new WorkOrderParts();
            insert.setServerId(sid);
            insert.setWorkOrderId(oid);
            insert.setName(parts.getName());
            insert.setSum(e.getSum());
            insert.setType(type);
            insert.setSn(parts.getSn());
            insert.setPartsId(parts.getId());
            insert.setSellPrice(e.getSellPrice());
            BigDecimal amount = BigDecimal.valueOf(0);
            if(Objects.equals(WorkOrderParts.TYPE_SERVER_PARTS, type)) {
                amount = qeuryAmount(e.getSum(), parts.getPurchasePrice());
            }

            if(Objects.equals(WorkOrderParts.TYPE_THIRD_PARTS, type)) {
                amount = qeuryAmount(e.getSum(), e.getSellPrice());
            }

            insert.setAmount(amount);
            insert.setCreateTime(System.currentTimeMillis());
            insert.setUpdateTime(System.currentTimeMillis());
            workOrderPartsService.insert(insert);

            totalResult = totalResult.add(amount);
        }

        return totalResult;
    }

    private BigDecimal qeuryAmount(Integer sum, BigDecimal price){
        return BigDecimal.valueOf(Optional.ofNullable(sum).orElse(0)).multiply(Optional.ofNullable(price).orElse(new BigDecimal("0"))).setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public Boolean checkServerProcess(Long id) {
        List<WorkOrderServerQuery> workOrderServerQueries = workOrderServerService
            .queryByWorkOrderIdAndServerId(id, null);
        boolean flag = true;
        if (!CollectionUtils.isEmpty(workOrderServerQueries)) {
            for (WorkOrderServerQuery item : workOrderServerQueries) {
                if (Objects.isNull(item.getSolutionTime())) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    @Override
    public List<AfterCountVo> qualityCount(Long pointId, Integer cityId, Long startTime,
        Long endTime) {

        return this.baseMapper.qualityCount(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterCountListVo> qualityCountList(Long pointId, Integer cityId, Long startTime,
        Long endTime) {

        return this.baseMapper.qualityCountList(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterOrderVo> afterWorkOrderByCity(Long pointId, Integer cityId, Long startTime,
        Long endTime) {
        return this.baseMapper.afterWorkOrderByCity(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterOrderVo> afterWorkOrderByPoint(Long pointId, Integer cityId, Long startTime,
        Long endTime) {
        return this.baseMapper.afterWorkOrderByPoint(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterOrderVo> afterWorkOrderList(Long pointId, Integer cityId, Long startTime,
        Long endTime) {
        return this.baseMapper.afterWorkOrderList(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterOrderVo> installWorkOrderByCity(Long pointId, Integer cityId, Long startTime,
        Long endTime) {
        return this.baseMapper.installWorkOrderByCity(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterOrderVo> installWorkOrderByPoint(Long pointId, Integer cityId, Long startTime,
        Long endTime) {
        return this.baseMapper.installWorkOrderByPoint(pointId, cityId, startTime, endTime);
    }

    @Override
    public List<AfterOrderVo> installWorkOrderList(Long pointId, Integer cityId, Long startTime,
        Long endTime) {
        return this.baseMapper.installWorkOrderList(pointId, cityId, startTime, endTime);
    }

    @Override
    public R updateWorkorderProcessTime(Long id, Long serverId) {
        List<WorkOrderServerQuery> workOrderServers = workOrderServerService
            .queryByWorkOrderIdAndServerId(id, serverId);
        if (CollectionUtils.isEmpty(workOrderServers) || workOrderServers.size() > 1) {
            return R.fail("未查询到工单服务商或查询出多个工单服务商");
        }

        WorkOrder workOrder = this.getById(id);
        if (Objects.isNull(workOrder)) {
            return R.fail("未查询到工单信息");
        }

        if (!(workOrder.getStatus() >= 1 && workOrder.getStatus() <= 4)) {
            return R.fail("待派发或已暂停状态下不可修改");
        }

        WorkOrderServerQuery old = workOrderServers.get(0);
        WorkOrderServer update = new WorkOrderServer();
        update.setId(old.getId());
        update.setSolutionTime(System.currentTimeMillis());
        update.setPrescription(update.getSolutionTime() - workOrder.getAssignmentTime());
        workOrderServerService.updateById(update);
        return R.ok();
    }

    @Override
    public R updateAuditStatus(WorkOrderAuditStatusQuery workOrderAuditStatusQuery) {
        if (Objects.isNull(workOrderAuditStatusQuery.getId())
            || Objects.isNull(workOrderAuditStatusQuery.getAuditStatus())) {
            return R.fail("参数不合法");
        }

        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return R.fail("未查询到相关用户");
        }

        WorkOrder workOrder = this.getById(workOrderAuditStatusQuery.getId());
        if (Objects.isNull(workOrder)) {
            return R.fail("未查询到相关工单");
        }

        if (workOrder.getStatus() < WorkOrder.STATUS_PROCESSING
            || Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            return R.fail("工单未处理，请处理后审核");
        }

        /*if (!Objects.equals(workOrder.getAuditStatus(), WorkOrder.AUDIT_STATUS_WAIT)
                && Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_FINISHED)
                && Objects.equals(workOrderAuditStatusQuery.getAuditStatus(), WorkOrder.AUDIT_STATUS_WAIT)) {
            saveWorkAuditNotify(workOrder);
        }*/

        WorkOrder workOrderUpdate = new WorkOrder();
        workOrderUpdate.setId(workOrderAuditStatusQuery.getId());
        workOrderUpdate.setAuditStatus(workOrderAuditStatusQuery.getAuditStatus());
        workOrderUpdate.setAuditRemarks(workOrderAuditStatusQuery.getAuditRemarks());
        workOrderUpdate.setAuditUid(uid);
        if (Objects
            .equals(workOrderAuditStatusQuery.getAuditStatus(), WorkOrder.AUDIT_STATUS_PASSED)) {
            workOrderUpdate.setStatus(WorkOrder.STATUS_FINISHED);
        }
        this.updateById(workOrderUpdate);
        return R.ok();
    }

    @Override
    public R putAdminPointNewCreateUser(Long id, Long createUid) {
        if (Objects.isNull(id) || Objects.isNull(createUid)) {
            return R.fail("参数非法，请检查");
        }

        User user = userService.getUserById(createUid);
        if (Objects.isNull(user)) {
            return R.fail("没有查询到该用户");
        }

        Integer len = baseMapper.putAdminPointNewCreateUser(id, createUid);

        if (len != null && len > 0) {
            return R.ok();
        }

        return R.fail("修改失败");
    }

    @Override
    public R queryAssignmentStatusList(Long offset, Long size, Integer auditStatus, Integer status,
        Integer type, Long createTimeStart, String serverName, String pointName) {
        Long uid = null;
        Long serverId = null;
        List<Long> workOrderServerIds = null;
        Long createTimeEnd = null;

        if (Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_COMMISSIONER)) {
            uid = SecurityUtils.getUid();
        } else if (Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_PATROL_APPLET)) {
            User user = userService.getUserById(SecurityUtils.getUid());
            if (Objects.isNull(user)) {
                return R.fail("未查询到相关服务商账号");
            }
            serverId = user.getThirdId();
            workOrderServerIds = workOrderServerService.queryWorkOrderIds(user.getThirdId());
        }

        if (Objects.nonNull(createTimeStart)) {
            createTimeEnd = createTimeStart + 24 * 3600000;
        }

        Page<WorkOrderAssignmentVo> page = PageUtil.getPage(offset, size);
        page = baseMapper
            .queryAssignmentStatusList(page, uid, workOrderServerIds, auditStatus, status, type,
                createTimeStart, createTimeEnd, serverName, pointName);
        List<WorkOrderAssignmentVo> data = page.getRecords();

        if (!CollectionUtils.isEmpty(data)) {
            for (WorkOrderAssignmentVo item : data) {
                item.setParentWorkOrderReason(
                    this.getParentWorkOrderReason(item.getWorkOrderReasonId()));

                if (Objects.nonNull(item.getProductInfo())) {
                    List<ProductInfoQuery> productInfo = JSON
                        .parseArray(item.getProductInfo(), ProductInfoQuery.class);
                    if (!CollectionUtils.isEmpty(productInfo)) {
                        productInfo.forEach(productItem -> {
                            Product product = productService.getById(productItem.getProductId());
                            if (Objects.nonNull(product)) {
                                productItem.setProductName(product.getName());
                            }
                        });
                    }
                    item.setProductInfoList(productInfo);
                }

                if (Objects.nonNull(item.getWorkOrderReasonId())) {
                    item.setWorkOrderReasonName(
                        this.getWorkOrderReasonStr(item.getWorkOrderReasonId(), ""));
                }

                List<WorkOrderServerQuery> workOrderServerList = workOrderServerService
                    .queryByWorkOrderIdAndServerId(item.getId(), serverId);
                item.setWorkOrderServerList(workOrderServerList);

                //服务商物件信息
                Optional.ofNullable(workOrderServerList).orElse(new ArrayList<>()).forEach(e -> {
                    List<WorkOrderParts> workOrderParts = workOrderPartsService
                        .queryByWorkOrderIdAndServerId(e.getWorkOrderId(), e.getServerId(), WorkOrderParts.TYPE_SERVER_PARTS);
                    e.setWorkOrderParts(workOrderParts);
                });

                //第三方物件信息
                Optional.ofNullable(workOrderServerList).orElse(new ArrayList<>()).forEach(e -> {
                    List<WorkOrderParts> workOrderParts = workOrderPartsService
                        .queryByWorkOrderIdAndServerId(e.getWorkOrderId(), e.getServerId(), WorkOrderParts.TYPE_THIRD_PARTS);
                    e.setThirdWorkOrderParts(workOrderParts);
                });

                //凭证
                LambdaQueryWrapper<File> eq = new LambdaQueryWrapper<File>()
                    .eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(File::getFileType, 0)
                    .eq(File::getBindId, item.getId());
                List<File> fileList = fileService.getBaseMapper().selectList(eq);
                item.setVoucherImgFile(fileList);

                //视频
                eq.clear();
                eq.eq(File::getType, File.TYPE_WORK_ORDER)
                    .eq(File::getFileType, 90000)
                    .eq(File::getBindId, item.getId());
                File files = fileService.getBaseMapper().selectOne(eq);
                item.setVoucherVideoFile(files);
            }

            //这里小程序显示，已处理提交审核的工单也会显示在已处理工单中，导致不知道提交到哪，所以排序
            //未通过 -->  未审核 -->  待审核 --> 未审核（一般不会出现)
            if(Objects.equals(status, WorkOrder.STATUS_PROCESSING)) {
                Map<Integer, List<WorkOrderAssignmentVo>> collect = data.parallelStream().filter(e ->Objects.nonNull(e.getAuditStatus())).collect(Collectors.groupingBy(WorkOrderAssignmentVo::getAuditStatus));
                List<WorkOrderAssignmentVo> result = new ArrayList<>();
                result.addAll(Optional.ofNullable(collect.get(2)).orElse(new ArrayList<>()).stream().sorted(Comparator.comparing(WorkOrderAssignmentVo::getCreateTime).reversed()).collect(Collectors.toList()));
                result.addAll(Optional.ofNullable(collect.get(0)).orElse(new ArrayList<>()).stream().sorted(Comparator.comparing(WorkOrderAssignmentVo::getCreateTime).reversed()).collect(Collectors.toList()));
                result.addAll(Optional.ofNullable(collect.get(1)).orElse(new ArrayList<>()).stream().sorted(Comparator.comparing(WorkOrderAssignmentVo::getCreateTime)).collect(Collectors.toList()));
                result.addAll(Optional.ofNullable(collect.get(3)).orElse(new ArrayList<>()).stream().sorted(Comparator.comparing(WorkOrderAssignmentVo::getCreateTime)).collect(Collectors.toList()));
                data = result;
            }
        }



        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", page.getTotal());
        return R.ok(result);
    }

    @Override
    public R updateAssignment(WorkOrderAssignmentQuery workOrderAssignmentQuery) {
        WorkOrder workOrderOld = this.getById(workOrderAssignmentQuery.getId());
        List<WorkOrderServerQuery> workOrderServerList = workOrderAssignmentQuery
            .getWorkOrderServerList();
        List<WorkOrderServerQuery> workOrderServerQuerys = workOrderServerService
            .queryByWorkOrderIdAndServerId(workOrderOld.getId(), null);
        if (Objects.isNull(workOrderOld)) {
            return R.fail("未查询到工单相关信息");
        }

        if (Objects.equals(workOrderOld.getAuditStatus(), WorkOrder.AUDIT_STATUS_PASSED)) {
            return R.fail("审核通过的工单不允许修改");
        }

        if (Objects.equals(workOrderAssignmentQuery.getStatus(), WorkOrder.STATUS_SUSPEND)
            && (!Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_SUSPEND)
            && !Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_INIT))) {
            return R.fail("工单已暂停只能修改为待处理");
        }

        if (Objects.equals(workOrderAssignmentQuery.getStatus(), WorkOrder.STATUS_SUSPEND)) {
            if (!Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_INIT)
                && !Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_SUSPEND)) {

                return R.fail("非待处理的工单不可修改为已暂停");
            }
        }

        /*if (Objects.equals(workOrderAssignmentQuery.getStatus(), WorkOrder.STATUS_FINISHED)) {
            if (!checkServerProcess(workOrderOld.getId())) {
                return R.fail("有服务商没有处理，请等待服务商处理或后台添加处理时间");
            }
        }*/

        if (CollectionUtils.isEmpty(workOrderServerList) && CollectionUtils
            .isEmpty(workOrderServerQuerys)) {
            return R.fail("工单必须有服务商");
        }

        for (WorkOrderServerQuery item : workOrderServerList) {
            if (Objects.isNull(item.getServerId())) {
                return R.fail("工单必须添加服务商");
            }

            if (Objects.isNull(item.getPaymentMethod())) {
                return R.fail("工单必须添加结算方式");
            }

            if (Objects.isNull(item.getFee())) {
                return R.fail("工单必须添加工单费用");
            }

            if (item.getIsUseThird()) {
                if (Objects.isNull(item.getThirdCompanyType())) {
                    return R.fail("工单必须添加第三方公司类型");
                }

                if (Objects.isNull(item.getThirdCompanyId())) {
                    return R.fail("工单必须添加第三方公司");
                }

                if (Objects.isNull(item.getArtificialFee())) {
                    return R.fail("工单必须添加应收第三方人工费");
                }

                if (Objects.isNull(item.getMaterialFee())) {
                    return R.fail("工单必须添加应收第三方物料费");
                }

                if (Objects.isNull(item.getThirdPaymentStatus())) {
                    return R.fail("工单必须添加第三方支付状态");
                }

                if (Objects.isNull(item.getThirdReason())) {
                    return R.fail("工单必须添加第三方原因");
                }

                if (Objects.isNull(item.getThirdResponsiblePerson())) {
                    return R.fail("工单必须添加第三方责任人");
                }

                if (!checkAndclearEntry(item.getThirdWorkOrderParts())) {
                    return R.fail("请添加相关物件信息");
                }
            }
        }

        for (WorkOrderServerQuery item : workOrderServerList) {
            if (Objects.equals(item.getHasParts(), WorkOrderServer.HAS_PARTS)) {
                if (!checkAndclearEntry(item.getWorkOrderParts())) {
                    return R.fail("请添加相关物料");
                }
            }
        }

        /*if(workOrderAssignmentQuery.getStatus() >= WorkOrder.STATUS_PROCESSING && workOrderAssignmentQuery.getStatus() <= WorkOrder.STATUS_FINISHED){
            for(WorkOrderServerQuery item : workOrderServerList){
                if(StringUtils.isBlank(item.getSolution())){
                    return R.fail("服务商"+item.getServerName()+"没填写解决方案");
                }

                QueryWrapper<File> wrapper = new QueryWrapper<>();
                wrapper.eq("type", File.TYPE_WORK_ORDER);
                wrapper.eq("bind_id", item.getWorkOrderId());
                wrapper.ge("file_type", 1).lt("file_type", 90000);
                wrapper.eq("server_id", item.getServerId());

                int fileCount = fileService.count(wrapper);
                if (fileCount == 0) {
                    return R.fail("服务商"+item.getServerName()+"没上传处理图片");
                }
            }
        }*/

        //非派单，不可修改服务商数量
        if (!Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            int workOrderServerOldCount = 0;
            int workOrderServerCount = 0;
            if (!CollectionUtils.isEmpty(workOrderServerQuerys)) {
                workOrderServerOldCount = workOrderServerQuerys.size();
            }
            if (!CollectionUtils.isEmpty(workOrderServerQuerys)) {
                workOrderServerCount = workOrderServerList.size();
            }
            if (!Objects.equals(workOrderServerOldCount, workOrderServerCount)) {
                return R.fail("非待派发模式不可添加或删除服务商");
            }
            List workOrderServerOldIds = workOrderServerQuerys.stream().map(item -> item.getId())
                .collect(Collectors.toList());
            List workOrderServerIds = workOrderServerList.stream().map(item -> item.getId())
                .collect(Collectors.toList());
            if (!workOrderServerOldIds.equals(workOrderServerIds)) {
                return R.fail("非待派发模式不可修改服务商");
            }
        }

        WorkOrder workOrder = new WorkOrder();
        BeanUtils.copyProperties(workOrderAssignmentQuery, workOrder);
        String productInfo = JSON.toJSONString(workOrderAssignmentQuery.getProductInfoList());
        workOrder.setProductInfo(productInfo);
        workOrder.setType(workOrderOld.getType());

        if (Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            workOrder.setStatus(WorkOrder.STATUS_INIT);
            if (Objects.isNull(workOrderOld.getAssignmentTime())) {
                workOrder.setAssignmentTime(System.currentTimeMillis());
            }
        }

        if (Objects.equals(workOrderAssignmentQuery.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            createWorkOrderServer(workOrderOld, workOrderAssignmentQuery);
        } else {
            //除了待派单 不允许修改除处理方案以外的信息
            if (!CollectionUtils.isEmpty(workOrderServerList)) {
                for (WorkOrderServerQuery item : workOrderServerList) {
                    WorkOrderServer old = workOrderServerService.getById(item.getId());
                    if (Objects.nonNull(old)) {
                        WorkOrderServer workOrderServer = new WorkOrderServer();
                        workOrderServer.setId(old.getId());
                        workOrderServer.setSolution(item.getSolution());
                        workOrderServer.setHasParts(item.getHasParts());
                        clareAndAddWorkOrderParts(workOrder.getId(), old.getServerId(), item.getWorkOrderParts(), WorkOrderParts.TYPE_SERVER_PARTS);
                        BigDecimal totalMaterialFee = clareAndAddWorkOrderParts(workOrder.getId(), old.getServerId(), item.getThirdWorkOrderParts(), WorkOrderParts.TYPE_THIRD_PARTS);

                        workOrderServer.setMaterialFee(totalMaterialFee);
                        workOrderServerService.updateById(workOrderServer);
                    }
                }
            }
        }
        this.updateById(workOrder);

        //发送Mq通知
        if (Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_ASSIGNMENT)) {
            workOrder.setStatus(WorkOrder.STATUS_INIT);
            if (Objects.isNull(workOrderOld.getAssignmentTime())) {
                this.sendWorkServerNotifyMq(workOrder);
            }
        }

        return R.ok();
    }

    @Override
    public R updateServer(String solution, Long workOrderId) {
        Long uid = SecurityUtils.getUid();

        if (Objects.isNull(uid)) {
            return R.fail("未查询到相关用户");
        }

        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return R.fail("未查询到相关用户");
        }

        WorkOrder workOrderOld = this.getById(workOrderId);
        if (Objects.isNull(workOrderOld)) {
            return R.fail("未查询到工单相关信息");
        }

        if (Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_SUSPEND)) {
            return R.fail("工单已暂停，不可上传");
        }

        List<WorkOrderServerQuery> workOrderServer = workOrderServerService
            .queryByWorkOrderIdAndServerId(workOrderId, user.getThirdId());
        if (CollectionUtils.isEmpty(workOrderServer) || workOrderServer.size() > 1) {
            return R.fail("未查询出或查询出多条服务商工单信息");
        }

        if (Objects.nonNull(workOrderServer.get(0).getSolutionTime())) {
            return R.fail("服务商工单已提交，不可修改");
        }

        if (StringUtils.isBlank(solution)) {
            return R.fail("请填写解决方案");
        }

        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("type", File.TYPE_WORK_ORDER);
        wrapper.eq("bind_id", workOrderId);
        wrapper.ge("file_type", 1)
            .lt("file_type", 90000)
            .eq("server_id", user.getThirdId());

        List<File> list = fileService.getBaseMapper().selectList(wrapper);

        if (CollectionUtils.isEmpty(list)) {
            return R.fail("请上传处理图片");
        }

        WorkOrderServer updateWorkOrderServer = new WorkOrderServer();
        updateWorkOrderServer.setId(workOrderServer.get(0).getId());
        updateWorkOrderServer.setSolution(solution);
        updateWorkOrderServer.setSolutionTime(System.currentTimeMillis());
        updateWorkOrderServer.setPrescription(
            updateWorkOrderServer.getSolutionTime() - workOrderOld.getAssignmentTime());
        workOrderServerService.updateById(updateWorkOrderServer);

        //全部完成修改工单为已处理
        if (checkServerProcess(workOrderId)) {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setId(workOrderOld.getId());
            workOrder.setStatus(WorkOrder.STATUS_PROCESSING);
            workOrder.setProcessTime(System.currentTimeMillis());
            this.updateById(workOrder);
        }

        return R.ok();
    }

    @Override
    public R submitReview(Long id) {

        WorkOrder workOrder = this.getById(id);
        if (Objects.isNull(workOrder)) {
            return R.fail("未查询到工单相关信息");
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_FINISHED)) {
            return R.fail("工单已完结");
        }

        if (!Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_PROCESSING)) {
            return R.fail("工单还未处理，请处理后提交审核");
        }

        if (Objects.equals(workOrder.getStatus(), WorkOrder.STATUS_SUSPEND)) {
            return R.fail("工单已暂停");
        }

        WorkOrder update = new WorkOrder();
        update.setId(id);
        update.setAuditStatus(WorkOrder.AUDIT_STATUS_WAIT);
        this.updateById(update);

        this.saveWorkAuditNotify(workOrder);
        this.sendWorkAuditNotifyMq(workOrder);
        return R.ok();
    }

    private void sendWorkAuditNotifyMq(WorkOrder workOrder) {
        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = maintenanceUserNotifyConfigService
            .queryByPermissions(MaintenanceUserNotifyConfig.TYPE_REVIEW, null);
        if (Objects.isNull(maintenanceUserNotifyConfig) || org.springframework.util.StringUtils
            .isEmpty(maintenanceUserNotifyConfig.getPhones())) {
            return;
        }
        List<String> phones = JsonUtil
            .fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class);

        if (CollectionUtils.isEmpty(phones)) {
            return;
        }

        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (Objects.equals(
            maintenanceUserNotifyConfig.getPermissions() & MaintenanceUserNotifyConfig.P_REVIEW,
            MaintenanceUserNotifyConfig.P_REVIEW)) {
            phones.forEach(p -> {
                MqNotifyCommon<MqWorkOrderAuditNotify> query = new MqNotifyCommon<>();
                Long time = System.currentTimeMillis();
                query.setType(MqNotifyCommon.TYPE_AFTER_SALES_AUDIT);
                query.setTime(time);
                query.setPhone(p);

                MqWorkOrderAuditNotify mqWorkOrderAuditNotify = new MqWorkOrderAuditNotify();
                mqWorkOrderAuditNotify.setWorkOrderNo(workOrder.getOrderNo());
                mqWorkOrderAuditNotify.setSubmitTime(simp.format(new Date(time)));

                WorkOrderType workOrderType = workOrderTypeService.getById(workOrder.getType());
                if (Objects.nonNull(workOrderType)) {
                    mqWorkOrderAuditNotify.setOrderTypeName(workOrderType.getType());
                }

                PointNew pointNew = pointNewService.getById(workOrder.getPointId());
                if (Objects.nonNull(pointNew)) {
                    mqWorkOrderAuditNotify.setPointName(pointNew.getName());
                }

                User user = userService.getUserById(workOrder.getCommissionerId());
                if (Objects.nonNull(user)) {
                    mqWorkOrderAuditNotify.setSubmitUName(user.getUserName());
                }
                query.setData(mqWorkOrderAuditNotify);

                Pair<Boolean, String> result = rocketMqService
                    .sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil.toJson(query),
                        MqConstant.TAG_AFTER_SALES, "", 0);
                if (!result.getLeft()) {
                    log.error("SEND WORKORDER AUDIT MQ ERROR! no={}", workOrder.getOrderNo());
                }
            });
        }

    }

    private void sendWorkServerNotifyMq(WorkOrder workOrder) {
        List<WorkOrderServerQuery> workOrderServerQueries = workOrderServerService
            .queryByWorkOrderId(workOrder.getId());
        if (CollectionUtils.isEmpty(workOrderServerQueries)) {
            return;
        }

        Map<String, String> data = new HashMap<>(2);
        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        WorkOrderType workOrderTypeByDB = workOrderTypeService.getById(workOrder.getType());
        if (Objects.nonNull(workOrderTypeByDB)) {
            data.put("workOrderType", workOrderTypeByDB.getType());
        }

        PointNew pointNewByDB = pointNewService.getById(workOrder.getPointId());
        if (Objects.nonNull(pointNewByDB)) {
            data.put("pointNew", pointNewByDB.getName());
        }

        workOrderServerQueries.parallelStream().forEach(s -> {
            if (Objects.isNull(s.getServerId())) {
                return;
            }

            MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = maintenanceUserNotifyConfigService
                .queryByPermissions(MaintenanceUserNotifyConfig.TYPE_SERVER, s.getServerId());
            if (Objects.isNull(maintenanceUserNotifyConfig)) {
                return;
            }

            List<String> serverPhones = JsonUtil
                .fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class);

            if (CollectionUtils.isEmpty(serverPhones)) {
                return;
            }

            if (Objects.equals(
                maintenanceUserNotifyConfig.getPermissions() & MaintenanceUserNotifyConfig.P_SERVER,
                MaintenanceUserNotifyConfig.P_SERVER)) {
                serverPhones.parallelStream().forEach(p -> {
                    MqNotifyCommon<MqWorkOrderServerNotify> query = new MqNotifyCommon<>();
                    query.setType(MqNotifyCommon.TYPE_AFTER_SALES_SERVER);
                    query.setTime(System.currentTimeMillis());
                    query.setPhone(p);

                    MqWorkOrderServerNotify mqWorkOrderServerNotify = new MqWorkOrderServerNotify();
                    mqWorkOrderServerNotify.setWorkOrderNo(workOrder.getOrderNo());
                    mqWorkOrderServerNotify.setOrderTypeName(data.get("workOrderType"));
                    mqWorkOrderServerNotify.setPointName(data.get("pointNew"));
                    mqWorkOrderServerNotify
                        .setAssignmentTime(simp.format(new Date(workOrder.getAssignmentTime())));
                    query.setData(mqWorkOrderServerNotify);

                    Pair<Boolean, String> result = rocketMqService
                        .sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil.toJson(query),
                            MqConstant.TAG_AFTER_SALES, "", 0);
                    if (!result.getLeft()) {
                        log.error("SEND WORKORDER SERVER MQ ERROR! no={}", workOrder.getOrderNo());
                    }
                });
            }

        });
    }

    private WorkAuditNotify saveWorkAuditNotify(WorkOrder workOrder) {
        WorkAuditNotify workAuditNotify = new WorkAuditNotify();
        workAuditNotify.setOrderNo(workOrder.getOrderNo());
        workAuditNotify.setOrderType(workOrder.getType());
        workAuditNotify.setWorkOrderReasonId(workOrder.getWorkOrderReasonId());
        workAuditNotify.setSubmitUid(SecurityUtils.getUid());
        workAuditNotify.setType(WorkAuditNotify.TYPE_UNFINISHED);
        workAuditNotify.setCreateTime(System.currentTimeMillis());
        workAuditNotify.setUpdateTime(System.currentTimeMillis());
        workAuditNotifyService.save(workAuditNotify);
        return workAuditNotify;
    }

    private void updateWorkOrderServer(List<WorkOrderServerQuery> workOrderServerList) {
        if (!CollectionUtils.isEmpty(workOrderServerList)) {
            for (WorkOrderServerQuery item : workOrderServerList) {
                WorkOrderServer old = workOrderServerService.getById(item.getId());
                if (Objects.nonNull(old)) {
                    WorkOrderServer workOrderServer = new WorkOrderServer();
                    BeanUtils.copyProperties(item, workOrderServer);
                    clareAndAddWorkOrderParts(old.getWorkOrderId(), old.getServerId(), item.getWorkOrderParts(), WorkOrderParts.TYPE_SERVER_PARTS);
                    BigDecimal totalAmount = clareAndAddWorkOrderParts(old.getWorkOrderId(), old.getServerId(), item.getThirdWorkOrderParts(), WorkOrderParts.TYPE_THIRD_PARTS);
                    workOrderServer.setMaterialFee(totalAmount);
                    workOrderServerService.updateById(workOrderServer);
                }
            }
        }
    }

    private void createWorkOrderServer(WorkOrder workOrder,
        WorkOrderAssignmentQuery workOrderAssignmentQuery) {
        //服务商第三方信息
        Boolean boo = false;
        workOrderServerService.removeByWorkOrderId(workOrder.getId());
        if (!CollectionUtils.isEmpty(workOrderAssignmentQuery.getWorkOrderServerList())) {
            workOrderAssignmentQuery.getWorkOrderServerList().forEach(item -> {
                WorkOrderServer workOrderServer = new WorkOrderServer();
                workOrderServer.setWorkOrderId(workOrder.getId());
                workOrderServer.setServerId(item.getServerId());
                //服务商名称
                if (item.getServerId() != null) {
                    Server server = serverService.getById(item.getServerId());
                    if (Objects.nonNull(server)) {
                        workOrderServer.setServerName(server.getName());
                    }
                }
                workOrderServer.setFee(item.getFee());
                workOrderServer.setPaymentMethod(item.getPaymentMethod());
                workOrderServer.setSolution(item.getSolution());
                workOrderServer.setSolutionTime(item.getSolutionTime());
                workOrderServer.setPrescription(item.getPrescription());
                workOrderServer.setIsUseThird(item.getIsUseThird());
                workOrderServer.setThirdCompanyType(item.getThirdCompanyType());
                workOrderServer.setThirdCompanyId(item.getThirdCompanyId());
                //第三方公司名称
                if (item.getThirdCompanyId() != null && item.getThirdCompanyType() != null) {
                    if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_CUSTOMER)) {
                        Customer customer = customerService.getById(item.getThirdCompanyId());
                        if (Objects.nonNull(customer)) {
                            workOrderServer.setThirdCompanyName(customer.getName());
                        }
                    }

                    if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SUPPLIER)) {
                        Supplier supplier = supplierService.getById(item.getThirdCompanyId());
                        if (Objects.nonNull(supplier)) {
                            workOrderServer.setThirdCompanyName(supplier.getName());
                        }
                    }

                    if (item.getThirdCompanyType().equals(WorkOrder.COMPANY_TYPE_SERVER)) {
                        Server server = serverService.getById(item.getThirdCompanyId());
                        if (Objects.nonNull(server)) {
                            workOrderServer.setThirdCompanyName(server.getName());
                        }
                    }
                }

                workOrderServer.setArtificialFee(item.getArtificialFee());
                workOrderServer.setMaterialFee(item.getMaterialFee());
                workOrderServer.setThirdPaymentStatus(item.getThirdPaymentStatus());
                workOrderServer.setThirdReason(item.getThirdReason());
                workOrderServer.setThirdResponsiblePerson(item.getThirdResponsiblePerson());
                workOrderServer.setHasParts(item.getHasParts());
                workOrderServer.setCreateTime(System.currentTimeMillis());

                workOrderServerService.save(workOrderServer);

                clareAndAddWorkOrderParts(workOrder.getId(), workOrderServer.getServerId(), item.getWorkOrderParts(), WorkOrderParts.TYPE_SERVER_PARTS);
                BigDecimal totalMaterialFee = clareAndAddWorkOrderParts(workOrder.getId(), workOrderServer.getServerId(), item.getThirdWorkOrderParts(), WorkOrderParts.TYPE_THIRD_PARTS);

                workOrderServer.setMaterialFee(totalMaterialFee);
                workOrderServerService.updateById(workOrderServer);
            });

        }
    }

    private R checkProperties(WorkOrderQuery workOrderQuery) {
        if (StringUtils.isBlank(workOrderQuery.getType())) {
            return R.fail("请填写工单类型");
        } else {
            WorkOrderType workOrderType = workOrderTypeService.getById(workOrderQuery.getType());
            if (Objects.isNull(workOrderType)) {
                return R.fail("请填写正确的工单类型");
            }
        }

        if (Objects.isNull(workOrderQuery.getPointId())) {
            return R.fail("请填写点位");
        } else {
            PointNew point = pointNewService.getById(workOrderQuery.getPointId());
            if (Objects.isNull(point)) {
                return R.fail("未查询到相关点位");
            }

        }

        if (Objects.isNull(workOrderQuery.getCreaterId())) {
            return R.fail("创建人为空");
        } else {
            User user = userService.getUserById(workOrderQuery.getCreaterId());
            if (Objects.isNull(user)) {
                return R.fail("未查询到相关创建人");
            }
        }

        if (Objects.isNull(workOrderQuery.getStatus())) {
            return R.fail("请填写工单状态");
        } else {
            String status = this.getStatusStr(workOrderQuery.getStatus());
            if (Objects.equals("", status)) {
                return R.fail("请填写正确的工单状态");
            }
        }

        if (Objects.isNull(workOrderQuery.getCommissionerId())) {
            return R.fail("请填写专员");
        } else {
            User user = userService.getUserById(workOrderQuery.getCommissionerId());
            if (Objects.isNull(user) || !Objects
                .equals(user.getUserType(), User.TYPE_COMMISSIONER)) {
                return R.fail("未查询到相关专员");
            }
        }

        if (Objects.isNull(workOrderQuery.getCreateTime())) {
            return R.fail("请填写创建时间");
        }
        if (Objects.equals(workOrderQuery.getType(), String.valueOf(WorkOrder.TYPE_MOBLIE))) {
            if (Objects.isNull(workOrderQuery.getSourceType())) {
                return R.fail("请填写起点类型");
            }
            if (Objects.isNull(workOrderQuery.getTransferSourcePointId())) {
                return R.fail("请填写起点");
            } else {
                if (Objects.equals(workOrderQuery.getSourceType(), 1)) {
                    PointNew pointNew = pointNewService
                        .queryByIdFromDB(workOrderQuery.getTransferSourcePointId());
                    if (Objects.isNull(pointNew)) {
                        return R.fail("未查询到相关起点");
                    }
                }

                if (Objects.equals(workOrderQuery.getSourceType(), 2)) {
                    WareHouse wareHouse = warehouseService
                        .getById(workOrderQuery.getTransferSourcePointId());
                    if (Objects.isNull(wareHouse)) {
                        return R.fail("未查询到相关起点");
                    }
                }
            }
            if (Objects.isNull(workOrderQuery.getDestinationType())) {
                return R.fail("请填写终点类型");
            }
            if (Objects.isNull(workOrderQuery.getTransferDestinationPointId())) {
                return R.fail("请填写终点");
            } else {
                if (Objects.equals(workOrderQuery.getDestinationType(), 1)) {
                    PointNew pointNew = pointNewService
                        .queryByIdFromDB(workOrderQuery.getTransferDestinationPointId());
                    if (Objects.isNull(pointNew)) {
                        return R.fail("未查询到相关终点");
                    }
                }

                if (Objects.equals(workOrderQuery.getDestinationType(), 2)) {
                    WareHouse wareHouse = warehouseService
                        .getById(workOrderQuery.getTransferDestinationPointId());
                    if (Objects.isNull(wareHouse)) {
                        return R.fail("未查询到相关终点");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getParentWorkOrderReason(Long id) {
        Long workOrderReasonId = id;
        for (int i = 0; i < 10; i++) {
            WorkOrderReason workOrderReason = workOrderReasonService.getById(workOrderReasonId);
            if (Objects.nonNull(workOrderReason)) {
                workOrderReasonId = workOrderReason.getParentId();
                if (Objects.equals(workOrderReason.getParentId(), -1L)) {
                    return workOrderReason.getName();
                }
            }
        }
        return null;
    }

    @Override
    public R queryProductModelPull(String name) {
        return productService.queryProductModelPull(name);
    }

    @Override
    public R queryWorkOrderReasonPull() {
        return R.ok(workOrderReasonService.list());
    }

    @Override
    public R queryServerPull(String name) {
        return serverService.queryServerPull(name);
    }

    @Override
    public R querySupplierPull(String name) {
        return R.ok(supplierService.querySupplierPull(name));
    }

    @Override
    public R queryCustomerPull(String name) {
        return R.ok(customerService.queryCustomerPull(name));
    }

    @Override
    public R queryPointNewPull(String name) {
        return R.ok(pointNewService.queryPointNewPull(name));
    }

    @Override
    public R queryWarehousePull(String name) {
        return R.ok(warehouseService.queryWarehouseLikeNamePull(name));
    }

    @Override
    public R workOrderServerAuditEntry(Long id) {
        List<WorkOrderServerQuery> workOrderServerQueries = workOrderServerService
            .queryByWorkOrderId(id);
        if (CollectionUtils.isEmpty(workOrderServerQueries)) {
            return R.fail("未查询到工单服务商");
        }

        List<WorkOrderServerAuditEntryVo> data = new ArrayList<>();

        workOrderServerQueries.forEach(item -> {
            Server server = serverService.getById(item.getServerId());
            if (Objects.isNull(server)) {
                return;
            }
            List<WeChatServerAuditEntryVo> weChatServerAuditEntryVoList = serverAuditEntryService
                .getWeChatServerAuditEntryVoList(item.getWorkOrderId(), item.getServerId());

            WorkOrderServerAuditEntryVo vo = new WorkOrderServerAuditEntryVo();
            vo.setId(server.getId());
            vo.setName(server.getName());
            vo.setWeChatServerAuditEntryVos(weChatServerAuditEntryVoList);
            data.add(vo);
        });
        return R.ok(data);
    }
}
