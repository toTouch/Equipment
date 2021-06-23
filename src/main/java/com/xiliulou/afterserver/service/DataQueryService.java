package com.xiliulou.afterserver.service;


import com.xiliulou.afterserver.util.R;

import java.util.Map;

public interface DataQueryService {

    R installWorkOrder(Long pointId, Integer cityId,Long startTime,Long endTime);

    R after(Long pointId, Integer cityId,Long startTime,Long endTime);

    R qualityAnalyse(Long pointId, Integer cityId,Long startTime,Long endTime);

    R qualityAnalyseList(Long pointId, Integer cityId, Long stratTime,Long endTime);
}
