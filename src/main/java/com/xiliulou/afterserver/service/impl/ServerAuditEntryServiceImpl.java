package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.ServerAuditEntry;
import com.xiliulou.afterserver.mapper.ServerAuditEntryMapper;
import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zgw
 * @date 2022/6/20 11:21
 * @mood
 */
@Service
@Slf4j
public class ServerAuditEntryServiceImpl extends ServiceImpl<ServerAuditEntryMapper, ServerAuditEntry> implements ServerAuditEntryService {
    @Resource
    ServerAuditEntryMapper serverAuditEntryMapper;


    @Override
    public R saveOne(ServerAuditEntryQuery serverAuditEntryQuery) {
        return null;
    }
}
