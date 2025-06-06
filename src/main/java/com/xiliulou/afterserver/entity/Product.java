package com.xiliulou.afterserver.entity;

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
    
    private String state;// 产品状态
    
    private BigDecimal price;// 价格
    
    private String remarks;// 备注
    
    // 产品系列
    // 取餐柜 1、餐厅柜 2、换电柜 3、充电柜 4、寄存柜 5、生鲜柜 6
    private Integer productSeries;
    
    /**
     * 购买类型 1--集采 2--非集采
     */
    private Integer buyType;
    
    /**
     * 是否有屏  0无 1有
     */
    private Integer hasScreen;
    
    /*
     * 上下架状态 0-上架、1-下架'
     */
    private Integer shelfStatus;
    
    
    /**
     * 消防类型 0-水消防 1-气溶胶消防
     */
    private Integer fireFightingType;
    
    public static final Integer MAIN_LOCKER = 1;
    
    public static final Integer VICE_LOCKER = 2;
    
    public static final Integer BUY_TYPE_CENTRALIZED = 1;
    
    public static final Integer BUY_TYPE_NOT_CENTRALIZED = 2;
    
    
    public static final Integer NO_SCREEN = 0;
    
    public static final Integer SHELF_STATUS = 0;
    
    public static final Integer HAS_SCREEN = 1;
    
    // 定义取餐柜对应的常量值
    public static final Integer TAKEOUT_CABINET = 1;
    
    // 定义餐厅柜对应的常量值
    public static final Integer RESTAURANT_CABINET = 2;
    
    /**
     *  换电柜
     */
    public static final Integer BATTERY_REPLACEMENT_CABINET = 3;
    
    // 定义充电柜对应的常量值
    public static final Integer CHARGING_CABINET = 4;
    
    // 定义寄存柜对应的常量值
    public static final Integer STORAGE_CABINET = 5;
    
    // 定义生鲜柜对应的常量值
    public static final Integer FRESH_FOOD_CABINET = 6;
}
