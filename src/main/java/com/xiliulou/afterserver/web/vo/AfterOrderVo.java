package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Hardy
 * @date 2021/6/15 0015 17:11
 * @Description:
 */
@Data
public class AfterOrderVo {
    private Double sumCount;
    private Integer city;
    private String cityName;
    private String date;
    private Long pointId;
    private String pointName;
    private Integer numCount;

    private Integer type;
    private String typeName;

    private String dateTime;
}
