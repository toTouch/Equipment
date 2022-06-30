package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.AuditGroupLogContent;
import com.xiliulou.afterserver.entity.AuditGroupUpdateLog;
import com.xiliulou.afterserver.mapper.AuditGroupLogContentMapper;
import com.xiliulou.afterserver.mapper.AuditGroupUpdateLogMapper;
import com.xiliulou.afterserver.service.AuditGroupLogContentService;
import com.xiliulou.afterserver.service.AuditGroupUpdateLogService;
import org.springframework.stereotype.Service;

/**
 * @author zgw
 * @date 2022/6/22 17:47
 * @mood
 */
@Service
public class AuditGroupLogContentServiceImpl extends ServiceImpl<AuditGroupLogContentMapper, AuditGroupLogContent> implements AuditGroupLogContentService {

    @Override
    public boolean saveOne(Long logId, String entryName, Integer entryType, String value) {
        AuditGroupLogContent auditGroupLogContent = new AuditGroupLogContent();
        auditGroupLogContent.setLogId(logId);
        auditGroupLogContent.setEntryName(entryName);
        auditGroupLogContent.setEntryType(entryType);
        auditGroupLogContent.setValue(value);
        return this.baseMapper.insert(auditGroupLogContent) > 0;
    }
}
