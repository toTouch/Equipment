package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.mapper.AuditEntryMapper;
import com.xiliulou.afterserver.service.AuditEntryService;
import com.xiliulou.afterserver.service.AuditValueService;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditEntryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:42
 * @mood
 */
@Slf4j
@Service
public class AuditEntryServiceImpl extends ServiceImpl<AuditEntryMapper, AuditEntry> implements AuditEntryService {

    @Resource
    AuditEntryMapper auditEntryMapper;
    @Autowired
    AuditValueService auditValueService;

    @Override
    public Long getCountByIdsAndRequired(List<Long> entryIds, Integer required) {
        return auditEntryMapper.getCountByIdsAndRequired(entryIds, required);
    }

    @Override
    public List<KeyProcessAuditEntryVo> getVoByEntryIds(List<Long> entryIds, Long pid) {
        if(CollectionUtils.isEmpty(entryIds)) {
            return null;
        }

        return  auditEntryMapper.queryByEntryIdsAndPid(entryIds, pid);
    }


    private String GenerateRegular(Integer auditEntryType, String alternatives){

        if(StringUtils.isNotBlank(alternatives)) {
            String alternativesReg = alternatives.replaceAll(",", "|");
            //1单选 2多选 3文本 4图片
            if(auditEntryType.equals(AuditEntry.TYPE_RADIO)) {
                return alternativesReg;
            }

            if(auditEntryType.equals(AuditEntry.TYPE_CHECKBOX)) {
                String template = "((%s),)*(%s)";
                return String.format(template, alternativesReg, alternativesReg);
            }
        }

        return null;
    }
}
