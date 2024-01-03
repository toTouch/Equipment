package com.xiliulou.afterserver.web.vo;

import lombok.Data;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/8 10:27
 * @mood
 */
@Data
public class KeyProcessVo {
    private Long pid;
    private Long groupId;
    private String groupName;
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
     * 创建时间
     */
    private String createTime;
    /**
     * 柜机时间
     */
    private String productStatus;
    /**
     * 分组
     */
    private List<KeyProcessAuditGroupVo> keyProcessAuditGroupList;
    /**
     * 页面，值
     */
    private List<KeyProcessAuditEntryVo> keyProcessAuditEntryList;
    /**
     * 柜机编码
     */
    private String cabinetSn;
}


