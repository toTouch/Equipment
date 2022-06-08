package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/7 17:14
 * @mood
 */
@Data
public class AuditGroupVo {
    private Long id;
    /**
     * 分组名
     */
    private String name;
    /**
     * 组件jds  (json:['1'])
     */
    private String entryIds;
    /**
     *分组流程顺序
     */
    private BigDecimal sort;
    /**
     * 页面id
     */
    private Long processId;
    /**
     * status 0未完成 1 当前 2已完成
     */
    private Integer status;

    public static final  Integer STATUS_UNFINISHED = 0;
    public static final  Integer STATUS_EXECUTING = 1;
    public static final  Integer STATUS_FINISHED = 2;
}
