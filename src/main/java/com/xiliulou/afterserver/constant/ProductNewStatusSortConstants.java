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
    private static final Map<Integer, String> STATUS_NAME_MAPS = Maps.newHashMap();
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
     *前置检验合格
     */
    public static final Integer STATUS_PRE_DETECTION = 7;
    public static final Double STATUS_PRE_DETECTION_VALUE = 1.00;
    /**
     * 后置检验合格
     */
    public static final Integer STATUS_POST_DETECTION = 8;
    public static final Double STATUS_POST_DETECTION_VALUE = 3.00;

    static{
        STATUS_VALUE_MAPS.put(STATUS_PRODUCTION, STATUS_PRODUCTION_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_SHIPPED, STATUS_SHIPPED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_RECEIVED, STATUS_RECEIVED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_USE, STATUS_USE_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_DISMANTLE, STATUS_DISMANTLE_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_SCRAPPED, STATUS_SCRAPPED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_TESTED, STATUS_TESTED_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_PRE_DETECTION, STATUS_PRE_DETECTION_VALUE);
        STATUS_VALUE_MAPS.put(STATUS_POST_DETECTION, STATUS_POST_DETECTION_VALUE);
        
        STATUS_NAME_MAPS.put(STATUS_PRODUCTION, "生产中");
        STATUS_NAME_MAPS.put(STATUS_SHIPPED, "已发货");
        STATUS_NAME_MAPS.put(STATUS_RECEIVED, "已收货");
        STATUS_NAME_MAPS.put(STATUS_USE, "使用中");
        STATUS_NAME_MAPS.put(STATUS_DISMANTLE, "拆机柜");
        STATUS_NAME_MAPS.put(STATUS_SCRAPPED, "已报废");
        STATUS_NAME_MAPS.put(STATUS_TESTED, "已测试");
        STATUS_NAME_MAPS.put(STATUS_PRE_DETECTION, "前置检验合格");
        STATUS_NAME_MAPS.put(STATUS_POST_DETECTION, "后置检验合格");
    }

    public static Double acquireStatusValue(Integer status) {
        return STATUS_VALUE_MAPS.get(status);
    }
    public static String acquireStatusName(Integer status) {
        return STATUS_NAME_MAPS.get(status);
    }
}
