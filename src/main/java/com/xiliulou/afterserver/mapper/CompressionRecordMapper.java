package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.CompressionRecord;

import java.util.List;

public interface CompressionRecordMapper  extends BaseMapper<CompressionRecord> {

    CompressionRecord queryEleByPid(Long pid);

    CompressionRecord queryCompressionByPid(Long pid);

    List<CompressionRecord> queryListByPid(Long pid);
}
