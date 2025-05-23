package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.File;
import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/17 9:10
 * @mood
 */
@Data
public class ProductNewDetailsVo {
    private Long id;
    /**
     * 产品编号
     */
    private String no;
    /**
     * 产品批次ID
     */
    private Long batchId;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态
     */
    private String statusName;
    /**
     * 外观
     */
    private Integer surface;
    /**
     * 颜色
     */
    private Integer color;
    /**
     * 颜色名称
     */
    private String colorName;
    /**
     * 物联网卡id
     */
    private Long iotCardId;
    /**
     * 摄像头id
     */
    private Long cameraId;
    /**
     * 物联网卡卡号
     */
    private String iotCardNo;
    /**
     * 摄像头序列号
     */
    private String serialNum;
    /**
     * 摄像头物联网卡号
     */
    private String cameraCard;
    /**
     * 摄像头物联网卡id
     */
    private Long cameraCardId;

    private String createTime;
    /**
     * 附件包
     */
    private List<OssUrlVo> accessoryPackagingFileUrl;
    /**
     * 外包装
     */
    private List<OssUrlVo> outerPackagingFileUrl;
    /**
     * 质检
     */
    private List<OssUrlVo> qualityInspectionFileUrl;
}
