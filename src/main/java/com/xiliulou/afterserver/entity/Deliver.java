package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import com.xiliulou.afterserver.web.vo.DeliverInfoVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description: 发货
 * @author: Mr.YG
 * @create: 2021-01-28 16:58
 **/
@Data
@TableName("deliver")
public class Deliver {
    
    
    
    private Long id;
    private Long customerId;//客户id
    private Long phone;//客户电话
    private String city;//起点城市
    private String province;
    private String destination;//终点
    private String remark;//备注
    private BigDecimal deliverCost;//配送费用
    private Long deliverTime;//配送时间
    private Long createTime;
    private String expressCompany;//快递公司
    private String expressNo;//快递单号
    private Long pointId;
    /**
     *       {
     *           value: 1,
     *           label: '未发货'
     *         },
     *         {
     *           value: 2,
     *           label: '已发货'
     *         },
     *         {
     *           value: 3,
     *           label: '已到达'
     *         }
     */
    private Integer state;//物流状态
    private String product;//型号
    private String quantity;//数量

    /**
     * 创建人
     */
    private Long createUid;

    private Long thirdCompanyId;
    private Integer thirdCompanyType;
    private BigDecimal thirdCompanyPay;
    private String thirdReason;
    /**
     * 结算方式 1 月结 2 现结
     */
    private Integer paymentMethod;
    private String no;
    private Integer cityType;
    private Integer destinationType;



    public static final Integer CITY_TYPE_POINT = 1;
    public static final Integer CITY_TYPE_WAREHOUSE = 2;
    public static final Integer CITY_TYPE_FACTORY = 3;
    public static final Integer DESTINATION_TYPE_POINT = 1;
    public static final Integer DESTINATION_TYPE_WAREHOUSE = 2;
    public static final Integer DESTINATION_TYPE_FACTORY = 3;
    
    /**
     * 已发货
     */
    public static final Integer STATUS_SHIPPED = 2;


    public static final Integer PAYMENT_METHOD_MONTHLY = 1;
    public static final Integer PAYMENT_METHOD_NOW = 2;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String thirdCompanyName;

    @TableField(exist = false)
    private Map details;

    @TableField(exist = false)
    private Long cityId;
    @TableField(exist = false)
    private Long destinationId;
    @TableField(exist = false)
    private boolean isDeliver;
    @TableField(exist = false)
    private String paymentMethodName;
    @TableField(exist = false)
    private List<ProductInfoQuery> productInfoList;
    @TableField(exist = false)
    private Map<String, List<DeliverInfoVo>> deliverInfoVoMap;

    private String tenantName;
}
