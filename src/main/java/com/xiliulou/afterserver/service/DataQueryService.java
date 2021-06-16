package com.xiliulou.afterserver.service;


import com.xiliulou.afterserver.util.R;

import java.util.Map;

public interface DataQueryService {

    R installWorkOrder(Long pointId, Integer cityId, Integer dateType);

    R afterRatio(Long pointId, Integer cityId, Integer dateType);

    R after(Long pointId, Integer cityId, Integer dateType);

    R qualityAnalyse(Long pointId, Integer cityId, Integer dateType);

    R qualityAnalyseList(Long pointId, Integer cityId, Integer dateType);
}
