package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.mapper.MouthMapper;
import com.xiliulou.afterserver.mapper.WorkOrderMapper;
import com.xiliulou.afterserver.service.MouthService;
import com.xiliulou.afterserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MouthServiceImpl extends ServiceImpl<MouthMapper,WorkOrder> implements MouthService {

    @Autowired
    private ProductService productService;

    @Autowired
    private MouthService mouthService;

    @Override
    public Map<String, Integer> getMouth(Map<String, Object> params) {

        HashMap<String, Integer> map = new HashMap<>();

        //总接口
        Integer mouthCount = productService.getMouth(params);
        map.put("getByMouthCount",mouthCount);

        return map;
    }

    @Override
    public Map<String, Object> getShowData( String begin, String end) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        //wrapper.select("date_calculated",type);
        List<WorkOrder> staList = baseMapper.selectList(wrapper);

        List<String> date_calculatedList = new ArrayList<>();
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
    }
}
