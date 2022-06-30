package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/20 11:10
 * @mood
 */
@Data
@TableName("t_server_audit_entry")
public class ServerAuditEntry {
    private Long id;
    /**
     * 组件名
     */
    private String name;
    /**
     * 组件type：1单选 2多选 3文本 4图片
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
     * 是否必须 0否1是
     */
    private Integer required;
    /**
     * 图片
     */
    private String photo;

    private Long updateTime;
    private Integer delFlag;
    private Long createTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    public static final Integer TYPE_RADIO = 1;
    public static final Integer TYPE_CHECKBOX = 2;
    public static final Integer TYPE_TEXT = 3;
    public static final Integer TYPE_PHOTO = 4;
}

