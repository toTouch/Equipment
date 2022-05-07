package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/5/6 17:15
 * @mood
 */
@Data
public class PointNewMapStatisticsVo {
    private String pointName;
    private Integer productSeries;
    private BigDecimal coordX;
    private BigDecimal coordY;
}
