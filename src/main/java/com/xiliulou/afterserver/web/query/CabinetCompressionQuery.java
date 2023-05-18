package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author zgw
 * @date 2023/4/26 10:30
 * @mood
 */
@Data
public class CabinetCompressionQuery {
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
    /**
     * 压测内容
     */
    private String testContent;
    /**
     * 单次压测结果
     */
    private String testContentResult;

    public static final String TEST_TYPE_ELECTRICITY = "electricity";
    public static final String TEST_TYPE_CUPBOARD = "cupboard";

    /**
     * 压测状态
     */
    public static final Integer TEST_FAIL = 0;
    public static final Integer TEST_SUCC = 1;
    public static final Integer TEST_ING = 2;


}
