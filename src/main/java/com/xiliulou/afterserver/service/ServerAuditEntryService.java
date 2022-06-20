package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ServerAuditEntry;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import com.xiliulou.afterserver.web.vo.WeChatServerAuditEntryVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/20 11:14
 * @mood
 */
public interface ServerAuditEntryService extends IService<ServerAuditEntry> {
    ServerAuditEntry getByName(String name);

    ServerAuditEntry getBySort(BigDecimal sort);

    R saveOne(ServerAuditEntryQuery serverAuditEntryQuery);

    R putOne(ServerAuditEntryQuery serverAuditEntryQuery);

    R getAdminList();

    R removeByid(Long id);

    R getUserList(Long workOrderId);

    List<WeChatServerAuditEntryVo> getWeChatServerAuditEntryVoList(Long workOrderId, Long serverId);
}
