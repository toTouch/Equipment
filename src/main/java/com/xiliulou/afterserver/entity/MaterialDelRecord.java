package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 物料删除追溯表(MaterialDelRecord)实体类
 *
 * @author zhangbozhi
 * @since 2024-06-27 20:44:33
 */ public class MaterialDelRecord implements Serializable {
    
    private static final long serialVersionUID = 964143018573603399L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 物料sn
     */
    private String materialSn;
    
    /**
     * 物料编号
     */
    private String materialNo;
    
    /**
     * 物料型号名称
     */
    private String materialName;
    
    /**
     * 操作账号
     */
    private String tenantName;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    
}

