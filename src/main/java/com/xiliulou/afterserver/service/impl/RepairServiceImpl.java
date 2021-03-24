package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.service.RepairService;
import com.xiliulou.afterserver.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private RepairService repairService;

    @Override
    public Map<String, Integer> getRepairCount(Map<String, Object> params) {

        HashMap<String, Integer> map = new HashMap<>();

        //总工单
        Integer orderCount = workOrderService.getByDateQuery(params);
        map.put("getRepairCount",orderCount);

        //总支出
        Integer expendCount = workOrderService.getTotal(params);
        map.put("getTotal",expendCount);

        return map;
    }
}
