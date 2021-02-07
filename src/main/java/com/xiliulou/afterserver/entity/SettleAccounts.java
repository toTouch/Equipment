package com.xiliulou.afterserver.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 结算表
 * @author: Mr.YG
 * @create: 2021-01-29 18:08
 **/
@Data
public class SettleAccounts {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal payRate;
    private BigDecimal payAmount;
    private BigDecimal unPayAmount;
    private BigDecimal billAmount;
    private BigDecimal unBillAmount;
    private String remark;
    private Long payTime;
    private Long createTime;

}
