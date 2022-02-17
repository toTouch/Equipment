package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author 叶书箖
 * @date 2022/2/17 13:54
 * @mood
 */
@Data
public class ProductNewDetailsQuery {
    private Long id;
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
     * 摄像头序列号
     */
    private String serialNum;
    /**
     * 摄像头物联网卡id
     */
    private Long cameraCardId;
}
