package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/8 14:56
 * @mood
 */
@Data
public class KeyProcessQuery {
    @NotNull(message = "分组id不能为空")
    private Long groupId;

    List<AuditEntryQuery> auditEntryQueryList;
}
