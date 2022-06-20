package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ServerAuditEntry;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/20 11:14
 * @mood
 */
public interface ServerAuditEntryService extends IService<ServerAuditEntry> {
    ServerAuditEntry getByName(String name);

    ServerAuditEntry getBySort(BigDecimal sort);

    R saveOne(ServerAuditEntryQuery serverAuditEntryQuery);
}
