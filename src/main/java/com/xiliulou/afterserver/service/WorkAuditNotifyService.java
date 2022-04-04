package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.entity.WorkAuditNotify;
import com.xiliulou.afterserver.util.R;

/**
 * @author zgw
 * @date 2022/4/2 17:09
 * @mood
 */
public interface WorkAuditNotifyService extends IService<WorkAuditNotify> {
    R queryList(Integer size, Integer offset, Integer type);

    R queryCount(Integer type);

    R readNotify(Long id);

    R readNotifyAll();
}
