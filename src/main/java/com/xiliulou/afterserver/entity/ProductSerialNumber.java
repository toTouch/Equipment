package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 产品序列号表
 * @author: Mr.YG
 * @create: 2021-02-02 14:14
 **/
@Data
@TableName("product_serial_number")
public class ProductSerialNumber {
    private Long id;

    private String serialNumber;
    private Long productId;
    private Long createTime;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer setNo;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long pointId;
    private BigDecimal price;
    private String remarks;
    /**
     *  质保期（1年，2年，3年）
     */
    private String guaranteePeriod;
    /**
     * 保质开始时间
     */
    private Long guaranteePeriodStartTime;
    /**
     * 工厂（在供应商里面选择）
     */
    private String factory;


    //批次
    private String batch;


    /**
     * 0:生产中（刚刚创建）                    工厂地址
     * 1:运输中  （发货管理）                  对应物流的收货地址
     * 2:已收货   （发货管理更改状态） 对应物流的收货地址
     * 3:使用中    （添加点位）                 点位地址
     * 4:拆机柜    （拆机工单）                 仓库地址
     * 5:已报废      （在产品列表修改状态）
     */
    private Integer status;

    public static final Integer PRODUCTION = 0;
    public static final Integer CARRIAGE = 1;
    public static final Integer RECEIVED = 2;
    public static final Integer IN_USE = 3;
    public static final Integer DISASSEMBLE = 4;
    public static final Integer TO_BE_REPAIRED = 3;
    public static final Integer WRITTEN_OFF = 5;


}
