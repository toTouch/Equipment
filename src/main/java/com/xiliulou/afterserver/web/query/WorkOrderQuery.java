package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 16:54
 **/
@Data
public class WorkOrderQuery extends WorkOrder {

    private Long createTimeStart;
    private Long createTimeEnd;
    private List<String> fileList;
    private Long offset;
    private Long size;
    private Integer workOrderType;
    private List<Long> productSerialNumberIdList;
    private Map<Long, Integer> productSerialNumberIdAndSetNoMap;
    private Integer status;
    private String code;
    private Integer thirdCompanyType;
    private BigDecimal sendManey;

    private String startAddr;
    private String count;

}
