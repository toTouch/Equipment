package com.xiliulou.afterserver.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/6/10 9:41
 * @mood
 */
public class AuditProcessConstans {

    public static boolean getFixedgGroup(Long id) {
        return id < 300;
    }

    public static boolean getFixedgEntry(Long id) {
        return id < 300;
    }
}
