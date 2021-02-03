package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-03 16:27
 **/
@Data
public class ReconciliationSummaryVo {
    private Long createTimeStart;
    private Long createTimeEnd;
    private String companyName;
    private BigDecimal allAmount;
    private Long companyId;
    private Integer companyType;
}
