package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private PointService pointService;

    @Autowired
    private StatService statService;

    @Autowired
    private StatService baseMapper;

    @Override
    public void getStat(Map<String, Object> params) {

        HashMap<String, Integer> map = new HashMap<>();

        //总点位
        Integer pointCount = pointService.getByStatCount(params);
        map.put("getByStatCount",pointCount);

        //城市点位

    }


}
