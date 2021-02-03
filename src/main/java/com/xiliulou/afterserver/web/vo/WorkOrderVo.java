package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 10:00
 **/
@Data
public class WorkOrderVo extends WorkOrder {
    private String companyName;
    private String workOrderReasonName;
    private String workOrderType;
    private String creater;
    private String pointName;
}
