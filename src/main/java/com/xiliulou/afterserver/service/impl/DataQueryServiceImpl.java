package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.WorkOrderReason;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.AfterCountListVo;
import com.xiliulou.afterserver.web.vo.AfterCountVo;
import com.xiliulou.afterserver.web.vo.AfterOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public R installWorkOrder(Long pointId, Integer cityId, Integer dateType) {
        Long datestamp = null;

        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
            return R.fail("查询时间间隔不正确");
        }
        datestamp =  ((dateType == 1 ) ? null : DateUtils.daysToStamp(-(dateType - 1)));
        List<AfterOrderVo>  installWorkOrderByCityList = workOrderService.installWorkOrderByCity(pointId,cityId,datestamp);
        List<AfterOrderVo>  installWorkOrderByPointList = workOrderService.installWorkOrderByPoint(pointId,cityId,datestamp);
        List<AfterOrderVo>  installWorkOrderList = workOrderService.installWorkOrderList(pointId,cityId,datestamp);

        HashMap<String, Object> map = new HashMap<>(3);
        map.put("installWorkOrderByCityList",installWorkOrderByCityList);
        map.put("installWorkOrderByPointList",installWorkOrderByPointList);
        map.put("installWorkOrderList",installWorkOrderList);
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
        datestamp =  ((dateType == 1 ) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterOrderVo>  afterWorkOrderByCityList = workOrderService.afterWorkOrderByCity(pointId,cityId,datestamp);
        List<AfterOrderVo>  afterWorkOrderByPointList = workOrderService.afterWorkOrderByPoint(pointId,cityId,datestamp);
        List<AfterOrderVo>  afterWorkOrderList = workOrderService.afterWorkOrderList(pointId,cityId,datestamp);

        HashMap<String, Object> map = new HashMap<>(3);
        map.put("afterWorkOrderByCityList",afterWorkOrderByCityList);
        map.put("afterWorkOrderByPointList",afterWorkOrderByPointList);
        map.put("afterWorkOrderList",afterWorkOrderList);
        return R.ok().data(map);
    }

    @Override
    public R qualityAnalyse(Long pointId, Integer cityId, Integer dateType) {
        Long datestamp = null;

        if (Objects.isNull(dateType) || dateType < 0 || dateType > 90) {
            return R.fail("查询时间间隔不正确");
        }
        datestamp =  ((dateType == 1 ) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterCountVo> qualityCount = workOrderService.qualityCount(pointId,cityId,datestamp);
        qualityCount.forEach(item -> {
            if (item.getReasonId()!=null){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(item.getReasonId());
                if (Objects.nonNull(workOrderReason)){
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
        datestamp =  ((dateType == 1 ) ? null : DateUtils.daysToStamp(-(dateType - 1)));

        List<AfterCountListVo> qualityCount = workOrderService.qualityCountList(pointId,cityId,datestamp);
        qualityCount.forEach(item -> {
            if (item.getReasonId()!=null){
                WorkOrderReason workOrderReason = workOrderReasonService.getById(item.getReasonId());
                if (Objects.nonNull(workOrderReason)){
                    item.setReasonName(workOrderReason.getName());
                }
            }
        });
        return R.ok(qualityCount);
    }
}
