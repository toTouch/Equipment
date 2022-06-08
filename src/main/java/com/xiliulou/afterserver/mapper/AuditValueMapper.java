package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.AuditValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:38
 * @mood
 */
public interface AuditValueMapper extends BaseMapper<AuditValue> {

    Long getCountByEntryIdsAndPid(@Param("entryIds") List<Long> entryIds, @Param("productNewId")Long productNewId, @Param("required")Integer required);
}
