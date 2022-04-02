package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import com.xiliulou.afterserver.web.query.WorkOrderServerQuery;
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
    private String thirdReason;
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
    private String reason;
    //起点
    private String transferSourcePointId;
    //终点
    private String transferDestinationPointId;
    //起点
    private String transferSourcePointName;
    //终点
    private String transferDestinationPointName;
    //产品类型
    private String product;
    //产品编码
    private String code;

    private String userName;

    private String describeinfo;

    private Integer paymentMethod;

    private String paymentMethodName;

    private Integer thirdPaymentStatus;

    private String destinationType;

    private String sourceType;

    //private String thirdPaymentStatusName;
    //第三方责任对接人
    private String thirdResponsiblePerson;
    //时效
    private String prescription;
    private Integer auditStatus;
    private String auditRemarks;
    private Integer fileCount;
    private Long prescriptionMillis;
    private String parentWorkOrderReason;
    private String productInfo;
    private List<ProductInfoQuery> productInfoList;
    private Integer pointStatus;
    private Long commissionerId;
    private String commissionerName;
    private List<WorkOrderServerQuery> workOrderServerList;
    /**
     * 凭证
     */
    private List<File> voucherImgFile;
    /**
     * 视频
     */
    private File voucherVideoFile;
}
