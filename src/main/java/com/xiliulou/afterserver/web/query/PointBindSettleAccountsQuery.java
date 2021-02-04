package com.xiliulou.afterserver.web.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-04 16:25
 **/
@Data
public class PointBindSettleAccountsQuery {

    private Long pointId;
    private BigDecimal deviceAmount;
    private BigDecimal serverAmount;

}
