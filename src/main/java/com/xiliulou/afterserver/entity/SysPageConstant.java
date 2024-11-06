package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 系统页面常量表(SysPageConstant)实体类
 *
 * @author zhangbozhi
 * @since 2024-11-01 17:11:47
 */ public class SysPageConstant implements Serializable {
    
    private static final long serialVersionUID = -28510045092576929L;
    
    /**
     * 键
     */
    private String constantKey;
    
    /**
     * 常量的名称（例，有效，无效）
     */
    private String constantName;
    
    /**
     * 常量的类型是什么(例，性别，状态)
     */
    private String constantType;
    
    /**
     * 代表该字段是否有效，默认1有效0无效
     */
    private String status;
    
    /**
     * 排序
     */
    private Integer orderby;
    
    /**
     * 说明该值是代表什么
     */
    private String comment;
    
    
}

