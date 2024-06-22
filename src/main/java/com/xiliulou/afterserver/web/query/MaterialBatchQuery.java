package com.xiliulou.afterserver.web.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * (MaterialBatch)实体类
 *
 * @author zhangbozhi
 * @since 2024-06-19 15:06:15
 */
public class MaterialBatchQuery implements Serializable {
    
    private static final long serialVersionUID = 951731148988636592L;
    
    /**
     * 物料批次号
     */
    private String materialBatchNo;
    
    /**
     * 物料编号id(物料型号编号id)
     */
    private Long materialId;
    
    /**
     * 物料编号sn(物料型号编号sn)
     */
    // @TableField(exist = false)
    private String sn;
    
    
    /**
     * 物料型号名称
     */
    // @TableField(exist = false)
    private String materialTypeName;
    
    /**
     * 供应商id
     */
    private Long supplierId;
    
    /**
     * 物料数量
     */
    private Integer materialCount;
    
    /**
     * 不合格物料数
     */
    private Integer unqualifiedCount;
    
    /**
     * 合格物料数
     */
    private Integer qualifiedCount;
    
    /**
     * 批次备注
     */
    private String materialBatchRemark;
    
    
    
    /**
     * 所属工厂
     */
    private Long tenantId;
    
    /**
     * 质检报告链接
     */
    private String qualityReportUrl;
    
    private Integer id;
    
    
}

