package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/8 10:36
 * @mood
 */
@Data
public class KeyProcessAuditEntryVo {
    private Long id;
    private String name;
    private Integer type;
    private String jsonContent;
    private String jsonRoot;
    private BigDecimal sort;
    private Integer required;
    private String valueId;
    private String value;
}
