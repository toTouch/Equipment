package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author zgw
 * @date 2021/9/18 14:21
 * @mood
 */
@Data
public class InventoryFlowBillQuery {
    private Long id;

    private String no;

    private Long type;

    private String markNum;

    private String surplusNum;

    private Long createTime;

    private Long wid;

    private Long createTimeStart;

    private Long createTimeEnd;
}
