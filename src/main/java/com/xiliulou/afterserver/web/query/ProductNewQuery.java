package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/17 14:53
 * @mood
 */
@Data
public class ProductNewQuery {
    private Long id;
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
     * 状态
     */
    private Integer status;
    /**
     * 外观
     */
    private Integer surface;
    /**
     * 颜色
     */
    private Integer color;
    /**
     * 物联网卡id
     */
    private Long iotCardId;
    /**
     * 摄像头物联网卡id
     */
    private Long cameraCardId;
    /**
     * 备注
     */
    private String remarks;
}
