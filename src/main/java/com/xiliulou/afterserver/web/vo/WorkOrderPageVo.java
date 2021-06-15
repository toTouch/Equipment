package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

/**
 * @author Hardy
 * @date 2021/6/15 0015 14:06
 * @Description:
 */
@Data
public class WorkOrderPageVo extends WorkOrder {
    private String pointName;
}
