package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.AuditValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:38
 * @mood
 */
public interface AuditValueMapper extends BaseMapper<AuditValue> {

    Long getCountByEntryIdsAndPid(@Param("entryIds") List<Long> entryIds, @Param("productNewId")Long productNewId, @Param("required")Integer required);

    @Select("select id, pid, entry_id, value, update_time, create_time from t_audit_value where entry_id = #{entryId} and pid = #{pid}")
    AuditValue selectByEntryId(@Param("entryId")Long entryId,  @Param("pid")Long pid);
}
