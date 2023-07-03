package com.xiliulou.afterserver.web.vo;

import lombok.Data;

@Data
public class ProductNewDeliverVo {

    private Long id;
    /**
     * 产品编号
     */
    private String no;
    /**
     * 型号名称
     */
    private String productName;
    /**
     * 产品批次no
     */
    private String batchNo;
    /**
     * 物联网productKey
     */
    private String productKey;
    /**
     * 物联网deviceName
     */
    private String deviceName;
    /**
     * 运营商名称
     */
    private String tenantName;

    /**
     * 三元组是否已使用 0使用 1 未使用
     */
    private Integer isUse;
    //三元组已使用
    public static final Integer IS_USE =0;
    //三元组未使用
    public static final Integer NOT_USE =1;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 发货时间
     */
    private Long deliverTime;

    /**
     * 测试结果 0--失败 1--通过 2--压测中
     */
    private Integer testResult;

    /**
     * 压测结束时间
     */
    private Long testEndTime;

}
