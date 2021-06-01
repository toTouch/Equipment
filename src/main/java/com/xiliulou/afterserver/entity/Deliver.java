package com.xiliulou.afterserver.entity;

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
    private Long phone;//客户电话
    private String city;
    private String province;
    private String destination;//终点
    private String remark;
    private BigDecimal deliverCost;
    private Long deliverTime;
    private Long createTime;
    private String expressCompany;
    private String expressNo;
    private Long pointId;
    private Integer state;//物流状态
    private String product;//型号
    private String quantity;//数量
}
