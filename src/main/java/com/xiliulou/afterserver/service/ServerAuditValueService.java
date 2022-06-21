package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ServerAuditValue;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.WechatServerAuditEntryQuery;

/**
 * @author zgw
 * @date 2022/6/20 11:16
 * @mood
 */
public interface ServerAuditValueService extends IService<ServerAuditValue> {
    ServerAuditValue queryByOrderIdAndServerId(Long id, Long workOrderId, Long thirdId);

    R saveOne(WechatServerAuditEntryQuery query);
}
