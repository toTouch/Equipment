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
    //客户id
    private Long customerId;
    //总金额
    private BigDecimal totalAmount;


    private String remark;
    private Long payTime;
    private Long createTime;


    //以下字段 暂时不做
    //付款比例
    private BigDecimal payRate;
    //付款金额
    private BigDecimal payAmount;
    //未付款金额
    private BigDecimal unPayAmount;
    //
    private BigDecimal billAmount;
    private BigDecimal unBillAmount;

}
