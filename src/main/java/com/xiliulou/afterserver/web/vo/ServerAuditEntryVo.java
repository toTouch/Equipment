package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/6/20 14:33
 * @mood
 */
@Data
public class ServerAuditEntryVo {
    private Long id;
    /**
     * 组件名
     */
    private String name;
    /**
     * 组件type：1单选 2多选 3文本 4图片
     */
    private Integer type;
    /**
     * 正则
     */
    private String jsonContent;
    /**
     * 保留字段
     */
    private String jsonRoot;
    /**
     * 排序
     */
    private BigDecimal sort;
    /**
     * 是否必须 0否1是
     */
    private Integer required;
    /**
     * 图片
     */
    private String photo;
    /**
     * ossUrlMap
     */
    private List<Map<String, String>> ossUrlList;
}
