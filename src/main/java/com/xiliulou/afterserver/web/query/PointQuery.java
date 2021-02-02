package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.Point;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 11:27
 **/
@Data
public class PointQuery extends Point {

    private List<String> fileNameList;
    private Map<Long, Integer> productIdAndCountMap;

    private Long createTimeStart;
    private Long createTimeEnd;
}
