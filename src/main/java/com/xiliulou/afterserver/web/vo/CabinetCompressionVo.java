package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author zgw
 * @date 2023/4/26 17:58
 * @mood
 */
@Data
public class CabinetCompressionVo {
    /**
     * 资产编码
     */
    private String sn;
    /**
     * 压测状态 0 失败 ，   1 成功   ， 2 压测中
     */
    private Integer testStatus;
    /**
     * 压测开始时间（压测结束无需上报）
     */
    private Long testStartTime;
    /**
     * 压测结束时间（压测开始无需上报）
     */
    private Long testEndTime;
    /**
     * 压测错误原因
     */
    private String testMsg;
}
