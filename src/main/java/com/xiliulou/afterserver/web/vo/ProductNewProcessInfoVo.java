package com.xiliulou.afterserver.web.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/7 9:47
 * @mood
 */
@Data
public class ProductNewProcessInfoVo {
    private Long id;
    /**
     * 产品编号
     */
    private String no;
    /**
     * 产品批次ID
     */
    private Long batchId;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 柜机状态
     */
    private String productStatus;
    /**
     * 大工序流程
     */
    private List<AuditProcessVo> auditProcessList;
//    /**
//     * 发货状态 0置灰 2绿灯
//     */
//    private Integer deliverStatus;

    public static final Integer STATUS_UN_FINISHED = 0;
    public static final Integer STATUS_FINISHED = 2;
}
