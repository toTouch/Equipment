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
    private String name;
    private Long customerId;
    private Integer primaryCabinetNo;
    private String serialNumber;
    private String cardNo;//物联网卡
    private String remark;
    private Long setTime;
    private Long deliverTime;
    private Long createTime;

}
