package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.ServerAuditValue;
import com.xiliulou.afterserver.mapper.ServerAuditValueMapper;
import com.xiliulou.afterserver.service.ServerAuditValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zgw
 * @date 2022/6/20 11:24
 * @mood
 */
@Service
public class ServerAuditValueServiceImpl extends ServiceImpl<ServerAuditValueMapper, ServerAuditValue> implements ServerAuditValueService {

    @Resource
    ServerAuditValueMapper serverAuditValueMapper;

    @Override
    public ServerAuditValue queryByOrderIdAndServerId(Long entryId, Long workOrderId, Long thirdId) {
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditValue>().eq("entry_id", entryId).eq("order_id", workOrderId).eq("server_id", thirdId));
    }
}
