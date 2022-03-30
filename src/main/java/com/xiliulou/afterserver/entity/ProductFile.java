package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description: 产品附件表
 * @author: Mr.YG
 * @create: 2021-01-28 17:03
 **/
@Data
@TableName("product_file")
public class ProductFile implements Serializable {

    @TableId
    private Long id;

    /**
     * 批次主键id
     */
    private Long productId;

    private String fileStr;

    private String productFileName;
}
