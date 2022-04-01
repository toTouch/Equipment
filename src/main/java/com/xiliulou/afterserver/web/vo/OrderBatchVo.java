package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/16 18:15
 * @mood
 */
@Data
public class OrderBatchVo {
    private Long id;
    private String batchNo;
    private String modelName;
    private Integer productNum;
    private String remarks;
    private String createTime;
}
