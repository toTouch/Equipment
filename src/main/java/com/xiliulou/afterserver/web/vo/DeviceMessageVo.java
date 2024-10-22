package com.xiliulou.afterserver.web.vo;

import lombok.Data;

@Data
public class DeviceMessageVo {

    /**
     * 物联网 productId
     */
    private Long productId;

    /**
     * 物联网productKey
     */
    private String productKey;
    /**
     * 物联网deviceName
     */
    private String deviceName;
    
    /**
     * 产品编码 sn
     */
    private String cabinetSn;
    
    /**
     * 物联网deviceSecret
     */
    private String deviceSecret;

    /**
     * 产品型号id
     */
    private Integer modelId;
    /**
     * 三元组是否已使用 0使用 1 未使用
     */

    private Integer isUse;
    /**
     * 格挡数量
     */
    private Integer boxNumber;
    
    
    /**
     * CPU 序列号
     */
    private String cpuSerialNum;
    //三元组已使用
    public static final Integer IS_USE =0;
    //三元组未使用
    public static final Integer NOT_USE =1;
}
