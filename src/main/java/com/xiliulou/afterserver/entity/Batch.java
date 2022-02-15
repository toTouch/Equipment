package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * (Batch)实体类
 *
 * @author Eclair
 * @since 2021-08-16 15:50:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_batch")
public class Batch {

    private Long id;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 批次类型
     */
    private Integer type;
    /**
     * 产品型号
     */
    private Long modelId;
    /**
     * 产品类型 M主柜 V副柜
     */
    private String productType;
    /**
     * 产品数量
     */
    private Integer productNum;
    /**
     * 工厂
     */
    private Long supplierId;
    /**
     * 描述
     */
    private String represent;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 创建时间
     */
    private Long createTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    public static  final Integer TYPE_STOCK = 1;
    public static  final Integer TYPE_ORDER = 2;


    @TableField(exist = false)
    private String fileStr;
    @TableField(exist = false)
    private String productFileName;
    @TableField(exist = false)
    private List<ProductFile> productFileList;
    @TableField(exist = false)
    private Long fileId;
    @TableField(exist = false)
    private String modelName;
    @TableField(exist = false)
    private String supplierName;
}
