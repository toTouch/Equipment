package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (ProductNew)实体类
 *
 * @author Eclair
 * @since 2021-08-17 10:29:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_product_new")
public class ProductNew {

    private Long id;
    /**
     * 产品编号（纯数字）
     */
    private Long no;
    /**
     * 型号ID
     */
    private Long modelId;
    /**
     * 产品批次ID
     */
    private Long batchId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 质保开始时间
     */
    private Long expirationStartTime;
    /**
     * 保质期年份
     */
    private Integer years;
    /**
     * 质保结束时间
     */
    private Long expirationEndTime;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 删除状态 0正常 1 删除
     */
    private Integer delFlag;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;


    //左区间
    @TableField(exist = false)
    private Long leftInterval;
    //右区间
    @TableField(exist = false)
    private Long rightInterval;

    @TableField(exist = false)
    private String modelName;
    @TableField(exist = false)
    private String batchName;


}
