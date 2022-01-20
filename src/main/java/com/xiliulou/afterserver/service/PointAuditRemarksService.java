package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.PointAuditRemarks;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.util.R;

public interface PointAuditRemarksService extends IService<PointAuditRemarks> {

    R saveOne(String remarks);

    R getList();

    R deleteOne(Long id);

    R updateOne(String remarks, Long id);
}
