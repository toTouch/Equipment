package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.xiliulou.afterserver.constant.ProductNewStatusSortConstants;
import com.xiliulou.afterserver.web.vo.OssUrlVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * (ProductNew)实体类
 *
 * @author Eclair
 * @since 2021-08-17 10:29:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_product_new")
public class ProductNew {

    private Long id;
    /**
     * 产品编号
     */
    private String no;
    /**
     * 型号ID
     */
    private Long modelId;
    /**
     * 产品批次ID
     */
    private Long batchId;
    /**
     * 状态  
     *         {
     *           value: 0,
     *           label: '生产中'
     *         },
     *         {
     *           value: 1,
     *           label: '已发货'
     *         },
     *         {
     *           value: 2,
     *           label: '已收货'
     *         },
     *         {
     *           value: 3,
     *           label: '使用中'
     *         },
     *         {
     *           value: 4,
     *           label: '拆机柜'
     *         },
     *         {
     *           value: 5,
     *           label: '已报废'
     *         },
     *         {
     *           value: 6,
     *           label: '已测试'
     *         },
     *         {
     *           value: 7,
     *           label: '电装检验合格'
     *         },
     *         {
     *           value: 8,
     *           label: '出货检验合格'
     *         }
     * {@link ProductNewStatusSortConstants}
     */
    private Integer status;
    /**
     * 质保开始时间
     */
    private Long expirationStartTime;
    /**
     * 保质期年份
     */
    private Integer years;
    /**
     * 质保结束时间
     */
    private Long expirationEndTime;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 删除状态 0正常 1 删除
     */
    private Integer delFlag;
    /**
     * 外观
     */
    private Integer surface;
    /**
     * 颜色
     */
    private Integer color;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    private Integer cache;
    private String serialNum;
    private Long supplierId;
    private String type;
    private String code;
    private String remarks;
    private Integer testResult;
    private String testFile;
    private Long iotCardId;
    private Long cameraId;
    private String appVersion;
    private String sysVersion;

    public static final String TYPE_M = "M";
    public static final String TYPE_V = "V";
    public static final Integer TEST_RESULT_FAIL = 0;
    public static final Integer TEST_RESULT_SUCCESS = 1;

//    //左区间
//    @TableField(exist = false)
//    private Long leftInterval;
//    //右区间
//    @TableField(exist = false)
//    private Long rightInterval;

    @TableField(exist = false)
    private String modelName;
    @TableField(exist = false)
    private String batchName;

    @TableField(exist = false)
    private Integer productCount;
    @TableField(exist = false)
    private List<File> fileList;
    @TableField(exist = false)
    private Integer pointId;
    @TableField(exist = false)
    private String pointName;
    @TableField(exist = false)
    private String supplierName;
    @TableField(exist = false)
    private Integer pointType;
    @TableField(exist = false)
    private String iotCardName;
    @TableField(exist = false)
    private String cameraSerialNum;
    @TableField(exist = false)
    private String colorName;
    @TableField(exist = false)
    private List<OssUrlVo> accessoryPackagingFileList;
    @TableField(exist = false)
    private List<OssUrlVo> outerPackagingFileList;
    @TableField(exist = false)
    private List<OssUrlVo> qualityInspectionFileList;
}
