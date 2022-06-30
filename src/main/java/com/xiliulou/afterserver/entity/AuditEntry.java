package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zgw
 * 控件表（t_audit_entry）
 * @date 2022/6/6 16:20
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_audit_entry")
public class AuditEntry {
    private Long id;
    /**
     * 控件名字
     */
    private String name;
    /**
     * 控件类型：1单选 2多选 3文本 4图片
     */
    private Integer type;
    /**
     * 正则
     */
    private String jsonContent;
    /**
     * 保留字段
     */
    private String jsonRoot;
    /**
     * 排序
     */
    private BigDecimal sort;
    /**
     * 是否必填 0非必填 1必填
     */
    private Integer required;

    private Integer delFlag;
    private Long updateTime;
    private Long createTime;
    private Long groupId;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    public static final Integer NOT_REQUIRED = 0;
    public static final Integer REQUIRED = 1;
    //控件类型：1单选 2多选 3文本 4图片
    public static final Integer TYPE_RADIO = 1;
    public static final Integer TYPE_CHECKBOX = 2;
    public static final Integer TYPE_TEXT = 3;
    public static final Integer TYPE_PHOTO = 4;
}
