package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2021/9/17 18:09
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("inventory_flow_bill")
public class InventoryFlowBill {
    private Long id;

    private String no;

    private Long type;

    private String markNum;

    private String surplusNum;

    private Long createTime;

    private Long wid;
    /**
     * 销售出货
     */
    public static final Long TYPE_SALES_DELIVERY = 1L;
    /**
     * 领用出货
     */
    public static final Long TYPE_TAKE_DELIVERY = 2L;
    /**
     * 调拨出货
     */
    public static final Long TYPE_CALL_DELIVERY = 3L;
    /**
     *采购入库
     */
    public static final Long TYPE_PURCHASE_WAREHOUSING = 4L;
    /**
     * 归还入库
     */
    public static final Long TYPE_RETURN_WAREHOUSING = 5L;
    /**
     * 调拨入库
     */
    public static final Long TYPE_CALL_WAREHOUSING = 6L;
}
