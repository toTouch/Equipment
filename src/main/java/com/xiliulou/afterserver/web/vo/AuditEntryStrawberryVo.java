package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/10 15:05
 * @mood
 */
@Data
public class AuditEntryStrawberryVo {
    private Long id;
    /**
     * 控件名字
     */
    private String name;
    /**
     * 控件类型：1单选 2多选 3文本 4图片
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
     * 是否必填 0非必填 1必填
     */
    private Integer required;
}
