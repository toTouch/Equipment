package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 发货
 * @author: Mr.YG
 * @create: 2021-01-28 16:58
 **/
@Data
@TableName("deliver")
public class Deliver {

    private Long id;
    private Long customerId;
    private String city;
    private String province;
    private String destination;//终点
    private String bills;
    private BigDecimal deliverCost;
    private Long deliverTime;
    private Long createTime;
    private String expressCompany;
    private String expressNo;
    private Long pointId;
}
