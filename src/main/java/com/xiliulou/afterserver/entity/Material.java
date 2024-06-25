package com.xiliulou.afterserver.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiliulou.afterserver.plugin.FieldCompare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 物料追溯表(Material)实体类
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_material_traceability")
public class Material implements Serializable {
    
    private static final long serialVersionUID = 891201978381378054L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 物料sn
     */
    @ExcelProperty(value = "物料编码")
    private String materialSn;
    
    /**
     * 产品编号/资产编码
     */
    @ExcelProperty(value = "资产编码")
    @FieldCompare(chineseName = "资产编码")
    private String productNo;
    
    private String imei;
    
    private String atmelID;
    
    private Long testTime;
    
    /**
     * 备注
     */
    @FieldCompare(chineseName = "备注")
    private String remark;
    
    /**
     * 物料批次No
     */
    private String materialBatchNo;
    
    /**
     * 物料型号id
     */
    private Long materialId;
    
    /**
     * 物料状态(0-不合格，1-合格)
     */
    @FieldCompare(chineseName = "物料状态", properties = "0:不合格，1:合格")
    private Integer materialState;
    
    /**
     * 删除标记(0-未删除，1-已删除)
     */
    @ExcelIgnore
    private Integer delFlag;
    
    /**
     * 未删除
     */
    public static final Integer UN_DEL_FLAG = 0;
    
    /**
     * 已删除
     */
    public static final Integer DEL_FLAG = 1;
    /**
     * 不合规
     */
    public static final Integer UN_PASSING = 0;
    /**
     * 待检
     */
    public static final Integer TO_BE_INSPECTED = null;
    
    /**
     * 合格
     */
    public static final Integer PASSING = 1;
    
    /**
     * 绑定标记(0-未绑定，1-已绑定)
     */
    @ExcelIgnore
    private Integer bindingStatus;
    
    /**
     * 未绑定
     */
    public static final Integer UN_BINDING = 0;
    
    /**
     * 已绑定
     */
    public static final Integer BINDING = 1;
    
    /**
     * 创建时间
     */
    @ExcelProperty(value = "绑定时间")
    private Long createTime;
    
    /**
     * 更新时间
     */
    @ExcelIgnore
    private Long updateTime;
    
    /**
     * 租户ID
     */
    @ExcelIgnore
    private Long tenantId;
    
    @TableField(exist = false)
    @ExcelIgnore
    private Long startTime;
    
    @TableField(exist = false)
    @ExcelIgnore
    private Long endTime;
    
    @TableField(exist = false)
    @ExcelIgnore
    private String name;
    
    @TableField(exist = false)
    @ExcelIgnore
    private String sn;
    
    private String materialBatchId;
    
}

