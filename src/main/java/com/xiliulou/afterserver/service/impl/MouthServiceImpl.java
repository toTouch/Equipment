package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.service.MouthService;
import com.xiliulou.afterserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MouthServiceImpl implements MouthService {

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
}
