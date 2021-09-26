package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author zgw
 * @date 2021/9/18 14:29
 * @mood
 */
@Data
public class InventoryFlowBillVo {
    private Long id;

    private String no;

    private Long type;

    private String markNum;

    private String surplusNum;

    private Long createTime;

    private Long wid;

    private String productName;

    private  String createTimeStr;

    private String typeName;
}
