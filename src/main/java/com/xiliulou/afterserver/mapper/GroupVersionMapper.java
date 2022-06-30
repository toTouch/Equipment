package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.GroupVersion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/23 15:52
 * @mood
 */
public interface GroupVersionMapper extends BaseMapper<GroupVersion> {

    @Select("select v.id, v.group_id, v.version, v.group_name from t_group_version v left join t_audit_group g on v.group_id = g.id where g.process_id = #{ProcessId}")
    List<GroupVersion> selectByProcessId(@Param("ProcessId") Long ProcessId);
}
