package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.CompressionRecord;
import com.xiliulou.afterserver.entity.DeviceApplyCounter;

public interface DeviceApplyCounterMapper extends BaseMapper<DeviceApplyCounter> {
    int insertOrUpdate(DeviceApplyCounter deviceApplyCounter);

    DeviceApplyCounter queryByDateAndType(DeviceApplyCounter deviceApplyCounter);
}
