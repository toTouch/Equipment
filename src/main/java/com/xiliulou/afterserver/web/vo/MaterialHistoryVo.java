package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.util.List;

@Data
public class MaterialHistoryVo {
    
    /**
     * 机柜编码cabinetSn
     */
    private String cabinetSn;
    
    /**
     * 资产编码
     */
    private String no;
    
    /**
     * 发货时间
     */
    private Long deliverTime;
    
    /**
     * 生产日期
     */
    private Long productionTime;
    
    /**
     * 运营商名称
     */
    private String tenantName;
    /**
     * 供应商名称
     */
    private String supplierName;
//
//    /**
//     * Y8030010
//     */
//    private List<MaterialCellVo> touchPanel;
//
//    /**
//     * Y8030017
//     */
//    private List<MaterialCellVo> linuxBoard;
//
//    /**
//     * Y5030015 6合1
//     */
//    private List<MaterialCellVo> SixInOne;
//
//    /**
//     * Y5030011
//     */
//    private List<MaterialCellVo> connectorBoard;
    
    private String atmelID;
//
//    /**
//     * Y5030010
//     */
//    private List<MaterialCellVo> communicationBoard;
//
//    /**
//     * Y5000301
//     */
//    private List<MaterialCellVo> ACCharger;
//
//    /**
//     * Y5030012 6合1 多个
//     */
//    private List<MaterialCellVo> DCDCBoard;
//
//    /**
//     * Y5000322
//     */
//    private List<MaterialCellVo> Module4G;
    
    private String imei;
    
    
    /**
     * 采购单
     */
    private String purchaseOrder;
    
    /**
     * 项次
     */
    private String item;
    
    /**
     * 成品料号
     */
    private String materialNo;
    
    /**
     * 批次号
     */
    private Long batchId;
    
}
