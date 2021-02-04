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
    private Integer thirdCompanyId;
    private Integer thirdCompanyType;
    private BigDecimal thirdCompanyPay;
    private Long workOrderReasonId;
    //服务商id
    private Long serverId;
    //服务商接单人
    private String processor;
    private Long processTime;
    private Integer status;
    private String orderNo;
    private Long createrId;
    private Long createTime;
    private String thirdCompanyName;
    private String serverName;
    //客户
    public static final Integer COMPANY_TYPE_CUSTOMER = 1;
    //供应商
    public static final Integer COMPANY_TYPE_SUPPLIER = 2;

    //待处理
    public static final Integer STATUS_INIT = 1;
    //处理中
    public static final Integer STATUS_PROCESSING = 2;
    //已完成
    public static final Integer STATUS_FINISHED = 3;


}
