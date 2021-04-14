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

   /* @Override
    public Map<String, Object> getShowData(String begin, String end) {
*//*        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        //wrapper.select("date_calculated",type);
//        List<WorkOrder> staList = baseMapper.selectList(wrapper);
        List<Long> date_calculatedList = new ArrayList<>();
        List<Integer> numDataList = new ArrayList<>();

        for (int i = 0; i < staList.size(); i++ ) {
            WorkOrder daily = staList.get(i);
            //封装日期集合
            date_calculatedList.add(daily.getCreateTime());
            //封装对应数量
            switch (begin) {
                case "box_number":
                    numDataList.add(daily.getBoxNumber());
                    break;

                default:
                    break;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("date_calculatedList",date_calculatedList);
        map.put("numDataList",numDataList);
        return map;
    }*//*
    }*/
}
