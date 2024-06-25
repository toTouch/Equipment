package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * (MaterialOperation)实体类
 *
 * @author zhangbozhi
 * @since 2024-06-23 22:21:41
 */
public class MaterialOperation implements Serializable {
    
    private static final long serialVersionUID = 770888308984313936L;
    
    private Integer id;
    /**
     * 物料id 物料表id
     */
    private Long materialId;
    
    /**
     * 操作时间
     */
    private Long operationTime;
    
    /**
     * 操作内容
     */
    private String operationContent;
    
    /**
     * 操作账号
     */
    private String operationAccount;
    
    
}

