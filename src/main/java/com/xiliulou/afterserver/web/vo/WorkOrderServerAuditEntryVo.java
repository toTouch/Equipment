package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/20 17:31
 * @mood
 */
@Data
public class WorkOrderServerAuditEntryVo {
    private Long id;
    private String name;
    private List<WeChatServerAuditEntryVo> weChatServerAuditEntryVos;
}
