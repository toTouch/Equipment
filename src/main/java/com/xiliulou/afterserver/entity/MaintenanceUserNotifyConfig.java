package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2022/5/16 14:16
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_maintenance_user_notify_config")
public class MaintenanceUserNotifyConfig {

    private Integer id;
    /**
     * 用户手机号数组json
     */
    private String phones;

    private Long createTime;

    private Long updateTime;

    /**
     * 选择权限
     */
    private Integer permissions;
    /**
     * 类型 1：服务商 2：专员
     */
    private Integer type;

    private Long bindId;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;
    /**
     * 点位审核未通过通知
     */
    public static Integer P_AUDIT_FAILED = 4;
    /**
     * 审核通知
     */
    public static Integer P_REVIEW = 2;
    /**
     * 派单通知
     */
    public static Integer P_SERVER = 1;

    /**
     * 服务商
     */
    public static Integer TYPE_SERVER = 1;
    /**
     * 专员
     */
    public static Integer TYPE_REVIEW = 2;
}
