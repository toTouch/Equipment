package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
     /**添加角色*/
     Integer addRole(Role role);
     /**修改角色*/
     Integer update(Role role);
     /**根据id查找一行角色*/
     Role findRoleById(Long id);
     /**查找所有的角色*/
     List<Role> findRoleAll();

     @Select("select id,name,create_time,update_time from t_role where name=#{name}")
     Role findByName(@Param("name") String name);

     List<Role> queryByRoleIds(@Param("list") List<Long> roleIds);
}
