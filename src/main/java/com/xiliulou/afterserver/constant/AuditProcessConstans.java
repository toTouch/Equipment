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
     * 分组映射
     */
    public static final Map<Long, String> GROUP_MAPS = new HashMap<>();
    /**
     * 组件映射
     */
    public static final Map<Long, String> ENTRY_MAPS = new HashMap<>();

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
     * 颜色
     */
    public static final Long PRODUCT_COLOR_AUDIT_ENTRY = 4L;
    /**
     * 外观
     */
    public static final Long PRODUCT_SURFACE_AUDIT_ENTRY = 5L;

    /**
     * 保存
     */
    public static final Long SAVE_AUDIT_ENTRY = 6L;
    static{
        GROUP_MAPS.put(ENTRY_INFO_AUDIT_GROUP, "OK");

        ENTRY_MAPS.put(CAMERA_SN_AUDIT_ENTRY, "OK");
        ENTRY_MAPS.put(CAMERA_IOT_AUDIT_ENTRY, "OK");
        ENTRY_MAPS.put(PRODUCT_IOT_AUDIT_ENTRY, "OK");
        ENTRY_MAPS.put(PRODUCT_COLOR_AUDIT_ENTRY, "OK");
        ENTRY_MAPS.put(PRODUCT_SURFACE_AUDIT_ENTRY, "OK");
        ENTRY_MAPS.put(SAVE_AUDIT_ENTRY, "OK");
    }

    public static String getFixedgGroup(Long key) {
        return GROUP_MAPS.get(key);
    }

    public static String getFixedgEntry(Long key) {
        return ENTRY_MAPS.get(key);
    }
}
