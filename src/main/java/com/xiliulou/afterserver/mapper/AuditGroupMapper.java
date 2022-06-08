package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.AuditGroup;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:40
 * @mood
 */
public interface AuditGroupMapper extends BaseMapper<AuditGroup> {
    @Select("select id, name, entry_ids, sort, process_id from t_audit_group where process_id = #{id} order by sort")
    List<AuditGroup> getByProcessId(Long id);
}
