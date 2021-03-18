package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 产品型号表
 * @author: Mr.YG
 * @create: 2021-01-28 17:03
 **/
@Data
@TableName("product")
public class Product {
    private Long id;
    private String name;
    private Integer type;
    private String code;
    private Integer boxNumber;
    private Long createTime;
    private String info;
    private String state;//价格
    private BigDecimal price;//价格


}
