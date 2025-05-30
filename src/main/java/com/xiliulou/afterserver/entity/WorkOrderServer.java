package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/3/29 17:53
 * @mood
 */
@Data
@TableName("t_work_order_server")
public class WorkOrderServer {

    private Long id;

    private Long workOrderId;

    private Long serverId;

    private String serverName;
    /**
     * 人工工单费用
     */
    private BigDecimal artificialFee;
    /**
     * 物料工单费用
     */
    private BigDecimal materialFee;
    /**
     * 运费
     */
    private BigDecimal deliverFee;
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
     * 第三方名字
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

    private Long createTime;
    /**
     * 是否有配件  0--无  1--有
     */
    private Integer hasParts;

    public static final Integer USE_THIRD = 1;
    public static final Integer NOT_USE_THIRD = 0;

    public static final Integer NOT_HAS_PARTS = 0;
    public static final Integer HAS_PARTS = 1;
}
