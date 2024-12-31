package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
     * 物料种类ID
     */
    private Long materialId;
    
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
    @TableField(exist = false)
    private List<Integer> associationStatus;
    
    @TableField("association_status")
    private String materialAssociation;
    
    /**
     * 顺序
     */
    private Integer sort;
    
    /**
     * 工厂id
     */
    private Long supplierId;
    
    /**
     * 模版类型(1：模版1  2：模版2)
     */
    @TableField("stencil_ids")
    private String stencilIds;
    
    @TableField(exist = false)
    private List<Integer> stencilIdList;
    
    
    public static final String EXPORT_MATERIAL_CONFIG_CALL_BACK = "ExportMaterialConfig:";
    public static final Integer ATMEL = 0;
    public static final Integer IMEL = 1;
    public static final Integer TEST_TIME = 2;
    
    public static final String STENCIL_ONE  = "1";
    public static final String STENCIL_TWO = "2";
}

