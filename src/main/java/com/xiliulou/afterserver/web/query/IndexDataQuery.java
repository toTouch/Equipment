package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 16:24
 **/
@Data
public class IndexDataQuery {
    private String city;
    private Long customerId;
    private Long pointId;
    private Long workOrderTypeId;
    private Long queryStartTime;
    private Long queryEndTime;
}
