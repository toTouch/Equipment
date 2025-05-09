package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/18 16:17
 * @mood
 */
@Data
public class DeliverProductNewInfoVo {
    private Long id;
    private Long batchId;
    private String batchNo;
    private String modelName;
    private String no;
    private String statusName;
    private String insertTime;

    private Boolean canPrint;
    private String customerName;
    private Integer sum;
    private String productColor;
    private String doorColor;
    private String type;
    private String productionTime;
}
