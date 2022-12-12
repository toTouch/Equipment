package com.xiliulou.afterserver.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/6/10 9:41
 * @mood
 */
public class AuditProcessConstans {

    /**
     * 固定分组 : 信息录入
     */
    public static final Long ENTRY_INFO_AUDIT_GROUP = 1L;

    /**
     * 固定组件
     * 摄像头序列号
     */
    public static final Long CAMERA_SN_AUDIT_ENTRY = 1L;
    /**
     * 摄像头物联网卡号
     */
    public static final Long CAMERA_IOT_AUDIT_ENTRY = 2L;
    /**
     * 柜机物联网卡号
     */
    public static final Long PRODUCT_IOT_AUDIT_ENTRY = 3L;
    /**
     * 柜机颜色
     */
    public static final Long PRODUCT_COLOR_AUDIT_ENTRY = 4L;
    /**
     * 外观
     */
    public static final Long PRODUCT_SURFACE_AUDIT_ENTRY = 5L;

    /**
     * 摄像头序列号2 （双面柜有两个摄像头序列号）
     */
    public static final Long CAMERA_SN_AUDIT_ENTRY_TOW = 6L;

    /**
     *仓门颜色
     */
    public static final Long DOOR_COLOR_AUDIT_ENTRY = 7L;


    public static boolean getFixedgGroup(Long id) {
        return id < 300;
    }

    public static boolean getFixedgEntry(Long id) {
        return id < 300;
    }
}
