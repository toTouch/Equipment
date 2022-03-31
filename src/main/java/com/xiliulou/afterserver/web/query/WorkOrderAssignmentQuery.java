package com.xiliulou.afterserver.web.query;

import lombok.Data;

import java.util.List;

/**
 * @author zgw
 * @date 2022/3/31 9:07
 * @mood
 */
@Data
public class WorkOrderAssignmentQuery {
    private Long id;
    private String orderNo;
    private Integer status;
    private Integer type;
    private Long pointId;
    private String pointName;
    private Integer pointStatus;
    private Long createTime;
    private String productInfo;
    private List<ProductInfoQuery> productInfoList;
    private String info;
    //起点
    private Long transferSourcePointId;
    private String transferSourcePointName;
    //终点
    private Long transferDestinationPointId;
    private String transferDestinationPointName;
    /**
     * 起点 1点位 2仓库
     */
    private Integer sourceType;
    /**
     * 终点  1点位 2仓库
     */
    private Integer destinationType;
    private String describeinfo;
    private String code;
    private Long workOrderReasonId;
    private String parentWorkOrderReason;
    private String workOrderReasonName;
    private List<WorkOrderServerQuery> workOrderServerList;
}
