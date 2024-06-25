package com.xiliulou.afterserver.util;

import com.xiliulou.afterserver.plugin.FieldCompare;
import org.apache.commons.lang3.StringUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializableFieldCompare {
    public static <T> List<String> compare (Class<T> type, T newObject, T  oldObject ) throws Exception {
        List<String> logList = new ArrayList<>();
        Class<?> newObj = newObject.getClass();
        Class<?> oldObj = oldObject.getClass();
        Field[] newFields = type.getDeclaredFields();
        for (int i = 0; i < newFields.length; i++) {
            FieldCompare newAnnotation = newFields[i].getAnnotation(FieldCompare.class);
            if(null != newAnnotation){
                PropertyDescriptor newPd = new PropertyDescriptor(newFields[i].getName(), newObj);
                Method getMethodNew = newPd.getReadMethod();
                Object oldVal = getMethodNew.invoke(oldObject);
                Object newVal = getMethodNew.invoke(newObject);
                boolean eq = false;
                if (oldVal instanceof String){
                    String s1 = String.valueOf(oldVal).trim();
                    String s2 = String.valueOf(newVal).trim();
                    eq = !s1.equals(s2);
                }else if(oldVal instanceof Integer){
                    int i1 = (Integer) oldVal;
                    int i2 = (Integer) newVal;
                    eq = i1 != i2;
                }else if(oldVal instanceof Double){
                    double d1 = (Double) oldVal;
                    double d2 = (Double) newVal;
                    eq = d1 != d2;
                }else if(oldVal instanceof BigDecimal){
                    BigDecimal b1 = (BigDecimal) oldVal;
                    BigDecimal b2 = (BigDecimal) newVal;
                    eq = b1.compareTo(b2) != 0;
                }else if(oldVal instanceof Long){
                    long l1 = (Long) oldVal;
                    long l2 = (Long) newVal;
                    eq = l1 != l2;
                }
                String s1 = oldVal == null ? "" : oldVal.toString().trim();
                String s2 = newVal == null ? "" : newVal.toString().trim();
                if (eq) {
                    Map<String, String> map = codeToNameMap(newAnnotation);
                    if (map.size() > 0) {
                        logList.add(newAnnotation.chineseName() + "由：[" + map.get(s1) + "]更改为了：[" + map.get(s2) + "]");
                    }else {
                        logList.add(newAnnotation.chineseName() + "由：[" + s1 + "]更改为了：[" + s2 + "]");
                    }
                }
            }
        }
        return logList;
    }

    private static Map<String,String> codeToNameMap(FieldCompare newAnnotation){
        Map<String,String> map = new HashMap<>();
        String properties = newAnnotation.properties();
        if(StringUtils.isNotBlank(properties)){
            String[] propertiesArr = properties.split(",");
            for (String propertie : propertiesArr) {
                String[] split = propertie.split(":");
                map.put(split[0],split[1]);
            }
        }
        return map;
    }
}
