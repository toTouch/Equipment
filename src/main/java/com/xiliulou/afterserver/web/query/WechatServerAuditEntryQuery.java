package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.WorkOrderParts;
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
    private String solution;
    private List<WechatServerEntryValueQuery> wechatServerEntryValueQueryList;
    private List<WorkOrderParts> workOrderParts;
    /**
     * 是否有物件 0否 1是
     */
    private Integer hasParts;
}
