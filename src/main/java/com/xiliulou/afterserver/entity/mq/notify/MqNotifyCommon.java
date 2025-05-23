package com.xiliulou.afterserver.entity.mq.notify;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/4/28 11:40
 * @mood
 */
@Data
public class MqNotifyCommon<T> {
    /**
     * 通知的类型
     */
    private Integer type;
    /**
     * 通知时间
     */
    private Long time;
    /**
     * 通知人的手机号
     */
    private String phone;
    /**
     * 详细通知内容
     */
    private T data;

    /**
     * 专员审核通知
     */
    public static final Integer  TYPE_AFTER_SALES_AUDIT = 4;
    /**
     * 服务商派单通知
     */
    public static final Integer TYPE_AFTER_SALES_SERVER = 3;
    /**
     * 专员点位审合通知
     */
    public static final Integer TYPE_AFTER_SALES_POINT_AUDIT = 7;
}
