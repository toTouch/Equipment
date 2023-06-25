package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 压测记录表
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_compression_record")
public class CompressionRecord {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 资产编码id
     */
    private Long pid;
    /**
     * 资产编码
     */
    private String no;


    private Long createTime;
    private Long updateTime;

    /**
     * 测试结果 0--失败 1--通过 2--压测中
     */
    private Integer testResult;
    /**
     * 测试结果文件
     */
    private String testFile;
    /**
     * 测试格挡文件
     */
    private String testBoxfile;

    private String testType;

    private Long testStartTime;

    private Long testEndTime;

    private String testMsg;

    private String errorMessage;

}


