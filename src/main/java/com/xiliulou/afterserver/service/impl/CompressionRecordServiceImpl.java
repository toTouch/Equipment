package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.CompressionRecord;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.mapper.CompressionRecordMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.CompressionRecordService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("compressionRecordService")
@Slf4j
public class CompressionRecordServiceImpl extends ServiceImpl<CompressionRecordMapper, CompressionRecord> implements
        CompressionRecordService {

    @Autowired
    private CompressionRecordMapper compressionRecordMapper;
    @Override
    public R queryRecordList(Long id) {
        return R.ok(compressionRecordMapper.queryListByPid(id));
    }
}
