package com.xiliulou.afterserver.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hardy
 * @date 2021/4/25 0025 13:30
 * @Description: 点位表
 */
@Slf4j
public class PointListener extends AnalysisEventListener<PointInfo> {

    private static final int BATCH_COUNT = 2000;
    List<PointInfo> list = new ArrayList<>();

    private PointNewService pointService;
    private CustomerService customerService;
    private CityService cityService;
    private HttpServletRequest request;
    private SupplierService supplierService;

    public PointListener(PointNewService pointService, CustomerService customerService, CityService cityService, HttpServletRequest request, SupplierService supplierService) {
        this.pointService = pointService;
        this.cityService = cityService;
        this.customerService = customerService;
        this.supplierService =supplierService;
        this.request = request;
    }

    @Override
    public void invoke(PointInfo pointInfo, AnalysisContext analysisContext) {
        log.info("点位导入=====解析到一条数据:{}", JSON.toJSONString(pointInfo));

        if(Objects.nonNull(pointInfo.getCardSupplier())){
            Supplier supplier = supplierService.getOne(new QueryWrapper<Supplier>().eq("name", pointInfo.getCardSupplier()));
            if(Objects.isNull(supplier)){
                log.error("insert PointInfo error! Not Find supplier pointName={}",pointInfo.getName());
                throw new RuntimeException("未查询到物联网卡供应商");
            }
        }

        if(Objects.isNull(pointInfo.getProductSeries())){
            log.error("insert PointInfo error! product series is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写产品系列");
        }

        if(StringUtils.isBlank(pointInfo.getName())){
            log.error("insert PointInfo error! Name is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写点位名称");
        }

        if(Objects.isNull(pointInfo.getStatus())){
            log.error("insert PointInfo error! Status is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写点位状态");
        }

        if(Objects.isNull(pointInfo.getInstallType())){
            log.error("insert PointInfo error! InstallType is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写安装类型");
        }

        if(StringUtils.isBlank(pointInfo.getCity())){
            log.error("insert PointInfo error! City is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写城市信息");
        }else{
            LambdaQueryWrapper<City> like = new LambdaQueryWrapper<City>().eq(City::getName, pointInfo.getCity());
            City city = cityService.getOne(like);
            if (Objects.isNull(city)){
                log.error("insert PointInfo error! not fund City pointName={}",pointInfo.getName());
                throw new RuntimeException("点位" + pointInfo.getName() + "没有查询到城市信息");
            }
        }

        if(Objects.isNull(pointInfo.getCustomerId())){
            log.error("insert PointInfo error! Customer is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写客户信息");
        }else{
            LambdaQueryWrapper<Customer> like = new LambdaQueryWrapper<Customer>().eq(Customer::getName, pointInfo.getCustomerId());
            Customer customer = customerService.getOne(like);
            if (Objects.isNull(customer)) {
                log.error("insert PointInfo error! not fund Customer pointName={}",pointInfo.getName());
                throw new RuntimeException("点位" + pointInfo.getName() + "没有查询到客户信息");
            }
        }
        if(Objects.nonNull(pointInfo.getWarrantyPeriod()) && Objects.isNull(pointInfo.getInstallTime())){
            log.error("insert PointInfo error! not calculation warrantyTime pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "没有添加安装时间");
        }
        list.add(pointInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("客户表导入=====所有数据解析完成！");
    }


    /**
     * 入库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());

        List<PointNew> pointList = new ArrayList<>();
        this.list.forEach(item -> {
            PointNew point = new PointNew();
            BeanUtils.copyProperties(item, point);
            point.setCameraCount(item.getCameraCount());
            point.setCanopyCount(item.getCanopyCount());
            point.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
            if (Objects.nonNull(item.getName())){
                LambdaQueryWrapper<PointNew> wrapper = new LambdaQueryWrapper<PointNew>().eq(PointNew::getDelFlag, 0).eq(PointNew::getName, item.getName());
                PointNew pointNew = pointService.getBaseMapper().selectOne(wrapper);
                if (Objects.nonNull(pointNew)){
                    return;
                }
            }

            if (item.getCustomerId() != null) {
                LambdaQueryWrapper<Customer> like = new LambdaQueryWrapper<Customer>().eq(Customer::getName, item.getCustomerId());
                Customer customer = customerService.getOne(like);
                if (Objects.nonNull(customer)) {
                    point.setCustomerId(customer.getId());
                }
            }

            if (item.getCity()!=null){
                LambdaQueryWrapper<City> like = new LambdaQueryWrapper<City>().eq(City::getName, item.getCity());
                City city = cityService.getOne(like);
                if (Objects.nonNull(city)){
                    point.setCityId(city.getId());
                }
            }

            point.setCreateTime(System.currentTimeMillis());

            Long installTime = 0L;
            if (item.getInstallTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getInstallTime());
                    installTime = l;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setInstallTime(l);
            }else {
                installTime = System.currentTimeMillis();
                point.setInstallTime(installTime);
            }

            if(Objects.isNull(item.getWarrantyPeriod())){
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(installTime);
                calendar.add(Calendar.YEAR, Integer.parseInt(item.getWarrantyPeriod()));
                point.setWarrantyPeriod(Integer.parseInt(item.getWarrantyPeriod()));
                point.setWarrantyTime(calendar.getTimeInMillis());
            }

            if(item.getStatus() != null){
                if (Objects.equals(item.getStatus(),"1") || "移机".equals(item.getStatus())){
                    point.setStatus(1);
                }else if (Objects.equals(item.getStatus(),"2") || "运营中".equals(item.getStatus())){
                    point.setStatus(2);
                }else if (Objects.equals(item.getStatus(),"3") || "已拆机".equals(item.getStatus())){
                    point.setStatus(3);
                }else if (Objects.equals(item.getStatus(),"4") || "初始化".equals(item.getStatus())){
                    point.setStatus(4);
                }else if (Objects.equals(item.getStatus(),"5") || "待安装".equals(item.getStatus())){
                    point.setStatus(5);
                }else if (Objects.equals(item.getStatus(),"6") || "运输中".equals(item.getStatus())){
                    point.setStatus(6);
                }else if (Objects.equals(item.getStatus(),"7") || "安装中".equals(item.getStatus())){
                    point.setStatus(7);
                }else if (Objects.equals(item.getStatus(),"8") || "安装完成".equals(item.getStatus())){
                    point.setStatus(8);
                }else if (Objects.equals(item.getStatus(),"9") || "已暂停".equals(item.getStatus())){
                    point.setStatus(9);
                }else if (Objects.equals(item.getStatus(),"10") || "已取消".equals(item.getStatus())){
                    point.setStatus(10);
                }else if (Objects.equals(item.getStatus(),"11") || "已过保".equals(item.getStatus())){
                    point.setStatus(11);
                }
            }

            if (item.getCompletionTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getCompletionTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setCompletionTime(l);
            }else {
                point.setCompletionTime(System.currentTimeMillis());
            }

            if(Objects.nonNull(item.getIsEntry())){
                if("1".equals(item.getIsEntry()) || "是".equals(item.getIsEntry())){
                    point.setIsEntry(1);
                }
                if("0".equals(item.getIsEntry()) || "否".equals(item.getIsEntry())){
                    point.setIsEntry(0);
                }
            }

            if(Objects.nonNull(item.getIsAcceptance())){
                if("1".equals(item.getIsAcceptance()) || "是".equals(item.getIsAcceptance())){
                    point.setIsAcceptance(1);
                }
                if("0".equals(item.getIsAcceptance()) || "否".equals(item.getIsAcceptance())){
                    point.setIsAcceptance(0);
                }
            }

            if(Objects.nonNull(item.getInstallType())){
                if("1".equals(item.getInstallType()) || "室外".equals(item.getInstallType())){
                    point.setInstallType(1);
                }
                if("2".equals(item.getInstallType()) || "半室外".equals(item.getInstallType())){
                    point.setInstallType(2);
                }
                if("3".equals(item.getInstallType()) || "室内".equals(item.getInstallType())){
                    point.setInstallType(3);
                }
            }

            if(Objects.nonNull(item.getProductSeries())){
                if("1".equals(item.getProductSeries()) || "取餐柜".equals(item.getProductSeries())){
                    point.setProductSeries(1);
                }
                if("2".equals(item.getProductSeries()) || "餐厅柜".equals(item.getProductSeries())){
                    point.setProductSeries(2);
                }
                if("3".equals(item.getProductSeries()) || "换电柜".equals(item.getProductSeries())){
                    point.setProductSeries(3);
                }
                if("4".equals(item.getProductSeries()) || "充电柜".equals(item.getProductSeries())){
                    point.setProductSeries(4);
                }
                if("5".equals(item.getProductSeries()) || "寄存柜".equals(item.getProductSeries())){
                    point.setProductSeries(5);
                }
                if("6".equals(item.getProductSeries()) || "生鲜柜".equals(item.getProductSeries())){
                    point.setProductSeries(6);
                }
            }


            if (item.getOrderTime()!= null){
                long l = 0;
                try {
                    l = dateToStamp(item.getOrderTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setOrderTime(l);
            }else {
                point.setOrderTime(System.currentTimeMillis());
            }

            Long uid = (Long) request.getAttribute("uid");
            if (Objects.nonNull(uid)){
                point.setCreateUid(uid);
            }

            point.setDelFlag(PointNew.DEL_NORMAL);
            point.setRemarks(item.getRemarks());



            pointList.add(point);
        });

        pointService.saveBatch(pointList);
        log.info("存储数据库成功！");
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

}
