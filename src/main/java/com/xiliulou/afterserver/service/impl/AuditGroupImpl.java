package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.entity.AuditGroup;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.mapper.AuditGroupMapper;
import com.xiliulou.afterserver.service.AuditEntryService;
import com.xiliulou.afterserver.service.AuditGroupService;
import com.xiliulou.afterserver.service.AuditValueService;
import com.xiliulou.afterserver.web.vo.AuditGroupVo;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditGroupVo;
import com.xiliulou.core.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zgw
 * @date 2022/6/6 18:24
 * @mood
 */
@Service
public class AuditGroupImpl extends ServiceImpl<AuditGroupMapper, AuditGroup> implements AuditGroupService {

    @Resource
    AuditGroupMapper auditGroupMapper;
    @Autowired
    AuditEntryService auditEntryService;
    @Autowired
    AuditValueService auditValueService;

    @Override
    public List<AuditGroup> getByProcessId(Long id) {
        return auditGroupMapper.getByProcessId(id);
    }

    @Override
    public Integer getGroupStatus(AuditGroup auditGroup, Long ProductNewId) {
        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds)) {
            return AuditGroupVo.STATUS_FINISHED;
        }

        Long auditEntryCount = auditEntryService.getCountByIdsAndRequired(entryIds, AuditEntry.REQUIRED);
        Long auditValueRequiredCount = auditValueService.getCountByEntryIdsAndPid(entryIds, ProductNewId, AuditEntry.REQUIRED);
        Long auditValueNotRequiredCount = auditValueService.getCountByEntryIdsAndPid(entryIds, ProductNewId, AuditEntry.NOT_REQUIRED);

        if(Objects.equals(0L, auditValueRequiredCount + auditValueNotRequiredCount)) {
            return AuditGroupVo.STATUS_UNFINISHED;
        }

        if(Objects.equals(auditEntryCount, auditValueRequiredCount)) {
            return AuditGroupVo.STATUS_FINISHED;
        }

        return AuditGroupVo.STATUS_EXECUTING;
    }

    @Override
    public KeyProcessAuditGroupVo statusAdjustment(List<KeyProcessAuditGroupVo> keyProcessAuditGroupVos) {
        for(KeyProcessAuditGroupVo vo : keyProcessAuditGroupVos) {
            if(Objects.equals(vo.getStatus(), AuditGroupVo.STATUS_EXECUTING)){
                return vo;
            }

            if(Objects.equals(vo.getStatus(), AuditGroupVo.STATUS_UNFINISHED)){
                vo.setStatus(AuditGroupVo.STATUS_EXECUTING);
                return vo;
            }
        }
        return null;
    }
}
