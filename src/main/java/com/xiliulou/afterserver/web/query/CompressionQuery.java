package com.xiliulou.afterserver.web.query;

import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/16 14:19
 * @mood
 */
@Data
public class CompressionQuery {
    private String iotCard;
    private String compressionFile;
    private String testBoxFile;
    private List<String> noList;
    private String testType;
    private String errorMessage;
    private Long testEndTime;
    /**
     * 压测状态 0 失败 ，1 成功 ，2 压测中
     */
    private Integer testStatus;

    public static final String TEST_TYPE_ELECTRICITY = "electricity";
    public static final String TEST_TYPE_CUPBOARD = "cupboard";

    /**
     * 压测状态
     */
    public static final Integer TEST_FAIL = 0;
    public static final Integer TEST_SUCC = 1;
    public static final Integer TEST_ING = 2;
    /**
     * 人机交互结果，和压测状态共用一个字段
     */
    public static final Integer ELE_TEST_FAIL = 3;
    public static final Integer ELE_TEST_SUCC = 4;
    public static final Integer ELE_TEST_ING = 5;
}
