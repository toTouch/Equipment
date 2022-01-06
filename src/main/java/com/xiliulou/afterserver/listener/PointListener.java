package com.xiliulou.afterserver.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
                throw new RuntimeException("Not Find supplier");
            }
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

            if (Objects.nonNull(item.getName())){
                LambdaQueryWrapper<PointNew> wrapper = new LambdaQueryWrapper<PointNew>().eq(PointNew::getDelFlag, 0).eq(PointNew::getName, item.getName());
                PointNew pointNew = pointService.getBaseMapper().selectOne(wrapper);
                if (Objects.nonNull(pointNew)){
                    return;
                }
            }

            if (item.getCustomerId() != null) {
                LambdaQueryWrapper<Customer> like = new LambdaQueryWrapper<Customer>().like(Customer::getName, item.getCustomerId());
                Customer customer = customerService.getOne(like);
                if (Objects.nonNull(customer)) {
                    point.setCustomerId(customer.getId());
                }
            }

            if (item.getCity()!=null){
                LambdaQueryWrapper<City> like = new LambdaQueryWrapper<City>().like(City::getName, item.getCity());
                City city = cityService.getOne(like);
                if (Objects.nonNull(city)){
                    point.setCityId(city.getId());
                }
            }

            point.setCreateTime(System.currentTimeMillis());

            if (item.getInstallTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getInstallTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setInstallTime(l);
            }else {
                point.setInstallTime(System.currentTimeMillis());
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
