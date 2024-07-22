package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    ;
    
    /**
     * 产品数量
     */
    private Integer productNum;
    
    /**
     * 未发货柜机数
     */
    private Integer notShipped;
    
    /**
     * 工厂
     */
    private Long supplierId;
    
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
    
    public static final Integer TYPE_STOCK = 1;
    
    public static final Integer TYPE_ORDER = 2;
    
    
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
    
    /**
     * 同步柜机SN
     */
    @TableField(exist = false)
    private String syncCabinetSn;
    
    // 换电柜类型(0:阿里云SaaS换电柜,1:api换电柜,3:华为云saas, 4: TCP柜机)
    private Integer batteryReplacementCabinetType;
    
    // 阿里云的SaaS换电柜服务类型
    public static final Integer ALIYUN_SaaS_ELECTRIC_SWAP_CABINET = 0;
    
    // API形式的换电柜服务
    public static final Integer API_ELECTRIC_SWAP_CABINET = 1;
    
    // 华为云的SaaS服务
    public static final Integer HUAWEI_CLOUD_SaaS = 3;
    // TCP 柜机
    public static final Integer TCP_ELECTRIC_SWAP_CABINET = 4;
    
    public static final Integer TRUE = 1;
    
    
    // 自定义设备名称
    @TableField(exist = false)
    private List<String> customDeviceNameList;
}
