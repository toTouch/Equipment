package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

import java.util.List;

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
    private List<String> fileNameList;
    private Long offset;
    private Long size;
    private Integer workOrderType;
}
