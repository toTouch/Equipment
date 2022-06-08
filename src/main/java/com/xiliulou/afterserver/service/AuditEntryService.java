package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditEntryVo;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:41
 * @mood
 */
public interface AuditEntryService extends IService<AuditEntry> {
    Long getCountByIdsAndRequired(List<Long> entryIds, Integer required);

    List<KeyProcessAuditEntryVo> getVoByEntryIds(List<Long> entryIds, Long pid);
}
