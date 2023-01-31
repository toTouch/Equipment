package com.xiliulou.afterserver.controller.admin;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.export.PointUpdateInfo;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.listener.PointUpdateListener;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;

import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.vo.PointExcelVo;
import com.xiliulou.afterserver.web.query.CameraInfoQuery;
import com.xiliulou.afterserver.web.query.PointAuditStatusQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import com.xiliulou.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hardy
 * @date 2021/8/17 10:33
 * @mood 点位模块新改
 */
@RestController
@Slf4j
public class AdminJsonPointNewController {

    @Autowired
    private PointNewService pointNewService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CityService cityService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PointProductBindMapper pointProductBindMapper;
    @Autowired
    private ProductNewMapper productNewMapper;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private PointProductBindService pointProductBindService;
    @Autowired
    private FileService fileService;


    @PostMapping("/admin/pointNew")
    public R saveAdminPointNew(@RequestBody PointNew pointNew, HttpServletRequest request) {
        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return R.fail("用户为空");
        }
        pointNew.setCreateUid(uid);
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
        return pointNewService.saveAdminPointNew(pointNew);
    }

    @PutMapping("/admin/pointNew")
    public R putAdminPointNew(@RequestBody PointNew pointNew) {
        return pointNewService.putAdminPoint(pointNew);
    }

    @DeleteMapping("/admin/pointNew/{id}")
    public R delAdminPointNew(@PathVariable("id") Long id) {
        return pointNewService.delAdminPointNew(id);
    }

    @GetMapping("/admin/pointNew/list")
    public R pointList(@RequestParam("offset") Integer offset,
        @RequestParam("limit") Integer limit,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cid", required = false) Integer cid,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "customerId", required = false) Long customerId,
        @RequestParam(value = "startTime", required = false) Long startTime,
        @RequestParam(value = "endTime", required = false) Long endTime,
        @RequestParam(value = "createUid", required = false) Long createUid,
        @RequestParam(value = "snNo", required = false) String snNo,
        @RequestParam(value = "productSeries", required = false) Integer productSeries,
        @RequestParam(value = "auditStatus", required = false) Integer auditStatus) {
        pointNewService.updatePastWarrantyStatus();
        List<PointNew> pointNews = pointNewService
            .queryAllByLimit(offset, limit, name, cid, status, customerId, startTime, endTime,
                createUid, snNo, productSeries, auditStatus);

        if (Objects.nonNull(pointNews)) {
            pointNews.forEach(item -> {
                if (Objects.nonNull(item.getCityId())) {
                    City byId = cityService.getById(item.getCityId());
                    item.setCityName(byId.getName());
                    item.setProvince(byId.getPid().toString());
                }

                if (Objects.nonNull(item.getCustomerId())) {
                    Customer byId = customerService.getById(item.getCustomerId());
                    if (Objects.nonNull(byId)) {
                        item.setCustomerName(byId.getName());
                    }
                }

                if (Objects.nonNull(item.getCreateUid())) {
                    User userById = userService.getUserById(item.getCreateUid());
                    if (Objects.nonNull(userById)) {
                        item.setUserName(userById.getUserName());
                    }
                }

                if (Objects.nonNull(item.getProductInfo())) {
                    List<ProductInfoQuery> productInfo = JSON
                        .parseArray(item.getProductInfo(), ProductInfoQuery.class);
                    item.setProductInfoList(productInfo);
                }

                if (Objects.nonNull(item.getCameraInfo())) {
                    List<CameraInfoQuery> cameraInfo = JSON
                        .parseArray(item.getCameraInfo(), CameraInfoQuery.class);
                    item.setCameraInfoList(cameraInfo);
                }

                //是否录入资产编码
                List<PointProductBind> pointProductBinds = pointProductBindService
                    .queryByPointNewIdAndBindType(item.getId(), PointProductBind.TYPE_POINT);
                if (CollectionUtil.isEmpty(pointProductBinds)) {
                    item.setIsbindProduct(PointNew.UNBIND_PRODUCT);
                    item.setBindProductCount(0);
                } else {
                    item.setIsbindProduct(PointNew.BIND_PRODUCT);
                    item.setBindProductCount(pointProductBinds.size());
                }

                //文件个数
                BaseMapper<File> fileMapper = fileService.getBaseMapper();
                LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<File> eq = fileLambdaQueryWrapper
                    .eq(File::getBindId, item.getId()).eq(File::getType, File.TYPE_POINTNEW);
                List<File> files = fileMapper.selectList(eq);
                if (CollectionUtil.isEmpty(files)) {
                    item.setFileCount(0);
                } else {
                    item.setFileCount(files.size());
                }
            });
        }

        Integer count = pointNewService
            .countPoint(name, cid, status, customerId, startTime, endTime, createUid, snNo,
                productSeries, auditStatus);

        HashMap<String, Object> map = new HashMap<>();
        map.put("data", pointNews);
        map.put("count", count);
        return R.ok(map);
    }


    /**
     * 导入
     */
    @PostMapping("admin/pointNew/upload")
    public R upload(MultipartFile file, HttpServletRequest request) {

        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), PointInfo.class,
                new PointListener(pointNewService, customerService, cityService, request,
                    supplierService)).build();
        } catch (Exception e) {
            log.error("inprot pointNew ");
            if (e.getCause() instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) e
                    .getCause();
                String cellMsg = "";
                CellData cellData = excelDataConvertException.getCellData();
                //这里有一个celldatatype的枚举值,用来判断CellData的数据类型
                CellDataTypeEnum type = cellData.getType();
                if (type.equals(CellDataTypeEnum.NUMBER)) {
                    cellMsg = cellData.getNumberValue().toString();
                } else if (type.equals(CellDataTypeEnum.STRING)) {
                    cellMsg = cellData.getStringValue();
                } else if (type.equals(CellDataTypeEnum.BOOLEAN)) {
                    cellMsg = cellData.getBooleanValue().toString();
                }
                String errorMsg = String.format(
                    "excel表格:第%s行,第%s列,数据值为:%s,该数据值不符合要求,请检验后重新导入!<span style=\"color:red\">请检查其他的记录是否有同类型的错误!</span>",
                    excelDataConvertException.getRowIndex() + 1,
                    excelDataConvertException.getColumnIndex(), cellMsg);
                log.error(errorMsg);
            }
        }
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

    /**
     * 更新导入
     */
    @PostMapping("admin/pointNew/update/upload")
    public R updateUpload(MultipartFile file, HttpServletRequest request) {

        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), PointUpdateInfo.class,
                new PointUpdateListener(pointNewService, customerService, cityService, request,
                    userService, supplierService)).build();
        } catch (Exception e) {
            log.error("handle upload excel error!", e);
            if (e instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) e
                    .getCause();
                String cellMsg = "";
                CellData cellData = excelDataConvertException.getCellData();
                //这里有一个celldatatype的枚举值,用来判断CellData的数据类型
                CellDataTypeEnum type = cellData.getType();
                if (type.equals(CellDataTypeEnum.NUMBER)) {
                    cellMsg = cellData.getNumberValue().toString();
                } else if (type.equals(CellDataTypeEnum.STRING)) {
                    cellMsg = cellData.getStringValue();
                } else if (type.equals(CellDataTypeEnum.BOOLEAN)) {
                    cellMsg = cellData.getBooleanValue().toString();
                }
                String errorMsg = String.format(
                    "excel表格:第%s行,第%s列,数据值为:%s,该数据值不符合要求,请检验后重新导入!<span style=\"color:red\">请检查其他的记录是否有同类型的错误!</span>",
                    excelDataConvertException.getRowIndex() + 1,
                    excelDataConvertException.getColumnIndex(), cellMsg);
                log.error("handle upload excel error! msg={}", errorMsg);
            }
        }
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

    @PutMapping("admin/pointNew/update/createUser")
    public R putAdminPointNewCreateUser(@RequestParam("id") Long id,
        @RequestParam("createUid") Long createUid) {
        return pointNewService.putAdminPointNewCreateUser(id, createUid);
    }

    @GetMapping("/admin/pointNew/exportExcel")
    public void pointExportExcel(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cid", required = false) Integer cid,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "customerId", required = false) Long customerId,
        @RequestParam(value = "startTime", required = false) Long startTime,
        @RequestParam(value = "endTime", required = false) Long endTime,
        @RequestParam(value = "createUid", required = false) Long createUid,
        @RequestParam(value = "snNo", required = false) String snNo,
        @RequestParam(value = "productSeries", required = false) Integer productSeries,
        @RequestParam(value = "auditStatus", required = false) Integer auditStatus,
        HttpServletResponse response) {
        pointNewService.updatePastWarrantyStatus();
        List<PointNew> pointNews = pointNewService
            .queryAllByLimitExcel(name, cid, status, customerId, startTime, endTime, createUid,
                snNo, productSeries, auditStatus);

        if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
            throw new NullPointerException("请选择开始时间和结束时间");
        }

        if (CollectionUtils.isEmpty(pointNews)) {
            throw new NullPointerException("查询内容为空，无法导出");
        }

        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"审核状态", "产品系列", "省份", "城市", "客户名称", "柜机名称", "点位状态", "创建人", "创建时间",
            "安装类型", "雨棚数量", "是否录入资产编码", "照片数量", "SN码", "物联网卡号", "物联网卡供应商", "详细地址", "安装时间", "质保有效期",
            "质保结束时间", "施工完成时间", "入账", "验收", "下单时间", "运营商", "物流信息", "审核人", "审核时间", "审核内容", "备注"};
        //List<Product> productAll = productService.list();
        Integer max = 0;

        for (PointNew pointNew : pointNews) {
            if (Objects.nonNull(pointNew.getCameraInfo()) && pointNew.getCameraInfo()
                .matches("\\[.*\\]")) {
                List<CameraInfoQuery> cameraInfoQuery = JSON
                    .parseArray(pointNew.getCameraInfo(), CameraInfoQuery.class);
                ;
                if (!CollectionUtils.isEmpty(cameraInfoQuery)) {
                    max = max < cameraInfoQuery.size() ? cameraInfoQuery.size() : max;
                }
            }
        }

        final Integer finalMax = max;
        for (String s : header) {
            List<String> headTitle = new ArrayList<>();
            headTitle.add(s);
            headList.add(headTitle);
        }
//        if(productAll != null && !productAll.isEmpty()){
//            for (Product p : productAll){
//                List<String> headTitle = new ArrayList<>();
//                headTitle.add(p.getName());
//                headList.add(headTitle);
//            }
//        }

        List<String> headTitle = new ArrayList<>();
        headTitle.add("摄像头数量");
        headList.add(headTitle);

        if (Objects.nonNull(finalMax)) {
            for (int i = 0; i < finalMax; i++) {
                List<String> headTitle1 = new ArrayList<>();
                headTitle1.add("摄像头卡供应商" + (1 + i));
                List<String> headTitle2 = new ArrayList<>();
                headTitle2.add("摄像头序列号" + (1 + i));
                List<String> headTitle3 = new ArrayList<>();
                headTitle3.add("摄像头卡号" + (1 + i));

                headList.add(headTitle1);
                headList.add(headTitle2);
                headList.add(headTitle3);
            }
        }

        List<String> modeTitle = new ArrayList<>();
        modeTitle.add("产品型号");
        headList.add(modeTitle);

        List<String> noTitle = new ArrayList<>();
        noTitle.add("资产编码");
        headList.add(noTitle);

        List<String> buyTitle = new ArrayList<>();
        buyTitle.add("是否集采");
        headList.add(buyTitle);

        table.setHead(headList);

        ArrayList<List<Object>> pointExcelVos = new ArrayList<>();

        pointNews.parallelStream().forEachOrdered(item -> {
            try {
                //for(PointNew item : pointNews) {
                //PointExcelVo pointExcelVo = new PointExcelVo();
                List<Object> list = new ArrayList<>();
                //审核状态
                if (Objects.equals(item.getAuditStatus(), 1)) {
                    list.add("待审核");
                } else if (Objects.equals(item.getAuditStatus(), 2)) {
                    list.add("未通过");
                } else if (Objects.equals(item.getAuditStatus(), 3)) {
                    list.add("已通过");
                } else {
                    list.add("");
                }

                //产品系列
                String productSeriesName = "";
                if ("1".equals(String.valueOf(item.getProductSeries()))) {
                    productSeriesName = "取餐柜";
                } else if ("2".equals(String.valueOf(item.getProductSeries()))) {
                    productSeriesName = "餐厅柜";
                } else if ("3".equals(String.valueOf(item.getProductSeries()))) {
                    productSeriesName = "换电柜";
                } else if ("4".equals(String.valueOf(item.getProductSeries()))) {
                    productSeriesName = "充电柜";
                } else if ("5".equals(String.valueOf(item.getProductSeries()))) {
                    productSeriesName = "寄存柜";
                } else if ("6".equals(String.valueOf(item.getProductSeries()))) {
                    productSeriesName = "生鲜柜";
                }
                list.add(productSeriesName);

                //城市名称
                if (Objects.nonNull(item.getCityId())) {
                    City byId = cityService.getById(item.getCityId());
                    if (Objects.nonNull(byId)) {
                        Province province = provinceService.queryByIdFromDB(byId.getPid());
                        if (Objects.nonNull(province)) {
                            list.add(province.getName());
                            list.add(byId.getName());
                        } else {
                            list.add("");
                            list.add(byId.getName());
                        }
                    } else {
                        list.add("");
                        list.add("");
                    }
                } else {
                    list.add("");
                    list.add("");
                }

                //客户名称
                if (Objects.nonNull(item.getCustomerId())) {
                    Customer byId = customerService.getById(item.getCustomerId());
                    if (Objects.nonNull(byId)) {
                        list.add(byId.getName());
                    } else {
                        list.add("");
                    }
                } else {
                    list.add("");
                }

                //柜机名称
                list.add(item.getName() == null ? "" : item.getName());

                //点位状态
                // 状态 1,移机,2运营中,3:拆机,4:初始化，5：待安装
                if (Objects.isNull(item.getStatus())) {
                    list.add("");
                } else if (Objects.equals(item.getStatus(), 1)) {
                    list.add("移机");
                } else if (Objects.equals(item.getStatus(), 2)) {
                    list.add("运营中");
                } else if (Objects.equals(item.getStatus(), 3)) {
                    list.add("已拆机");
                } else if (Objects.equals(item.getStatus(), 4)) {
                    list.add("初始化");
                } else if (Objects.equals(item.getStatus(), 5)) {
                    list.add("待安装");
                } else if (Objects.equals(item.getStatus(), 6)) {
                    list.add("运输中");
                } else if (Objects.equals(item.getStatus(), 7)) {
                    list.add("安装中");
                } else if (Objects.equals(item.getStatus(), 8)) {
                    list.add("安装完成");
                } else if (Objects.equals(item.getStatus(), 9)) {
                    list.add("已暂停");
                } else if (Objects.equals(item.getStatus(), 10)) {
                    list.add("已取消");
                } else if (Objects.equals(item.getStatus(), 11)) {
                    list.add("已过保");
                }

                //创建人
                if (Objects.nonNull(item.getCreateUid())) {
                    User user = userService.getUserById(item.getCreateUid());
                    if (Objects.nonNull(user)) {
                        list.add(user.getUserName());
                    } else {
                        list.add("");
                    }
                } else {
                    list.add("");
                }

                //创建时间
                if (item.getCreateTime() != null) {
                    list.add(DateUtils.stampToDate(item.getCreateTime().toString()));
                } else {
                    list.add("");
                }

                // 安装类型 1:室外 2:半室外3：室内
                if (Objects.isNull(item.getInstallType())) {
                    list.add("");
                } else if (Objects.equals(item.getInstallType(), 1)) {
                    list.add("室外");
                } else if (Objects.equals(item.getInstallType(), 2)) {
                    list.add("半室外");
                } else if (Objects.equals(item.getInstallType(), 3)) {
                    list.add("室内");
                }

                //雨棚数量
                list.add(item.getCanopyCount() == null ? "" : item.getCanopyCount());

                //是否录入资产编码
                List<PointProductBind> pointProductBinds = pointProductBindService
                    .queryByPointNewIdAndBindType(item.getId(), PointProductBind.TYPE_POINT);
                if (CollectionUtil.isEmpty(pointProductBinds)) {
                    list.add("否");
                } else {
                    list.add("是");
                }

                //文件个数
                BaseMapper<File> fileMapper = fileService.getBaseMapper();
                LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<File> eq = fileLambdaQueryWrapper
                    .eq(File::getBindId, item.getId());
                List<File> files = fileMapper.selectList(eq);
                if (CollectionUtil.isEmpty(files)) {
                    list.add("0");
                } else {
                    list.add(files.size());
                }

                //SN码
                list.add(item.getSnNo() == null ? "" : item.getSnNo());

                //物联网卡号
                list.add(item.getCardNumber() == null ? "" : item.getCardNumber());
                //物联网卡供应商
                list.add(item.getCardSupplier() == null ? "" : item.getCardSupplier());

                //详细地址
                list.add(item.getAddress() == null ? "" : item.getAddress());

                //安装时间
                if (item.getCreateTime() != null) {
                    list.add(DateUtils.stampToDate(item.getInstallTime().toString()));
                } else {
                    list.add("");
                }
                //质保有效期
                list.add(item.getWarrantyPeriod() == null ? "0年" : item.getWarrantyPeriod() + "年");
                //质保结束时间
                if (item.getWarrantyTime() != null) {
                    list.add(DateUtils.stampToDate(item.getWarrantyTime().toString()));
                } else {
                    list.add("");
                }
                //施工完成时间
                if (item.getCompletionTime() != null) {
                    list.add(DateUtils.stampToDate(item.getCompletionTime().toString()));
                } else {
                    list.add("");
                }

                //入账
                list.add(item.getIsEntry() == null ? "" : (item.getIsEntry() == 0 ? "否" : "是"));
                //验收
                list.add(
                    item.getIsAcceptance() == null ? ""
                        : (item.getIsAcceptance() == 0 ? "否" : "是"));

                //下单时间
                if (item.getOrderTime() != null) {
                    list.add(DateUtils.stampToDate(item.getOrderTime().toString()));
                } else {
                    list.add("");
                }

                //运营商
                list.add(item.getOperator() == null ? "" : item.getOperator());

                //物流信息
                list.add(item.getLogisticsInfo() == null ? "" : item.getLogisticsInfo());

                //审核人
                list.add(item.getAuditUserName() == null ? "" : item.getAuditUserName());

                //审核时间
                if (item.getAuditTime() != null) {
                    list.add(DateUtils.stampToDate(item.getAuditTime().toString()));
                } else {
                    list.add("");
                }

                //审核内容
                list.add(item.getAuditRemarks() == null ? "" : item.getAuditRemarks());

                //备注
                list.add(item.getRemarks() == null ? "" : item.getRemarks());

                //产品个数
//            if(productAll != null && !productAll.isEmpty()) {
//                List<ProductInfoQuery> productInfoQueries = null;
//                if(Objects.nonNull(item.getProductInfo())){
//                    productInfoQueries = JSON.parseArray(item.getProductInfo(), ProductInfoQuery.class);
//                }
//                if (!CollectionUtil.isEmpty(productInfoQueries)) {
//
//                    for (Product p : productAll) {
//                        //boolean falg = false;
//                        ProductInfoQuery index = null;
//                        for (ProductInfoQuery entry : productInfoQueries) {
//                            if (Objects.equals(p.getId(), entry.getProductId())) {
//                                //falg = true;
//                                index = entry;
//                            }
//                        }
//
//                        if (index != null) {
//                            list.add(index.getNumber());
//                        } else {
//                            list.add("");
//                        }
//                    }
//                }else{
//                    for (Product p : productAll) {
//                        list.add("");
//                    }
//                }
//            }
                //摄像头数量
                list.add(item.getCameraCount() == null ? "" : item.getCameraCount());

                //TODO
                //摄像头信息
                if (Objects.nonNull(item.getCameraInfo())) {
                    List<CameraInfoQuery> cameraInfoQuery = JSON
                        .parseArray(item.getCameraInfo(), CameraInfoQuery.class);
                    ;
                    if (!CollectionUtils.isEmpty(cameraInfoQuery)) {
                        int len = 0;
                        for (CameraInfoQuery cameraInfo : cameraInfoQuery) {
                            //摄像头运营商
                            list.add(cameraInfo.getCameraSupplier() == null ? ""
                                : cameraInfo.getCameraSupplier());
                            //摄像头序列号
                            list.add(
                                cameraInfo.getCameraSn() == null ? "" : cameraInfo.getCameraSn());
                            //摄像头卡号
                            list.add(cameraInfo.getCameraNumber() == null ? ""
                                : cameraInfo.getCameraNumber());

                            len++;
                        }
                        //填补
                        int finalMaxTemp = finalMax;
                        finalMaxTemp -= len;
                        for (int i = 0; i < finalMaxTemp; i++) {
                            list.add("");
                            list.add("");
                            list.add("");
                        }
                    } else {
                        for (int i = 0; i < finalMax; i++) {
                            list.add("");
                            list.add("");
                            list.add("");
                        }
                    }
                } else {
                    for (int i = 0; i < finalMax; i++) {
                        list.add("");
                        list.add("");
                        list.add("");
                    }
                }

                if (CollectionUtils.isEmpty(pointProductBinds)) {
                    pointExcelVos.add(list);
                } else {
                    pointProductBinds.parallelStream().forEach(pointProductBind -> {
                        List<Object> lineList = new ArrayList<>(list);
                        ProductNew productNew = productNewMapper
                            .queryById(pointProductBind.getProductId());
                        if (Objects.isNull(productNew)) {
                            lineList.add("");
                            lineList.add("");
                            lineList.add("");
                            return;
                        }

                        Product byId = productService.getById(productNew.getModelId());
                        if (Objects.isNull(byId)) {
                            lineList.add("");
                            lineList.add("");
                            lineList.add("");
                            return;
                        }

                        lineList.add(StringUtils.isEmpty(byId.getName()) ? "" : byId.getName());
                        lineList.add(Objects.isNull(productNew.getNo()) ? "" : productNew.getNo());
                        lineList.add(
                            Objects.equals(byId.getBuyType(), Product.BUY_TYPE_CENTRALIZED) ? "集采"
                                : "非集采");

                        pointExcelVos.add(lineList);
                    });
                }
                //}
            } catch (Exception e) {
                log.error("pointExcel Error" , e);
            }
        });

        String fileName = "点位.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            //EasyExcel.write(outputStream, PointExcelVo.class).sheet("sheet").doWrite(pointExcelVos);
            EasyExcelFactory.getWriter(outputStream).write1(pointExcelVos, sheet, table).finish();
            return;
        } catch (IOException e) {
            throw new NullPointerException("导出报表失败！请联系管理员处理！");
        }

    }

    @GetMapping("/admin/pointNew/info/{pid}")
    public R printInfo(@PathVariable("pid") Long pid) {
        return pointNewService.pointInfo(pid);
    }

    @GetMapping("/admin/pointNew/queryFiles/{pid}")
    public R queryFiles(@PathVariable("pid") Long pid) {
        return pointNewService.queryFiles(pid);
    }

    @PostMapping("admin/point/bindSerialNumber")
    public R pointBindSerialNumber(@RequestBody PointQuery pointQuery) {

        return pointNewService.pointBindSerialNumber(pointQuery);
    }

    @PostMapping("admin/pointNew/update/auditStatus")
    public R updateAuditStatus(@RequestBody PointAuditStatusQuery pointAuditStatusQuery) {
        return pointNewService.updateAuditStatus(pointAuditStatusQuery);
    }

    @PostMapping("admin/pointNew/batch/update/auditStatus")
    public R batchUpdateAuditStatus(@RequestBody PointAuditStatusQuery pointAuditStatusQuery) {
        return pointNewService.batchUpdateAuditStatus(pointAuditStatusQuery);
    }

    @GetMapping("admin/pointNew/map/statistics")
    public R pointNewMapStatistics(@RequestParam(value = "cityId", required = false) Long cityId,
        @RequestParam(value = "provinceId", required = false) Long provinceId,
        @RequestParam(value = "productSeries", required = false) Integer productSeries,
        @RequestParam(value = "coordXs") String coordXs,
        @RequestParam(value = "coordYs") String coordYs) {

        List<BigDecimal> coordXList = JsonUtil.fromJsonArray(coordXs, BigDecimal.class);
        List<BigDecimal> coordYList = JsonUtil.fromJsonArray(coordYs, BigDecimal.class);
        return pointNewService
            .pointNewMapStatistics(coordXList, coordYList, cityId, provinceId, productSeries);
    }

    @GetMapping("admin/pointNew/map/province/count")
    public R pointNewMapProvinceCount() {
        return pointNewService.pointNewMapProvinceCount();
    }

    @GetMapping("admin/pointNew/map/city/count")
    public R pointNewMapCityCount(@RequestParam("pid") Long pid) {
        return pointNewService.pointNewMapCityCount(pid);
    }
}
