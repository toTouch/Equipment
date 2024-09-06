package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 物料导出配置表(ExportMaterialConfig)实体类
 *
 * @author zhangbozhi
 * @since 2024-08-30 11:21:06
 */
public class ExportMaterialConfig implements Serializable {
    
    private static final long serialVersionUID = 224791474276661788L;
    
    private Integer id;
    
    /**
     * 物料种类SN(PN)
     */
    private String pn;
    
    /**
     * 物料名称
     */
    private String name;
    
    /**
     * 物料别名
     */
    private String materialAlias;
    
    /**
     * 关联状态(0：Atmel  1：IMEL code)
     */
    private Integer associationStatus;
    
    /**
     * 顺序
     */
    private Integer sort;
    
    /**
     * 工厂id
     */
    private Long supplierId;
    
    public static final String EXPORT_MATERIAL_CONFIG_CALL_BACK = "ExportMaterialConfig:";
    public static final Integer ATMEL = 0;
    public static final Integer IMEL = 1;
}

