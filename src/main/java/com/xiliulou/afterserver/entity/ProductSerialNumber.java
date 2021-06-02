package com.xiliulou.afterserver.entity;

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
    private Integer setNo;
    private Long pointId;
    private BigDecimal price;
    private String remarks;

//    0：【未使用】1：【使用中】2：【拆机柜】3：【待维修】
    private Integer status;

    public static final Integer UNUSED = 0;
    public static final Integer IN_USE = 1;
    public static final Integer DISASSEMBLE = 2;
    public static final Integer TO_BE_REPAIRED = 3;
}
