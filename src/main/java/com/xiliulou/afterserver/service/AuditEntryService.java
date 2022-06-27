package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditEntryStrawberryQuery;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditEntryVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:41
 * @mood
 */
public interface AuditEntryService extends IService<AuditEntry> {
    Long getCountByIdsAndRequired(List<Long> entryIds, Integer required);

    List<KeyProcessAuditEntryVo> getVoByEntryIds(List<Long> entryIds, Long pid,Long groupId, Integer isAdmin);

    List<AuditEntry> getByEntryIds(List<Long> entryIds);

    AuditEntry getByName(String name);

    AuditEntry getBySort(BigDecimal sort, List<Long> entryIds);

    boolean removeById(Long id);

    R queryList(String groupId);

    R saveOne(AuditEntryStrawberryQuery query);

    R putOne(AuditEntryStrawberryQuery query);

    R removeOne(Long groupId, Long id);
}
