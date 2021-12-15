package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Delete("delete from t_role_permission where rid= #{rid}")
      int deleteByRid(@Param("rid")Long rid);

}
