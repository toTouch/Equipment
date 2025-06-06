package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.WorkOrderParts;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zgw
 * @date 2022/3/29 18:36
 * @mood
 */
@Data
public class WorkOrderServerQuery {
    private Long id;

    private Long workOrderId;

    private Long serverId;
    /**
     * 服务商名字
     */
    private String serverName;
    /**
     * 人工费用
     */
    private BigDecimal artificialFee;
    /**
     * 运费
     */
    private BigDecimal deliverFee;
    /**
     * 物料费用
     */
    private BigDecimal materialFee;
    /**
     * 结算方式 1--月结 2--现结
     */
    private Integer paymentMethod;
    /**
     * 解决方案
     */
    private String solution;
    /**
     * 解决时间
     */
    private Long solutionTime;
    /**
     * 时效（处理时长）
     */
    private Long prescription;
    /**
     * 是否需要第三方
     */
    private Boolean isUseThird;
    /**
     * 第三方类型 1：客户 2：供应商 3:服务商
     */
    private Integer thirdCompanyType;
    /**
     * 第三方公司id
     */
    private String thirdCompanyId;
    /**
     * 第三方公司名
     */
    private String thirdCompanyName;
    /**
     * 工单费用
     */
    private BigDecimal fee;
    /**
     * 无需结算 1，未结算 2，已结算 3
     */
    private Integer thirdPaymentStatus;
    /**
     * 第三方结算人
     */
    private String thirdPaymentCustomer;
    /**
     * 第三方问题
     */
    private String thirdReason;
    /**
     * 第三方对接人
     */
    private String thirdResponsiblePerson;
    /**
     * 文件内容
     */
    private List<File> fileList;
    /**
     * 文件个数
     */
    private Integer fileCount;
    /**
     * 是否有配件 0--否 1--是
     */
    private Integer hasParts;

    private List<WorkOrderParts> workOrderParts;

    private List<WorkOrderParts> thirdWorkOrderParts;
}
