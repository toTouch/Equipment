package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.service.DataQueryService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class DataQueryServiceImpl implements DataQueryService {

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private ProductService productService;

    @Override
    public void getDataCount(Map<String, Object> params) {

        HashMap<String, Integer> map = new HashMap<>();

        //总工单
        Integer orderCount = workOrderService.getByDateQuery(params);
        map.put("WorkOrderCount",orderCount);

        //总支出
        Integer expendCount = workOrderService.getByDateQuery(params);
        map.put("WorkOrderCount",expendCount);

        //总点位
        Integer pointCount = workOrderService.getByDateQuery(params);
        map.put("workOrderCount",pointCount);
        //总格口
        Integer gridCount = productService.getByDateQuery(params);
        map.put("workOrderCount",gridCount);
    }
}
