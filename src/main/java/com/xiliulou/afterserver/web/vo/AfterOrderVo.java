package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author Hardy
 * @date 2021/6/15 0015 17:11
 * @Description:
 */
@Data
public class AfterOrderVo {
    private String sumCount;
    private Integer city;
    private String cityName;
    private String date;
    private String pointName;
    private Integer numCount;

    private String avg;
}
