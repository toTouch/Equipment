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
    private Long processTimeStart;
    private Long processTimeEnd;
    //private List<String> fileList;
    private Long offset;
    private Long size;
    private Integer workOrderType;
    private List<Long> productSerialNumberIdList;
    private Map<Long, Integer> productSerialNumberIdAndSetNoMap;
    private Integer status;
    private String code;
    private Integer thirdCompanyType;
    private BigDecimal sendManey;
    //1点位 2仓库


    private String startAddr;
    private String count;
    /**
     * 柜子信息list
     */
    private List<ProductInfoQuery> productInfoList;
    private Integer pointStatus;
    private List<WorkOrderServerQuery> workOrderServerList;
    /**
     * 工单总费用
     */
    private BigDecimal totalFee;
    private String thirdCompanyName;
    private String thirdResponsiblePerson;
    private String serverName;
}
