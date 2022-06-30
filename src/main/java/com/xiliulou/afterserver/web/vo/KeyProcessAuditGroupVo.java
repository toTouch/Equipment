package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/8 10:29
 * @mood
 */
@Data
public class KeyProcessAuditGroupVo {
    private Long id;
    /**
     * 分组名
     */
    private String name;
    /**
     *分组流程顺序
     */
    private BigDecimal sort;
    /**
     * status 0未完成 1 当前 2已完成
     */
    private Integer status;
}
