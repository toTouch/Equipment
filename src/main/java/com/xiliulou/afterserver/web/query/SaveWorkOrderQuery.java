package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.WorkOrder;
import jdk.jfr.DataAmount;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 09:29
 **/
@Data
public class SaveWorkOrderQuery {

    private List<WorkOrder> workOrderList;
    private String processor;
}
