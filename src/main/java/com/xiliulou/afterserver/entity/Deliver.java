package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 16:58
 **/
@Data
@TableName("deliver")

public class Deliver {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String city;
    private String destination;
    private String bills;
    private BigDecimal deliverCost;
    private Long deliverTime;
    private Long createTime;


}
