package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.KeyProcessQuery;
import com.xiliulou.afterserver.web.vo.AuditProcessVo;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 18:26
 * @mood
 */
public interface AuditProcessService extends IService<AuditProcess> {
    AuditProcess getByType(String type);

    AuditProcessVo createTestAuditProcessVo();

    AuditProcessVo createDeliverAuditProcessVo();

    Integer getAuditProcessStatus(AuditProcess item, ProductNew productNew);

    public void processStatusAdjustment(List<AuditProcessVo> auditProcessVos);

    R getKeyProcess(String no, String type, Long groupId);

    R putKeyProcess(KeyProcessQuery keyProcessQuery);
}
