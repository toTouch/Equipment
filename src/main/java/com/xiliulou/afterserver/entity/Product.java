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
    //产品系列
    // 取餐柜 1、餐厅柜 2、换电柜 3、充电柜 4、寄存柜 5、生鲜柜 6
    private Integer productSeries;
    /**
     * 购买类型 1--集采 2--非集采
     */
    private Integer buyType;

    public static final Integer MAIN_LOCKER = 1;
    public static final Integer VICE_LOCKER = 2;

    public static final Integer BUY_TYPE_CENTRALIZED = 1;
    public static final Integer BUY_TYPE_NOT_CENTRALIZED = 2;
}
