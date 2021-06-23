package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.InstallSumCountVo;
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
import java.util.*;
import java.util.stream.Collectors;

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
    public R installWorkOrder(Long pointId, Integer cityId,Long startTime,Long endTime) {
        Integer pointCount = pointService.queryPointCountFromDate(startTime,endTime);
        List<AfterOrderVo> installWorkOrderByCityList = workOrderService.installWorkOrderByCity(pointId,cityId,startTime,endTime);
        installWorkOrderByCityList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
            if (pointCount !=null && item.getSumCount()!=null){
                Integer count = Integer.parseInt(item.getSumCount()) / pointCount;
                item.setSumCount(count.toString());
            }
        });

        List<AfterOrderVo> installWorkOrderByPointList = workOrderService.installWorkOrderByPoint(pointId, cityId, startTime,endTime);

        Double avg = 0.00;
        Double finalAvg = avg;
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
                add(finalAvg.toString(), item.getSumCount());
            }
        });

        avg = installWorkOrderByPointList.stream().mapToDouble(item -> Double.parseDouble(item.getSumCount())).average().getAsDouble();

        Map<Long, List<AfterOrderVo>> collect = installWorkOrderByPointList
                .stream()
                .collect(Collectors.groupingBy(AfterOrderVo::getPointId));

        ArrayList<InstallSumCountVo> list = new ArrayList<>();
        collect.forEach((k,v) -> {
            InstallSumCountVo installSumCountVo = new InstallSumCountVo();
            double sum = v.stream().mapToDouble(item -> Double.parseDouble(item.getSumCount())).sum();

            v.forEach(item -> {
                installSumCountVo.setPointName(item.getPointName());
            });

            installSumCountVo.setPointId(k);
            installSumCountVo.setSum(sum);
            installSumCountVo.setList(v);
            list.add(installSumCountVo);
        });

        List<InstallSumCountVo> collect1 = list.stream().sorted(Comparator.comparing(InstallSumCountVo::getSum).reversed()).collect(Collectors.toList());

        List<AfterOrderVo> installWorkOrderList = workOrderService.installWorkOrderList(pointId, cityId, null,null);
        Integer pointCountFromList = pointService.queryPointCountFromDate(null,null);
        installWorkOrderList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }
            if (pointCount !=null && item.getSumCount()!=null){
                Integer count = Integer.parseInt(item.getSumCount()) / pointCountFromList;
                item.setSumCount(count.toString());
            }
        });

        HashMap<String, Object> map = new HashMap<>(4);
        map.put("installWorkOrderByCityList", installWorkOrderByCityList);
        map.put("installWorkOrderByPointMap", collect1);
        map.put("installWorkOrderList", installWorkOrderList);
        map.put("avg",String.format("%.2f",avg));
        return R.ok().data(map);
    }

    @Override
    public R after(Long pointId, Integer cityId,Long startTime,Long endTime) {
       Integer pointCount = pointService.queryPointCountFromDate(null,null);

        List<AfterOrderVo> afterWorkOrderByCityList = workOrderService.afterWorkOrderByCity(pointId, cityId, startTime,endTime);
        afterWorkOrderByCityList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }

            if (pointCount !=null && item.getSumCount()!=null){
                Integer count = Integer.parseInt(item.getSumCount()) / pointCount;
                item.setSumCount(count.toString());
            }

            if (pointCount!=null && item.getNumCount()!=null){
                int i = item.getNumCount() / pointCount;
                item.setNumCount(i);
            }
        });

        List<AfterOrderVo> afterWorkOrderByPointList = workOrderService.afterWorkOrderByPoint(pointId, cityId,startTime,endTime);

        List<AfterOrderVo> afterWorkOrderList = workOrderService.afterWorkOrderList(pointId, cityId,null,null);
        afterWorkOrderList.forEach(item -> {
            if (Objects.nonNull(item.getCity())) {
                City city = cityService.getById(item.getCity());
                if (Objects.nonNull(city)) {
                    item.setCityName(city.getName());
                }
            }

            if (pointCount !=null && item.getSumCount()!=null){
                Integer count = Integer.parseInt(item.getSumCount()) / pointCount;
                item.setSumCount(count.toString());
            }
            if (pointCount!=null && item.getNumCount()!=null){
                int i = item.getNumCount() / pointCount;
                item.setNumCount(i);
            }
        });

        HashMap<String, Object> map = new HashMap<>(3);
        map.put("afterWorkOrderByCityList", afterWorkOrderByCityList);
        map.put("afterWorkOrderByPointList", afterWorkOrderByPointList);
        map.put("afterWorkOrderList", afterWorkOrderList);
        return R.ok().data(map);
    }

    @Override
    public R qualityAnalyse(Long pointId, Integer cityId,Long startTime,Long endTime) {
        List<AfterCountVo> qualityCount = workOrderService.qualityCount(pointId, cityId, startTime,endTime);
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
    public R qualityAnalyseList(Long pointId, Integer cityId, Long stratTime,Long endTime) {
//        Long datestamp = null;
//
//        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
//            return R.fail("查询时间间隔不正确");
//        }
//        datestamp = ((dateType == 1) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterCountListVo> qualityCount = workOrderService.qualityCountList(pointId, cityId, stratTime,endTime);
        qualityCount.forEach(item -> {
            if (item.getReasonId() != null) {
                WorkOrderReason workOrderReason = workOrderReasonService.getById(item.getReasonId());
                if (Objects.nonNull(workOrderReason)) {
                    item.setReasonName(workOrderReason.getName());
                }
            }
        });

        Map<Long, List<AfterCountListVo>> collect = qualityCount.stream().collect(Collectors.groupingBy(AfterCountListVo::getReasonId));
        return R.ok(collect);
    }

    private static double add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }


}
