package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/6/8 10:36
 * @mood
 */
@Data
public class KeyProcessAuditEntryVo {
    /**
     * ID
     * */
    private Long id;
    /**
     * 组件名
     * */
    private String name;
    /**
     * 组件类型
     * */
    private Integer type;
    /**
     * 正则
     * */
    private String jsonContent;
    /**
     * 对组件的限制
     * */
    private String jsonRoot;
    /**
     * 排序
     * */
    private BigDecimal sort;
    /**
     * 是否是必填
     * */
    private Integer required;
    /**
     *
     * */
    private Long valueId;
    /**
     * 组件值
     * */
    private String value;
    /**
     * ossURL
     * key:url value:主键值
     */
    private Map<String, String> ossUrlMap;
}
