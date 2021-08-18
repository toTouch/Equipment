package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.PointNewService;
import com.xiliulou.afterserver.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

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

    public PointListener(PointNewService pointService, CustomerService customerService, CityService cityService) {
        this.pointService = pointService;
        this.cityService = cityService;
        this.customerService = customerService;
    }

    @Override
    public void invoke(PointInfo pointInfo, AnalysisContext analysisContext) {
        log.info("点位导入=====解析到一条数据:{}", JSON.toJSONString(pointInfo));
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

            if (item.getCreateTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getCreateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setCreateTime(l);
            }

            point.setCreateTime(System.currentTimeMillis());
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
