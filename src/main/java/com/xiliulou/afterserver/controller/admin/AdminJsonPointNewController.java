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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.vo.PointExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

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


    @PostMapping("/admin/pointNew")
    public R saveAdminPointNew(@RequestBody PointNew pointNew, HttpServletRequest request){
        Long uid = (Long) request.getAttribute("uid");
        if (Objects.isNull(uid)){
            return R.fail("用户为空");
        }
        pointNew.setCreateUid(uid);
        return pointNewService.saveAdminPointNew(pointNew);
    }

    @PutMapping("/admin/pointNew")
    public R putAdminPointNew(@RequestBody PointNew pointNew){
        return pointNewService.putAdminPoint(pointNew);
    }

    @DeleteMapping("/admin/pointNew/{id}")
    public R delAdminPointNew(@PathVariable("id") Long id){
        return pointNewService.delAdminPointNew(id);
    }

    @GetMapping("/admin/pointNew/list")
    public R pointList(@RequestParam("offset") Integer offset,
                       @RequestParam("limit") Integer limit,
                       @RequestParam(value = "name",required = false) String name,
                       @RequestParam(value = "cid",required = false) Integer cid,
                       @RequestParam(value = "status",required = false) Integer status,
                       @RequestParam(value = "customerId",required = false) Long customerId,
                       @RequestParam(value = "startTime",required = false) Long startTime,
                       @RequestParam(value = "endTime",required = false) Long endTime,
                       @RequestParam(value = "createUid",required = false) Long createUid,
                       @RequestParam(value = "snNo",required = false) String snNo){
        List<PointNew> pointNews = pointNewService.queryAllByLimit(offset, limit, name,cid,status,customerId,startTime,endTime,createUid,snNo);

        if (Objects.nonNull(pointNews)){
            pointNews.forEach(item -> {
                if (Objects.nonNull(item.getCityId())){
                    City byId = cityService.getById(item.getCityId());
                    item.setCityName(byId.getName());
                    item.setProvince(byId.getPid().toString());
                }

                if (Objects.nonNull(item.getCustomerId())){
                    Customer byId = customerService.getById(item.getCustomerId());
                    if (Objects.nonNull(byId)){
                        item.setCustomerName(byId.getName());
                    }
                }

                if (Objects.nonNull(item.getCreateUid())){
                    User userById = userService.getUserById(item.getCreateUid());
                    if (Objects.nonNull(userById)){
                        item.setUserName(userById.getUserName());
                    }
                }
            });
        }


        Integer count =  pointNewService.countPoint(name,cid,status,customerId,startTime,endTime,createUid,snNo);


        HashMap<String, Object> map = new HashMap<>();
        map.put("data",pointNews);
        map.put("count",count);
        return R.ok(map);
    }


    /**
     * 导入
     */
    @PostMapping("admin/pointNew/upload")
    public R upload(MultipartFile file){

        ExcelReader excelReader = null;
        try {
            try {
                excelReader = EasyExcel.read(file.getInputStream(), PointInfo.class,new PointListener(pointNewService,customerService,cityService)).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ExcelAnalysisException e) {
            e.printStackTrace();
            if (e.getCause() instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) e.getCause();
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
                String errorMsg = String.format("excel表格:第%s行,第%s列,数据值为:%s,该数据值不符合要求,请检验后重新导入!<span style=\"color:red\">请检查其他的记录是否有同类型的错误!</span>", excelDataConvertException.getRowIndex() + 1, excelDataConvertException.getColumnIndex(), cellMsg);
                log.error(errorMsg);
            }
        }
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

    @GetMapping("/admin/pointNew/exportExcel")
    public void pointExportExcel(@RequestParam(value = "name",required = false) String name,
                              @RequestParam(value = "cid",required = false) Integer cid,
                              @RequestParam(value = "status",required = false) Integer status,
                              @RequestParam(value = "customerId",required = false) Long customerId,
                              @RequestParam(value = "startTime",required = false) Long startTime,
                              @RequestParam(value = "endTime",required = false) Long endTime,
                              @RequestParam(value = "createUid",required = false) Long createUid,
                              @RequestParam(value = "snNo",required = false)  String snNo,
                              HttpServletResponse response){
        List<PointNew> pointNews = pointNewService.queryAllByLimitExcel(name,cid,status,customerId,startTime,endTime,createUid,snNo);


        if (Objects.isNull(startTime) || Objects.isNull(endTime)){
            throw new NullPointerException("请选择开始时间和结束时间");
        }

        if (pointNews.isEmpty()){
            return;
        }

        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();

        String[] header = {"柜机名称", "机柜状态", "安装类型", "详细地址", "摄像头数量", "雨棚数量", "SN码", "物联网卡号", "安装时间", "城市名称", "客户名称"};
        List<Product> productAll = productService.list();
        for(String s : header){
            List<String> headTitle = new ArrayList<>();
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


        ArrayList<List<Object>> pointExcelVos = new ArrayList<>();

        pointNews.forEach(item -> {
            //PointExcelVo pointExcelVo = new PointExcelVo();
            List<Object> list = new ArrayList<>();

            //柜机名称
            list.add(item.getName() == null ? "" : item.getName());

            //机柜状态
            // 状态 1,移机,2运营中,3:拆机,4:初始化，5：待安装
            if (Objects.isNull(item.getStatus())){
                list.add("");
            }else if (Objects.equals(item.getStatus(),1)){
                list.add("移机");
            }else if (Objects.equals(item.getStatus(),2)){
                list.add("运营中");
            }else if (Objects.equals(item.getStatus(),3)){
                list.add("拆机");
            }else if (Objects.equals(item.getStatus(),4)){
                list.add("初始化");
            }else if (Objects.equals(item.getStatus(),5)){
                list.add("待安装");
            }

            // 安装类型 1:室外 2:半室外3：室内
            if (Objects.isNull(item.getInstallType())){
                list.add("");
            }else if (Objects.equals(item.getInstallType(),1)){
                list.add("室外");
            }else if (Objects.equals(item.getInstallType(),2)){
                list.add("半室外");
            }else if (Objects.equals(item.getInstallType(),3)){
                list.add("室内");
            }

            //详细地址
            list.add(item.getAddress() == null ? "" : item.getAddress());

            //摄像头数量
            list.add(item.getCameraCount() == null ? "" : item.getCameraCount());

            //雨棚数量
            list.add(item.getCanopyCount() == null ? "" : item.getCanopyCount());

            //SN码
            list.add(item.getSnNo() == null ? "" : item.getSnNo());

            //物联网卡号
            list.add(item.getCardNumber() == null ? "" : item.getCardNumber());
            //安装时间
            if (item.getCreateTime() != null) {
                list.add(DateUtils.stampToDate(item.getCreateTime().toString()));
            }else{
                list.add("");
            }
            //城市名称
            if (Objects.nonNull(item.getCityId())){
                City byId = cityService.getById(item.getCityId());
                if (Objects.nonNull(byId)){
                    list.add(byId.getName());
                }else{
                    list.add("");
                }
            }else{
                list.add("");
            }
            //客户名称
            if (Objects.nonNull(item.getCustomerId())){
                Customer byId = customerService.getById(item.getCustomerId());
                if (Objects.nonNull(byId)){
                    list.add(byId.getName());
                }else{
                    list.add("");
                }
            }else{
                list.add("");
            }

            if(productAll != null && !productAll.isEmpty()) {
                Map<Long, Long> productStatisticsMap = new HashMap<>();
                List<PointProductBind> pointProductBinds = pointProductBindMapper.selectList(new QueryWrapper<PointProductBind>().eq("point_id", item.getId()));
                if (!CollectionUtil.isEmpty(pointProductBinds)) {
                    for (PointProductBind pointProductBind : pointProductBinds) {
                        ProductNew productNew = productNewMapper.selectById(pointProductBind.getProductId());
                        if (!Objects.isNull(productNew)) {
                            Product product = productService.getById(productNew.getModelId());
                            productStatisticsMap.put(product.getId(), productStatisticsMap.containsKey(product.getId()) ? productStatisticsMap.get(product.getId()) + 1 : 1);
                        }
                    }
                    for (Product p : productAll) {
                        boolean falg = false;
                        Map.Entry index = null;
                        for (Map.Entry<Long, Long> entry : productStatisticsMap.entrySet()) {
                            if (Objects.equals(p.getId().intValue(), entry.getKey().intValue())) {
                                falg = true;
                                index = entry;
                            }
                        }

                        if (index != null) {
                            list.add(index.getValue());
                        } else {
                            list.add("0");
                        }
                    }
                }
            }

            pointExcelVos.add(list);

        });

        String fileName = "点位.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            //EasyExcel.write(outputStream, PointExcelVo.class).sheet("sheet").doWrite(pointExcelVos);
            EasyExcelFactory.getWriter(outputStream).write1(pointExcelVos,sheet,table).finish();
            return;
        } catch (IOException e) {
            throw new NullPointerException("导出报表失败！请联系管理员处理！");
        }


    }

    @GetMapping("/admin/pointNew/info/{pid}")
    public R printInfo(@PathVariable("pid") Long pid){
        return pointNewService.pointInfo(pid);
    }

    /*@PostMapping("/admin/pointNew/saveCache")
    public R saveCache(Long pointId,
                       @RequestParam(value = "modelId", required = false) Long modelId,
                       @RequestParam(value = "no", required = false) String no,
                       @RequestParam(value = "batch", required = false, defaultValue = "16") Long batch){

            return pointNewService.saveCache(pointId, modelId, no, batch);
    }

    @DeleteMapping("/admin/pointNew/deleteProduct")
    public R deleteProduct(Long pointId, Long producutId){
        return pointNewService.deleteProduct(pointId, producutId);
    }*/
}
