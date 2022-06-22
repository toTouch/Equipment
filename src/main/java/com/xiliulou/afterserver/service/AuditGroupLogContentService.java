package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.entity.AuditGroupLogContent;

/**
 * @author zgw
 * @date 2022/6/22 17:44
 * @mood
 */
public interface AuditGroupLogContentService extends IService<AuditGroupLogContent> {

    public boolean saveOne(Long logId, String entryName, Integer entryType, String value);
}
