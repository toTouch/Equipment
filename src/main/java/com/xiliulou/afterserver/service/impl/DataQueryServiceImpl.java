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
    public Map<String, Integer> getDataCount(Map<String, Object> params) {

        HashMap<String, Integer> map = new HashMap<>();

        //总工单
        Integer orderCount = workOrderService.getByDateQuery(params);
        map.put("getDataCount",orderCount);

        //总支出
        Integer expendCount = workOrderService.getGeneral(params);
        map.put("getDataCount",expendCount);

        //总点位
        Integer pointCount = workOrderService.getTotal(params);
        map.put("getDataCount",pointCount);

        //总格口
        Integer gridCount = productService.getMouth(params);
        map.put("getDataCount",gridCount);

        return map;
    }
}
