package com.xiliulou.afterserver.service;

import java.util.Map;

public interface StatService {

    //获取统计数据
    Map<String, Integer> getStat(Map<String, Object> params);


}
