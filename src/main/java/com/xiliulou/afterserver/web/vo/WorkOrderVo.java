package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.WorkOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 10:00
 **/
@Data
public class WorkOrderVo {
    private Long id;
    private Integer type;
    private Long pointId;
    private String info;
    private BigDecimal fee;
    private Long thirdCompanyId;
    private Integer thirdCompanyType;
    private BigDecimal thirdCompanyPay;
    private Long workOrderReasonId;
    //服务商id
    private Long serverId;
    //服务商接单人
    private String processor;
    private Long processTime;
    private Integer status;
    private String orderNo;
    private Long createrId;
    private Long createTime;
    private String thirdCompanyName;
    private String serverName;
    private String workOrderReasonName;
    private String workOrderType;
    private String creater;
    private String pointName;
    private List<File> fileList;
    //起点
    private String transferSourcePointId;
    //终点
    private String transferDestinationPointId;
    //起点
    private String transferSourcePointName;
    //终点
    private String transferDestinationPointName;
}
