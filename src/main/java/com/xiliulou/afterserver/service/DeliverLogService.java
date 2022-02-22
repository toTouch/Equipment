package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.DeliverLog;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/21 9:10
 * @mood
 */
public interface DeliverLogService extends IService<DeliverLog> {

    List<DeliverLog> getByDeliverId(Long id);
}
