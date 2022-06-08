package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditGroup;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditGroupVo;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 18:23
 * @mood
 */
public interface AuditGroupService extends IService<AuditGroup> {

    List<AuditGroup> getByProcessId(Long id);

    Integer getGroupStatus(AuditGroup auditGroup, Long ProductNewId);

    KeyProcessAuditGroupVo statusAdjustment(List<KeyProcessAuditGroupVo> keyProcessAuditGroupVos);
}
