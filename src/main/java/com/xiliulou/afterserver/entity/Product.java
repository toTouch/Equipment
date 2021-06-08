package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 产品型号表
 * @author: Mr.YG
 * @create: 2021-01-28 17:03
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    // 1.主柜 2.副柜
    private Integer type;
    private String code;
    private Integer boxNumber;
    private Long createTime;
    private String info;
    private String state;//产品状态
    private BigDecimal price;//价格
    private String remarks;//备注

    //批次
    private String batch;

    //附件
    @TableField(exist = false)
    private String accessory;

    public static final Integer MAIN_LOCKER = 1;
    public static final Integer VICE_LOCKER = 2;

}
