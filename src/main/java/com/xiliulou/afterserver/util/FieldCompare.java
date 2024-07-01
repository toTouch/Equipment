package com.xiliulou.afterserver.util;

import com.xiliulou.afterserver.entity.Material;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.xiliulou.afterserver.entity.Material.PASSING;
import static com.xiliulou.afterserver.entity.Material.TO_BE_INSPECTED;
import static com.xiliulou.afterserver.entity.Material.UN_PASSING;

public class FieldCompare {
    
    public static List<String> materialFieldCompare(Material newObject, Material oldObject) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(UN_PASSING, "不合格");
        map.put(PASSING, "合格");
        map.put(TO_BE_INSPECTED, "待检");
        List<String> logList = new ArrayList<>();
        if (Objects.isNull(oldObject)) {
            return logList;
        }
        
        normalizedObject(newObject, oldObject);
        
        if (!Objects.equals(newObject.getProductNo(), oldObject.getProductNo())) {
            logList.add(formatLogString("资产编码", oldObject.getProductNo(), newObject.getProductNo()));
        }
        
        if (!Objects.equals(newObject.getRemark(), oldObject.getRemark())) {
            logList.add(formatLogString("备注", oldObject.getRemark(), newObject.getRemark()));
        }
        
        if (!Objects.equals(newObject.getMaterialState(), oldObject.getMaterialState())) {
            if (Objects.isNull(newObject.getMaterialState())) {
                newObject.setMaterialState(TO_BE_INSPECTED);
            }
            if (Objects.isNull(oldObject.getMaterialState())) {
                oldObject.setMaterialState(TO_BE_INSPECTED);
            }
            logList.add(formatLogString("物料状态", map.get(oldObject.getMaterialState()), map.get(newObject.getMaterialState())));
        }
        
        return logList;
    }
    
    private static void normalizedObject(Material newObject, Material oldObject) {
        if (StringUtils.isEmpty(newObject.getProductNo())) {
            newObject.setProductNo("");
        }
        if (StringUtils.isEmpty(oldObject.getProductNo())) {
            oldObject.setProductNo("");
        }
        if (StringUtils.isEmpty(newObject.getRemark())) {
            newObject.setRemark("");
        }
        if (StringUtils.isEmpty(oldObject.getRemark())) {
            oldObject.setRemark("");
        }
        if (Objects.isNull(newObject.getMaterialState())) {
            newObject.setMaterialState(TO_BE_INSPECTED);
        }
        if (Objects.isNull(oldObject.getMaterialState())) {
            oldObject.setMaterialState(TO_BE_INSPECTED);
        }
    }
    
    private static String formatLogString(String fieldName, String oldValue, String newValue) {
        return fieldName + "由：[\"" + getLogString(oldValue) + "\"]更改为了：[\"" + getLogString(newValue) + "\"]";
    }
    
    private static String getLogString(String string) {
        return StringUtils.isEmpty(string) ? "空" : string;
    }
   
}
