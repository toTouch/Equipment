package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.xiliulou.afterserver.web.query.CameraInfoQuery;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * (PointNew)实体类
 *
 * @author Eclair
 * @since 2021-08-17 10:28:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_point_new")
public class PointNew {

    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 状态 1,移机,2运营中,3:拆机,4初始化,5待安装,6运输中，7安装中，8安装完成，9已暂停，10已取消
     */
    private Integer status;
    /**
     * 城市id
     */
    private Integer cityId;
    /**
     * 安装类型（1:室外 2:半室外3：室内）
     */
    private Integer installType;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 摄像头数量
     */
    private Integer cameraCount;
    /**
     * 雨棚数量
     */
    private Integer canopyCount;
    /**
     * SN码
     */
    private String snNo;
    /**
     * 物联网卡号
     */
    private String cardNumber;
    /***
     * 备注
     */
    private String remarks;
    /**
     * 创建人
     */
    private Long createUid;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 删除状态 0正常 1 删除
     */
    private Integer delFlag;

    private Long installTime;

    //新需求
    private String cardSupplier;//物联网卡供应商
    private Long completionTime;//施工完成时间
    private Integer isEntry;//是否入账
    private Integer isAcceptance;//是否验收
    
    //取餐柜 1、餐厅柜 2、换电柜 3、充电柜 4、寄存柜 5、生鲜柜 6
    private Integer productSeries;//产品系列
    private Long orderTime;//工单时间
    private String operator;//运营商
    private String logisticsInfo;//物流信息
    private String productInfo;//柜子信息
    private String cameraInfo;//摄像头信息
    private Integer auditStatus;//审核状态
    private String auditRemarks;//审核备注


    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    @TableField(exist = false)
    private List<Long> productIds;
    @TableField(exist = false)
    private String cityName;
    @TableField(exist = false)
    private String customerName;
    @TableField(exist = false)
    private String province;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private List<ProductInfoQuery> productInfoList;
    @TableField(exist = false)
    private List<CameraInfoQuery> cameraInfoList;
    @TableField(exist = false)
    private Integer fileCount;
    @TableField(exist = false)
    private String isbindProduct;

    public static final String UNBIND_PRODUCT = "否";
    public static final String BIND_PRODUCT = "是";

    public static final Integer AUDIT_STATUS_WAIT = 1;
    public static final Integer AUDIT_STATUS_FAIL = 2;
    public static final Integer AUDIT_STATUS_PASSED = 3;
}
