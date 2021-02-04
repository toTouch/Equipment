package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 10:00
 **/
@Data
public class WorkOrderVo extends WorkOrder {

    private String workOrderReasonName;
    private String workOrderType;
    private String creater;
    private String pointName;
    private List<File> fileList;
}
