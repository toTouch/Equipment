package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:点位
 * @author: Mr.YG
 * @create: 2021-01-28 17:01
 **/
@Data
@TableName("point")
public class Point {
    private Long id;
    private String city;

    private String province;

    private String name;
    private Long customerId;
    private Integer primaryCabinetNo;
    private String serialNumber;
    private String cardNo;//物联网卡
    private String remark;
    private Long setTime;
    private Long deliverTime;
    private Long createTime;
    private Integer status;
    private Integer canopyCount;
    private Integer primaryCabinetCount;
    private Integer deputyCabinetCount;
    private BigDecimal deviceAmount;//设备费用
    private BigDecimal paiedAmount;//已支付费用
    private BigDecimal serverAmount;//点位费
    private Integer boxNumber;
    //正常
    public static final Integer STATUS_NORMAL = 1;
    //已移机
    public static final Integer STATUS_TRANSFER = 2;
    //拆机
    public static final Integer STATUS_REMOVE = 3;

}
