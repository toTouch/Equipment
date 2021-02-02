package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 16:27
 **/
@Data
public class IndexDataVo {
    //总运费
    private BigDecimal deliverCostAmount;
    //总工单运费
    private BigDecimal workOrderCostAmount;
    //柜机总数
    private Long cabinetAmount;
    //格子总数
    private Long boxAmount;
    //总成本
    private BigDecimal AllCostAmount;
    //单格口费用
    private BigDecimal singleBoxCostAmount;
}
