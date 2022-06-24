package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditGroup;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditGroupStrawberryQuery;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditGroupVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 18:23
 * @mood
 */
public interface AuditGroupService extends IService<AuditGroup> {

    List<AuditGroup> getByProcessId(Long id);

    AuditGroup getByName(String name);

    AuditGroup getBySort(BigDecimal sort,Long processId);

    Integer getGroupStatus(AuditGroup auditGroup, Long productNewId, Integer preStatus);

    KeyProcessAuditGroupVo statusAdjustment(List<KeyProcessAuditGroupVo> keyProcessAuditGroupVos);

    R queryList(String type);

    R saveOne(AuditGroupStrawberryQuery auditGroupStrawberryQuery);

    R putOne(AuditGroupStrawberryQuery auditGroupStrawberryQuery);

    R reomveOne(Long id);
}
