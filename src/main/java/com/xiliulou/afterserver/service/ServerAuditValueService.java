package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ServerAuditValue;

/**
 * @author zgw
 * @date 2022/6/20 11:16
 * @mood
 */
public interface ServerAuditValueService extends IService<ServerAuditValue> {
    ServerAuditValue queryByOrderIdAndServerId(Long id, Long workOrderId, Long thirdId);
}
