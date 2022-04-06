package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.DeliverLog;
import com.xiliulou.afterserver.mapper.DeliverLogMapper;
import com.xiliulou.afterserver.mapper.DeliverMapper;
import com.xiliulou.afterserver.service.DeliverLogService;
import com.xiliulou.afterserver.service.DeliverService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/21 9:11
 * @mood
 */
@Service
public class DeliverLogServiceImpl extends ServiceImpl<DeliverLogMapper, DeliverLog> implements DeliverLogService {

    @Override
    public List<DeliverLog> getByDeliverId(Long deliverId) {
        return this.baseMapper.selectList(new QueryWrapper<DeliverLog>().eq("deliver_id", deliverId));
    }
}
