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
    private String sumCount;
    private Integer city;
    private String cityName;
    private String date;
    private String pointName;
    private Integer numCount;

    private Integer type;
    private String typeName;
    private BigDecimal avg;
}
