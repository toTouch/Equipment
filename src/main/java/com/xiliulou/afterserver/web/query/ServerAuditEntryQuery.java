package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/20 11:41
 * @mood
 */

@Data
public class ServerAuditEntryQuery  {
    private Long id;
    /**
     * 正则
     */
    private String jsonContent;
    /**
     * 保留字段
     */
    private String jsonRoot;
    /**
     * 图片
     */
    private String photo;

    /**
     * 组件名
     */
    @NotNull(message = "组件名称不能为空")
    private String name;
    /**
     * 组件type：1单选 2多选 3文本 4图片
     */
    @NotNull(message = "组件类型不能为空")
    private Integer type;

    /**
     * 排序
     */
    @NotNull(message = "组件排序不能为空")
    private BigDecimal sort;
    /**
     * 是否必须 0否1是
     */
    @NotNull(message = "是否必填不能为空")
    private Integer required;

}
