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
     * 硬件消息
     */
    public static final Integer  TYPE_AFTER_SALES_AUDIT = 3;
    /**
     * 设备消息
     */
    public static final Integer TYPE_AFTER_SALES_SERVER = 4;
}
