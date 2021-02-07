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
}
