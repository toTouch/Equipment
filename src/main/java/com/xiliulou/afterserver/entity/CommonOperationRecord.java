package com.xiliulou.afterserver.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * (CommonOperationRecord)实体类
 *
 * @author zhangbozhi
 * @since 2024-11-06 17:21:07
 */ public class CommonOperationRecord implements Serializable {
    
    private static final long serialVersionUID = -42462770026445228L;
    
    private Integer id;
    
    /**
     * 操作时间
     */
    @ExcelIgnore
    private Long operationTime;
    
    /**
     * 操作时间
     */
    private String operationTimeExcel;
    
    /**
     * 操作内容
     */
    private String operationContent;
    
    /**
     * 备注
     */
    private String remarks;
    
    /**
     * 操作账号
     */
    private String operationAccount;
    
    /**
     * 操作记录类型
     */
    @ExcelIgnore
    private String recordType;
    
    /**
     * 产品操作
     */
    public static final  String PRODUCT_NEW_OPERATION = "1";
}

