package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.WorkOrderReason;
import com.xiliulou.afterserver.entity.WorkOrderType;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.AfterCountListVo;
import com.xiliulou.afterserver.web.vo.AfterCountVo;
import com.xiliulou.afterserver.web.vo.AfterOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DataQueryServiceImpl implements DataQueryService {

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private PointService pointService;

    @Autowired
    private WorkOrderReasonService workOrderReasonService;
    @Autowired
    private CityService cityService;
    @Autowired
    private WorkOrderTypeService workOrderTypeService;

    @Override
    public R installWorkOrder(Long pointId, Integer cityId, Integer dateType) {
        Long datestamp = null;

        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
            return R.fail("查询时间间隔不正确");
        }

        datestamp = ((dateType == 1) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterOrderVo> installWorkOrderByCityList = workOrderService.installWorkOrderByCity(pointId, cityId, datestamp);
        installWorkOrderByCityList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
        });

        List<AfterOrderVo> installWorkOrderByPointList = workOrderService.installWorkOrderByPoint(pointId, cityId, datestamp);

        Double avg = 0.00;
        installWorkOrderByPointList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
            if (Objects.nonNull(item.getType())) {
                WorkOrderType workOrderType = workOrderTypeService.getById(item.getType());
                if (Objects.nonNull(workOrderType)) {
                    item.setTypeName(workOrderType.getType());
                }
            }
            if (Objects.nonNull(item.getSumCount())) {
                add(avg.toString(), item.getSumCount());
            }
        });

        List<AfterOrderVo> installWorkOrderList = workOrderService.installWorkOrderList(pointId, cityId, datestamp);
        installWorkOrderList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
        });

        BigDecimal sumCount = new BigDecimal(avg);
        BigDecimal divide = sumCount.divide(BigDecimal.valueOf(installWorkOrderByPointList.size()));

        HashMap<String, Object> map = new HashMap<>(4);
        map.put("installWorkOrderByCityList", installWorkOrderByCityList);
        map.put("installWorkOrderByPointList", installWorkOrderByPointList);
        map.put("installWorkOrderList", installWorkOrderList);
        map.put("avg",divide);
        return R.ok().data(map);
    }

    @Override
    public R afterRatio(Long pointId, Integer cityId, Integer dateType) {
        return null;
    }

    @Override
    public R after(Long pointId, Integer cityId, Integer dateType) {
        Long datestamp = null;

        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
            return R.fail("查询时间间隔不正确");
        }
        datestamp = ((dateType == 1) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterOrderVo> afterWorkOrderByCityList = workOrderService.afterWorkOrderByCity(pointId, cityId, datestamp);
        afterWorkOrderByCityList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
        });
        List<AfterOrderVo> afterWorkOrderByPointList = workOrderService.afterWorkOrderByPoint(pointId, cityId, datestamp);
        List<AfterOrderVo> afterWorkOrderList = workOrderService.afterWorkOrderList(pointId, cityId, datestamp);
        afterWorkOrderList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
        });

        HashMap<String, Object> map = new HashMap<>(3);
        map.put("afterWorkOrderByCityList", afterWorkOrderByCityList);
        map.put("afterWorkOrderByPointList", afterWorkOrderByPointList);
        map.put("afterWorkOrderList", afterWorkOrderList);
        return R.ok().data(map);
    }

    @Override
    public R qualityAnalyse(Long pointId, Integer cityId, Integer dateType) {
        Long datestamp = null;

        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
            return R.fail("查询时间间隔不正确");
        }
        datestamp = ((dateType == 1) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterCountVo> qualityCount = workOrderService.qualityCount(pointId, cityId, datestamp);
        qualityCount.forEach(item -> {
            if (item.getReasonId() != null) {
                WorkOrderReason workOrderReason = workOrderReasonService.getById(item.getReasonId());
                if (Objects.nonNull(workOrderReason)) {
                    item.setReasonName(workOrderReason.getName());
                }
            }
        });
        return R.ok(qualityCount);
    }


    @Override
    public R qualityAnalyseList(Long pointId, Integer cityId, Integer dateType) {
        Long datestamp = null;

        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
            return R.fail("查询时间间隔不正确");
        }
        datestamp = ((dateType == 1) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterCountListVo> qualityCount = workOrderService.qualityCountList(pointId, cityId, datestamp);
        qualityCount.forEach(item -> {
            if (item.getReasonId() != null) {
                WorkOrderReason workOrderReason = workOrderReasonService.getById(item.getReasonId());
                if (Objects.nonNull(workOrderReason)) {
                    item.setReasonName(workOrderReason.getName());
                }
            }
        });
        return R.ok(qualityCount);
    }

    private static double add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }


}
