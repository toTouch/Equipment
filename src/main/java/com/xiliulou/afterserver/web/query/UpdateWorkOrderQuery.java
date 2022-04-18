package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/4/18 16:12
 * @mood
 */
@Data
public class UpdateWorkOrderQuery extends WorkOrder {
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
    private List<UpdateWorkOrderServerQuery> workOrderServerList;
    /**
     * 工单总费用
     */
    private BigDecimal totalFee;
}
