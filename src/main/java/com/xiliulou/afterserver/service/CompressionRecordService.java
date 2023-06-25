package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.CompressionRecord;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.util.R;

public interface CompressionRecordService extends IService<CompressionRecord> {

    R queryRecordList(Long id);
}
