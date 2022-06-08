package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.AuditValue;
import com.xiliulou.afterserver.mapper.AuditValueMapper;
import com.xiliulou.afterserver.service.AuditValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 18:06
 * @mood
 */
@Service
public class AuditValueServiceImpl extends ServiceImpl<AuditValueMapper, AuditValue> implements AuditValueService {

    @Resource
    AuditValueMapper auditValueMapper;

    @Override
    public Long getCountByEntryIdsAndPid(List<Long> entryIds, Long productNewId, Integer required) {
        return auditValueMapper.getCountByEntryIdsAndPid(entryIds, productNewId, required);
    }
}
