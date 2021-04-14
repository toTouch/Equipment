package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 工单
 * @author: Mr.YG
 * @create: 2021-02-01 09:21
 **/
@Data
@TableName("work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer type;
    private Long pointId;
    private String info;
    private BigDecimal fee;
    private Long thirdCompanyId;
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
    private Long createId;
    private Long createTime;
    private String thirdCompanyName;
    private String serverName;
    private String product;
    private String code;
    private Integer boxNumber;


    //从什么地方转移到什么地方
    //起点
    private Long transferSourcePointId;
    //终点
    private Long transferDestinationPointId;
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
