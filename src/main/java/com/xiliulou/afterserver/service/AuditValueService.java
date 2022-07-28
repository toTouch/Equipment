package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditValue;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 18:03
 * @mood
 */
public interface AuditValueService extends IService<AuditValue> {
    Long getCountByEntryIdsAndPid(List<Long> entryIds, Long productNewId, Integer required);

    boolean biandOrUnbindEntry(Long entryId, String value, Long pid);

    boolean isValueEmpty(Long entryId, Long pid);

    String getValue(Long entryId, Long pid);

    public List<AuditValue> getByPidAndEntryIds(List<Long> entryIds, Long pid);

    void copyValueToTargetValueIsNoll(List<AuditValue> source, Long pid);

    void copyValueToTargetValueIsNoll(AuditValue source, AuditValue target);
}
