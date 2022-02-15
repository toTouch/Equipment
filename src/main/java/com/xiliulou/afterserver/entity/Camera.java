package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hardy
 * @date 2022/2/8 16:26
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_camera")
public class Camera {
    private Long id;
    private String serialNum;
    private Long supplierId;
    private String cameraCard;
    private Long createTime;
    private Long updateTime;
    private Integer delFlag;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    @TableField(exist = false)
    private String supplierName;
}
