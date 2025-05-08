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
     * 状态 { value:0, label:'生产中' }, { value:1, label:'已发货' }, { value:2, label:'已收货' }, { value:3, label:'使用中' }, { value:4, label:'拆机柜' }, { value:5, label:'已报废' }, { value:6, label:'已测试' }, { value: 7, label: '前置检测完成' }, { value: 8, label: '后置检查完成' }
     */
    private Integer status;
    
    /**
     * nos
     */
    private String nos;
    ///**
    // * 外观
    // */
    //private Integer surface;
    ///**
    // * 颜色
    // */
    //private Integer color;
    ///**
    // * 物联网卡id
    // */
    //private Long iotCardId;
    ///**
    // * 摄像头物联网卡id
    // */
    //private Long cameraCardId;
    /**
     * 备注
     */
    private String remarks;
    /**
     * app版本
     */
    private String appVersion;
    /**
     * 系统版本
     */
    private String sysVersion;
}
