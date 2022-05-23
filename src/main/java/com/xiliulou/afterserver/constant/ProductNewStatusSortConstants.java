package com.xiliulou.afterserver.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zgw
 * @date 2022/5/23 16:08
 * @mood
 */
public class ProductNewStatusSortConstants {
    /**
     * 映射
     */
    private static final Map<Integer, Double> STATUS_VALUE_MAPS = Maps.newHashMap();
    /**
     * 生产中
     */
    public static final Integer STATUS_PRODUCTION = 0;
    public static final Double STATUS_PRODUCTION_VALUE = 0.00;
    /**
     *已发货
     */
    public static final Integer STATUS_SHIPPED = 1;
    public static final Double STATUS_SHIPPED_VALUE = 4.00;
    /**
     * 已收货
     */
    public static final Integer STATUS_RECEIVED = 2;
    public static final Double STATUS_RECEIVED_VALUE = 5.00;
    /**
     * 使用中
     */
    public static final Integer STATUS_USE = 3;
    public static final Double STATUS_USE_VALUE = 6.00;
    /**
     * 拆机柜
     */
    public static final Integer STATUS_DISMANTLE = 4;
    public static final Double STATUS_DISMANTLE_VALUE = 7.00;
    /**
     * 已报废
     */
    public static final Integer STATUS_SCRAPPED = 5;
    public static final Double STATUS_SCRAPPED_VALUE = 8.00;
    /**
     * 已测试
     */
    public static final Integer STATUS_TESTED = 6;
    public static final Double STATUS_TESTED_VALUE = 2.00;
    /**
     *电装检验合格
     */
    public static final Integer STATUS_ELECTRICAL_QUALIFIED = 7;
    public static final Double STATUS_ELECTRICAL_QUALIFIED_VALUE = 1.00;
    /**
     * 出货检验合格
     */
    public static final Integer STATUS_DELIVERY_QUALIFIED = 8;
    public static final Double STATUS_DELIVERY_QUALIFIED_VALUE = 3.00;

    static{
        STATUS_VALUE_MAPS.put(STATUS_PRODUCTION, STATUS_PRODUCTION_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_SHIPPED, STATUS_SHIPPED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_RECEIVED, STATUS_RECEIVED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_USE, STATUS_USE_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_DISMANTLE, STATUS_DISMANTLE_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_SCRAPPED, STATUS_SCRAPPED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_TESTED, STATUS_TESTED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_ELECTRICAL_QUALIFIED, STATUS_ELECTRICAL_QUALIFIED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_DELIVERY_QUALIFIED, STATUS_DELIVERY_QUALIFIED_VALUE);
    }

    public static Double acquireStatusValue(Integer status) {
        return STATUS_VALUE_MAPS.get(status);
    }
}
