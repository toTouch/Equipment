package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/21 11:00
 * @mood
 */
@Data
public class WechatServerAuditEntryQuery {
    @NotNull
    private Long workOrderId;
    private List<WechatServerEntryValueQuery> wechatServerEntryValueQueryList;
}
