package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.PriorityQueue;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 09:21
 **/
@Data
@TableName("work_order")
public class WorkOrder {
    private Long id;
    private Integer type;
    private Long pointId;
    private String info;
    private BigDecimal fee;
    private Integer companyId;
    private Integer companyType;
    private BigDecimal thirdPartyPay;
    private Long workOrderReasonId;
    private String processor;
    private Long processorTime;
    private Integer status;
    private String orderNo;
    private Long createrId;
    private Long createTime;

    //客户
    public static final Integer COMPANY_TYPE_CUSTOMER = 1;
    //供应商
    public static final Integer COMPANY_TYPE_SUPPLIER = 2;


    public static final Integer STATUS_INIT = 1;
    public static final Integer STATUS_PROCESSING = 2;
    public static final Integer STATUS_FINISHED = 3;


}
