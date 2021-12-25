package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    /**
     * 工单类型 {@link WorkOrderType}
     */
    private String type;
    /**
     * 点位id {@link PointNew}
     */
    private Long pointId;
    /**
     * 备注信息
     */
    private String info;
    private BigDecimal fee;
    private Long thirdCompanyId;
    private Integer thirdCompanyType;
    private BigDecimal thirdCompanyPay;
    private String thirdReason;
    private Long workOrderReasonId;
    //服务商id
    private Long serverId;
    //服务商接单人
    private String processor;
    private Long processTime;
    private Integer status; //状态
    private String orderNo;  //工单编号·
    private Long createrId;
    private Long createTime;
    private String thirdCompanyName;
    private String serverName;
    private String product;
    private String code;
    //private Integer boxNumber;
    private Integer warehourseId;
    private String reason;
    /**
     * 结算方式 1--月结 2--现结
     */
    private Integer paymentMethod;
    /**
     * 第三方结算状态 无需结算 1，未结算 2，已结算 3
     */
    private Integer thirdPaymentStatus;

    /**
     * 第三方结算状态 无需结算 1，未结算 2，已结算 3
     */
    public static final Integer THIRD_PAYMENT_UNWANTED = 1;
    public static final Integer THIRD_PAYMENT_UNFINISHED = 2;
    public static final Integer THIRD_PAYMENT_FINISHED = 3;

    /**
     * 结算方式 1--月结 2--现结
     */
    public static final Integer PAYMENT_METHOD_MONTHLY = 1;
    public static final Integer PAYMENT_METHOD_NOW = 2;

    //从什么地方转移到什么地方
    //起点
    private Long transferSourcePointId;
    //终点
    private Long transferDestinationPointId;

    private String describeinfo;
    //客户
    public static final Integer COMPANY_TYPE_CUSTOMER = 1;
    /**
     *    供应商
     */
    public static final Integer COMPANY_TYPE_SUPPLIER = 2;

    // 0：未完成1;待处理2:已处理3:待分析4：已完结5：已完成
    public static final Integer STATUS_UNFINISHED = 0;
    public static final Integer STATUS_INIT = 1;
    public static final Integer STATUS_PROCESSING = 2;
    public static final Integer STATUS_ANALYSE = 3;
    public static final Integer STATUS_FINISHED = 4;
    public static final Integer STATUS_SUSPEND = 5;


    //工单类型
    //1.安装
    public static final Integer TYPE_INSTALL = 1;
    //2.派送
    public static final Integer TYPE_SEND = 2;
    //3.接电
    public static final Integer TYPE_CONTACT = 3;
    //4.移机
    public static final Integer TYPE_MOBLIE = 4;
    //5.售后
    public static final Integer TYPE_AFTER = 5;
    //6.归仓
    public static final Integer TYPE_BELONG_WAREHOUSE = 6;
    //7.派送安装
    public static final Integer TYPE_SEND_INSERT = 7;


    @TableField(exist = false)
    private String userName;
}
